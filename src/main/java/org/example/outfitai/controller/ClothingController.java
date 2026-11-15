package org.example.outfitai.controller;

import org.example.outfitai.model.ClothingItem;
import org.example.outfitai.repository.ClothingItemRepository;
import org.example.outfitai.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClothingController {

    @Autowired
    ClothingItemRepository repository;

    @Autowired
    AiService aiService;

    // Hent alt toej
    @GetMapping("/clothes")
    public List<ClothingItem> getAll() {
        return repository.findAll();
    }

    // Upload et billede -> AI'en beskriver det -> gem i databasen
    @PostMapping("/clothes")
    public ClothingItem upload(@RequestParam("image") MultipartFile image) throws IOException {
        AiService.ClothingInfo info = aiService.describeClothing(image.getBytes(), image.getContentType());

        ClothingItem item = new ClothingItem();
        item.setImage(image.getBytes());
        item.setImageType(image.getContentType());
        item.setCategory(info.category());
        item.setColor(info.color());
        item.setDescription(info.description());
        return repository.save(item);
    }

    // Vis billedet (bruges i <img src="/api/clothes/1/image">)
    @GetMapping("/clothes/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id) {
        ClothingItem item = repository.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(item.getImageType()))
                .body(item.getImage());
    }

    // Slet et stykke toej
    @DeleteMapping("/clothes/{id}")
    public void delete(@PathVariable int id) {
        repository.deleteById(id);
    }

    // Lav et outfit: { "occasion": "skole, 8 grader og regn" }
    @PostMapping("/outfit")
    public Map<String, Object> makeOutfit(@RequestBody Map<String, String> request) {
        List<ClothingItem> clothes = repository.findAll();
        AiService.Outfit outfit = aiService.makeOutfit(clothes, request.get("occasion"));

        // Find det toej AI'en valgte
        List<ClothingItem> chosen = repository.findAllById(outfit.ids());
        return Map.of("items", chosen, "reason", outfit.reason());
    }
}
