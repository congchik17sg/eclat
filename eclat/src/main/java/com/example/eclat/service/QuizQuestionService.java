package com.example.eclat.service;


import com.example.eclat.entities.QuizAnswer;
import com.example.eclat.entities.QuizQuestion;
import com.example.eclat.entities.SkinType;
import com.example.eclat.entities.UserQuizResult;
import com.example.eclat.mapper.QuizQuestionMapper;
import com.example.eclat.model.request.quiz.QuizQuestionUpdateRequest;
import com.example.eclat.model.response.quiz.QuizQuestionResponse;
import com.example.eclat.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class QuizQuestionService {

    @Autowired
    QuizQuestionRepository quizQuestionRepository;
    @Autowired
    QuizAnswerRepository quizAnswerRepository;
    @Autowired
    SkinTypeRepository skinTypeRepository;
    @Autowired
    UserQuizResultRepository userQuizResultRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    QuizQuestionMapper quizQuestionMapper;
    public QuizQuestionService(CloudinaryService cloudinaryService,
                               QuizQuestionRepository quizQuestionRepository,
                               QuizQuestionMapper quizQuestionMapper) {
        this.cloudinaryService = cloudinaryService;
        this.quizQuestionRepository = quizQuestionRepository;
        this.quizQuestionMapper = quizQuestionMapper;
    }


    public QuizQuestionResponse createQuiz(String questionText, MultipartFile file) {
        String imageUrl = (file != null && !file.isEmpty())
                ? cloudinaryService.uploadFile(file)
                : null;

        QuizQuestion quizQuestion = new QuizQuestion();
        quizQuestion.setQuestionText(questionText);
        quizQuestion.setImg_url(imageUrl);
        quizQuestion.setCreateAt(LocalDate.now());
        quizQuestion.setUpdateAt(LocalDate.now());

        quizQuestion = quizQuestionRepository.save(quizQuestion);
        return quizQuestionMapper.toQuizQuestionResponse(quizQuestion);
    }


    //    @PreAuthorize("hasRole('Admin')")
    public List<QuizQuestionResponse> getAllQuiz() {
        return quizQuestionRepository.findAll().stream()
                .map(quizQuestionMapper::toQuizQuestionResponse).toList();
    }

    public QuizQuestionResponse updateQuizById(Long id, QuizQuestionUpdateRequest request) {
        QuizQuestion quizQuestion = quizQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("quiz not found"));
        quizQuestionMapper.updateQuizQuestion(quizQuestion, request);
        return quizQuestionMapper.toQuizQuestionResponse(quizQuestionRepository.save(quizQuestion));
    }

    public void deleteQuizById(Long Id) {
        quizQuestionRepository.deleteById(Id);
    }


    public SkinType determineSkinType(List<Long> selectedAnswerIds) {
        // Fetch all answers by their IDs
        List<QuizAnswer> answers = quizAnswerRepository.findAllById(selectedAnswerIds);

        // Group answers by SkinType and count occurrences
        Map<SkinType, Long> skinTypeCountMap = answers.stream()
                .collect(Collectors.groupingBy(QuizAnswer::getSkinType, Collectors.counting()));

        // Find the SkinType with the highest count
        return skinTypeCountMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null); // Return null if no answers are provided
    }


    public void saveQuizResult(String userId, SkinType skinType) {
        UserQuizResult result = new UserQuizResult();
        result.setUser(userRepository.findById(userId).orElseThrow());
        result.setSkinType(skinType);
        userQuizResultRepository.save(result);
    }

    public List<QuizQuestion> getAllQuestions() {
        return quizQuestionRepository.findAll();
    }


}
