package com.example.eclat.service;


import com.example.eclat.entities.QuizAnswer;
import com.example.eclat.entities.QuizQuestion;
import com.example.eclat.entities.SkinType;
import com.example.eclat.mapper.QuizAnswerMapper;
import com.example.eclat.model.request.quiz.QuizAnswerRequest;
import com.example.eclat.model.response.quiz.QuizAnswerResponse;
import com.example.eclat.model.response.quiz.QuizQuestionResponse;
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


    public QuizAnswerResponse getQuizAnswerById(Long id) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with ID: " + id));

        return QuizAnswerResponse.builder()
                .id(quizAnswer.getId())
                .answerText(quizAnswer.getAnswerText())
                .questionId(quizAnswer.getQuizQuestion().getId())
                .questionText(quizAnswer.getQuizQuestion().getQuestionText())
                .skinTypeId(quizAnswer.getSkinType() != null ? quizAnswer.getSkinType().getId() : null)
                .skinName(quizAnswer.getSkinType() != null ? quizAnswer.getSkinType().getSkinName() : null)
                .skinDescription(quizAnswer.getSkinType() != null ? quizAnswer.getSkinType().getDescription() : null)
                .build();
    }


    public QuizAnswerResponse updateAnswer(Long id, QuizAnswerRequest request) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with ID: " + id));

        if (request.getAnswerText() != null) quizAnswer.setAnswerText(request.getAnswerText());

        if (request.getQuizQuestionId() != null) {
            QuizQuestion quizQuestion = quizQuestionRepository.findById(request.getQuizQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found with ID: " + request.getQuizQuestionId()));
            quizAnswer.setQuizQuestion(quizQuestion);
        }

        if (request.getSkinTypeId() != null) {
            SkinType skinType = skinTypeRepository.findById(request.getSkinTypeId())
                    .orElseThrow(() -> new RuntimeException("SkinType not found with ID: " + request.getSkinTypeId()));
            quizAnswer.setSkinType(skinType);
        }

        QuizAnswer updatedAnswer = quizAnswerRepository.save(quizAnswer);

        return QuizAnswerResponse.builder()
                .id(updatedAnswer.getId())
                .answerText(updatedAnswer.getAnswerText())
                .questionId(updatedAnswer.getQuizQuestion().getId())
                .questionText(updatedAnswer.getQuizQuestion().getQuestionText())
                .skinTypeId(updatedAnswer.getSkinType() != null ? updatedAnswer.getSkinType().getId() : null)
                .skinName(updatedAnswer.getSkinType() != null ? updatedAnswer.getSkinType().getSkinName() : null)
                .skinDescription(updatedAnswer.getSkinType() != null ? updatedAnswer.getSkinType().getDescription() : null)
                .build();
    }

    public void deleteAnswer(Long id) {
        QuizAnswer quizAnswer = quizAnswerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Answer not found with ID: " + id));
        quizAnswerRepository.delete(quizAnswer);
        log.info("Deleted answer with ID: {}", id);


    }

    public QuizQuestionResponse getQuizQuestionWithAllAnswer(Long questionId) {
        QuizQuestion quizQuestion = quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("QuizQuestion not found with ID: " + questionId));

        List<QuizAnswerResponse> answers = quizAnswerRepository.findAllByQuizQuestion_Id(questionId).stream()
                .map(answer -> QuizAnswerResponse.builder()
                        .id(answer.getId())
                        .answerText(answer.getAnswerText())
                        .questionId(quizQuestion.getId())
                        .questionText(quizQuestion.getQuestionText())
                        .skinTypeId(answer.getSkinType() != null ? answer.getSkinType().getId() : null)
                        .skinName(answer.getSkinType() != null ? answer.getSkinType().getSkinName() : null)
                        .skinDescription(answer.getSkinType() != null ? answer.getSkinType().getDescription() : null)
                        .build())
                .toList();

        return QuizQuestionResponse.builder()
                .id(String.valueOf(quizQuestion.getId()))
                .question_text(quizQuestion.getQuestionText())
                .create_at(quizQuestion.getCreateAt())
                .update_at(quizQuestion.getUpdateAt())
                .img_url(quizQuestion.getImg_url())
                .answers(answers)
                .build();
    }
}
