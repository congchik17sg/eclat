package com.example.eclat.repository;

import com.example.eclat.entities.Category;
import com.example.eclat.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByTagName(String tagName);

    List<Tag> findByTagNameAndCategory(String tagName, Category category);
}
