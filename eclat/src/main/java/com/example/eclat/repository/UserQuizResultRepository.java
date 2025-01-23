package com.example.eclat.repository;

import com.example.eclat.entities.QuizAnswer;
import com.example.eclat.entities.UserQuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserQuizResultRepository extends JpaRepository<UserQuizResult, Long> {
}
