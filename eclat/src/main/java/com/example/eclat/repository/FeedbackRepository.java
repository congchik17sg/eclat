package com.example.eclat.repository;

import com.example.eclat.entities.FeedBack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<FeedBack, Long> {

}
