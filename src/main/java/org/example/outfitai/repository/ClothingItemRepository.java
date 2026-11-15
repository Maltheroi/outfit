package org.example.outfitai.repository;

import org.example.outfitai.model.ClothingItem;
import org.springframework.data.jpa.repository.JpaRepository;

// Giver os save, findAll, findById og deleteById - uden at skrive SQL
public interface ClothingItemRepository extends JpaRepository<ClothingItem, Integer> {
}
