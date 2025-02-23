package com.example.eclat.repository;

import com.example.eclat.entities.QuizAnswer;
import com.example.eclat.entities.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    boolean existsByQuestionText(String questionText);

    Optional<QuizQuestion> findByQuestionText(String questionText);

}


