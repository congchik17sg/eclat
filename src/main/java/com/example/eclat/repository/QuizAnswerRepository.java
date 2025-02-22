package com.example.eclat.repository;

import com.example.eclat.entities.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer , Long> {


    List<QuizAnswer> findAllByQuizQuestion_Id(Long quizQuestionId);

}
