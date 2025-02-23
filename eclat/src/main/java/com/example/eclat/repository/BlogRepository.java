package com.example.eclat.repository;

import com.example.eclat.entities.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface BlogRepository extends JpaRepository<Blog, Long> {}

