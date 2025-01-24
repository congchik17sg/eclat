package com.example.eclat.repository;

import com.example.eclat.entities.Category;
import com.example.eclat.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCategoryName(String username);

}
