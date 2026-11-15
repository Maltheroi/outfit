package org.example.outfitai.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.example.outfitai.model.ClothingItem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.json.JsonMapper;

import java.util.Base64;
import java.util.List;
import java.util.Map;

// Al snak med AI'en (Google Gemini) sker her - via WebClient, ligesom i WebClientE25A.
@Service
public class AiService {

    // AI'ens rolle - sendes med i alle kald
    private static final String ROLE = "Du er en personlig stylist. Du taler kun om tøj, farver og stil. Svar på dansk.";

    // Det AI'en svarer, naar den har kigget paa et billede
    public record ClothingInfo(String category, String color, String description) {}

    // Det AI'en svarer, naar den har lavet et outfit
    public record Outfit(List<Integer> ids, String reason) {}

    private final WebClient webClient;
    private final JsonMapper jsonMapper;
    private final String model;

    public AiService(JsonMapper jsonMapper,
                     @Value("${ai.api-key}") String apiKey,
                     @Value("${ai.model}") String model) {
        this.jsonMapper = jsonMapper;
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key", apiKey)
                .codecs(c -> c.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)) // plads til billeder
                .build();
    }

    // 1) AI'en kigger paa et billede og beskriver toejet
    public ClothingInfo describeClothing(byte[] image, String imageType) {
        String prompt = """
                Beskriv tøjet på billedet. Svar med JSON:
                {"category": "TOP, BUKSER, SKO, OVERTØJ eller ACCESSORY",
                 "color": "hovedfarven",
                 "description": "kort beskrivelse, max 8 ord"}
                """;

        String imageBase64 = Base64.getEncoder().encodeToString(image);
        List<Object> parts = List.of(
                Map.of("inline_data", Map.of("mime_type", imageType, "data", imageBase64)),
                Map.of("text", prompt));

        String json = askGemini(parts);
        return jsonMapper.readValue(json, ClothingInfo.class);
    }

    // 2) AI'en laver et outfit ud fra garderoben
    public Outfit makeOutfit(List<ClothingItem> clothes, String occasion) {
        // Garderoben laves om til tekst, fx "id=3: TOP, sort - Sort t-shirt"
        String wardrobe = "";
        for (ClothingItem c : clothes) {
            wardrobe += "id=" + c.getId() + ": " + c.getCategory() + ", " + c.getColor()
                    + " - " + c.getDescription() + "\n";
        }

        String prompt = "Her er min garderobe:\n" + wardrobe
                + "\nLav ét outfit til: " + occasion
                + """

                Brug kun tøj fra listen. Svar med JSON:
                {"ids": [1, 2, 3], "reason": "2 sætninger om hvorfor det passer sammen"}
                """;

        String json = askGemini(List.of(Map.of("text", prompt)));
        return jsonMapper.readValue(json, Outfit.class);
    }

    // Selve kaldet til Gemini: POST /v1beta/models/{model}:generateContent
    private String askGemini(List<Object> parts) {
        Map<String, Object> body = Map.of(
                "system_instruction", Map.of("parts", List.of(Map.of("text", ROLE))),
                "contents", List.of(Map.of("parts", parts)),
                "generationConfig", Map.of("responseMimeType", "application/json"));

        GeminiResponse response = webClient.post()
                .uri("/v1beta/models/{model}:generateContent", model)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();   // vent paa svaret

        // Svaret ligger i: candidates[0].content.parts[0].text
        return response.candidates().get(0).content().parts().get(0).text();
    }

    // Geminis svar som Java-klasser (vi tager kun de felter vi skal bruge)
    @JsonIgnoreProperties(ignoreUnknown = true)
    record GeminiResponse(List<Candidate> candidates) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Candidate(Content content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Content(List<Part> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Part(String text) {}
}
