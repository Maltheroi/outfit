package org.example.outfitai.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// Et stykke toej. category, color og description bliver skrevet af AI'en.
@Entity
public class ClothingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String category;      // fx TOP, BUKSER, SKO, OVERTØJ
    private String color;         // fx sort
    private String description;   // fx "Sort hoodie med lille logo"

    // Selve billedet gemmes i databasen. @JsonIgnore = sendes ikke med i JSON.
    @JsonIgnore
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @JsonIgnore
    private String imageType;     // fx image/jpeg

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }
}
