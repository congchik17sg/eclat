package com.example.eclat.repository;

import com.example.eclat.entities.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {}
