package org.example.outfitai.config;

import org.example.outfitai.model.ClothingItem;
import org.example.outfitai.repository.ClothingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

// Koerer automatisk naar appen starter.
// Er databasen tom, laegges 9 stykker toej ind, saa der altid er noget at vise frem.
@Component
public class DemoData implements CommandLineRunner {

    @Autowired
    ClothingItemRepository repository;

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            return;
        }
        add("01-hvid-tshirt.jpg", "TOP", "hvid", "Hvid t-shirt med rund hals");
        add("02-ternet-flannelskjorte.jpg", "TOP", "grå", "Grå og sort ternet flannelskjorte");
        add("03-brun-strik.jpg", "TOP", "brun", "Brun strikket sweater");
        add("05-blaa-jeans.jpg", "BUKSER", "blå", "Blå slim jeans");
        add("06-sorte-jeans.jpg", "BUKSER", "sort", "Sorte jeans");
        add("07-denimjakke.jpg", "OVERTØJ", "blå", "Denimjakke med brun fløjlskrave");
        add("08-hvide-sneakers.jpg", "SKO", "hvid", "Hvide læder-sneakers");
        add("09-brune-stoevler.jpg", "SKO", "brun", "Brune læderstøvler");
        add("10-graa-kasket.jpg", "ACCESSORY", "grå", "Grå kasket");
    }

    private void add(String file, String category, String color, String description) throws Exception {
        ClothingItem item = new ClothingItem();
        item.setImage(new ClassPathResource("demo/" + file).getContentAsByteArray());
        item.setImageType("image/jpeg");
        item.setCategory(category);
        item.setColor(color);
        item.setDescription(description);
        repository.save(item);
    }
}
