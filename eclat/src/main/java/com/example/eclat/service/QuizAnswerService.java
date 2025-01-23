package com.example.eclat.service;


import com.example.eclat.entities.QuizAnswer;
import com.example.eclat.entities.QuizQuestion;
import com.example.eclat.entities.SkinType;
import com.example.eclat.mapper.QuizAnswerMapper;
import com.example.eclat.model.request.quiz.QuizAnswerRequest;
import com.example.eclat.model.response.QuizAnswerResponse;
import com.example.eclat.repository.QuizAnswerRepository;
import com.example.eclat.repository.QuizQuestionRepository;
import com.example.eclat.repository.SkinTypeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class QuizAnswerService {

    QuizAnswerRepository quizAnswerRepository;
    QuizQuestionRepository quizQuestionRepository;
    SkinTypeRepository skinTypeRepository;

    QuizAnswerMapper quizAnswerMapper;

    public QuizAnswerResponse createAnswer(QuizAnswerRequest request) {
        // Tìm câu hỏi theo ID
        QuizQuestion quizQuestion = quizQuestionRepository.findById(request.getQuizQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + request.getQuizQuestionId()));

        // Tìm SkinType theo ID
        SkinType skinType = skinTypeRepository.findById(request.getSkinTypeId())
                .orElseThrow(() -> new RuntimeException("SkinType not found with ID: " + request.getSkinTypeId()));

        // Tạo đối tượng QuizAnswer
        QuizAnswer quizAnswer = QuizAnswer.builder()
                .answerText(request.getAnswerText())
                .quizQuestion(quizQuestion)
                .skinType(skinType)
                .build();

        // Lưu câu trả lời vào database
        QuizAnswer savedAnswer = quizAnswerRepository.save(quizAnswer);

        // Trả về response
        return QuizAnswerResponse.builder()
                .id(savedAnswer.getId())
                .answerText(savedAnswer.getAnswerText())
                .questionId(quizQuestion.getId())
                .questionText(quizQuestion.getQuestionText())
                .skinTypeId(skinType.getId())
                .skinName(skinType.getSkinName())
                .skinDescription(skinType.getDescription())
                .build();
    }

    public List<QuizAnswerResponse> getAllAnswer() {
        return quizAnswerRepository.findAll().stream()
                .map(quizAnswerMapper::toQuizAnswerResponse).toList();
    }


}
