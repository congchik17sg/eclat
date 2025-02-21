package com.example.eclat.repository;

import com.example.eclat.entities.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<ProductOption, Long> {
    List<ProductOption> findByProduct_ProductId(Long productId);
}