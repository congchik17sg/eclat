
package com.example.eclat.repository;

import com.example.eclat.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProduct_ProductId(Long productId);
    List<Image> findByOption_OptionId(Long optionId);
}

