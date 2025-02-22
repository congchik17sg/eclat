package com.example.eclat.controller;


import com.example.eclat.entities.SkinType;
import com.example.eclat.model.request.quiz.QuizSubmitRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.quiz.QuizQuestionResponse;
import com.example.eclat.service.QuizAnswerService;
import com.example.eclat.service.QuizQuestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/quiz")
@Slf4j
@Tag(name = "Quiz API", description = "API for managing quiz")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizQuestionController {

    @Autowired
    QuizQuestionService quizService;

    @Autowired
    QuizAnswerService quizAnswerService;

    @PostMapping(value = "/create-quiz", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<QuizQuestionResponse> createQuiz(
            @RequestParam("question_text") String questionText,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("question_text = " + questionText);

        return ApiResponse.<QuizQuestionResponse>builder()
                .result(quizService.createQuiz(questionText, file))
                .build();
    }


    @GetMapping
    ApiResponse<List<QuizQuestionResponse>> getAllQuiz() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<QuizQuestionResponse>>builder()
                .result(quizService.getAllQuiz())
                .build();
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<QuizQuestionResponse> updateQuiz(
            @PathVariable("id") Long id,
            @RequestParam("question_text") String questionText,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("Update - question_text = " + questionText);

        return ApiResponse.<QuizQuestionResponse>builder()
                .result(quizService.updateQuiz(id, questionText, file))
                .build();
    }


    @DeleteMapping("/{Id}")
    ApiResponse<String> deleteUser(@PathVariable Long Id) {
        quizService.deleteQuizById(Id);
        return ApiResponse.<String>builder().result("Quiz has been deleted").build();
    }
    @DeleteMapping("/{id}/image")
    public ApiResponse<QuizQuestionResponse> deleteQuizImage(@PathVariable("id") Long id) {
        return ApiResponse.<QuizQuestionResponse>builder()
                .result(quizService.deleteQuizImage(id))
                .build();
    }



    @PostMapping("/submit")
    public ResponseEntity<String> submitQuiz(@RequestBody QuizSubmitRequest payload) {
        try {
            // Lấy danh sách câu trả lời
            List<Long> selectedAnswers = payload.getAnswers();

            // Lấy userId từ payload
            String userIdString = payload.getUserId();

            // Xác định loại da từ câu trả lời
            SkinType skinType = quizService.determineSkinType(selectedAnswers);

            if (skinType != null) {
                // Lưu kết quả và trả về phản hồi
                quizService.saveQuizResult(userIdString, skinType);
                return ResponseEntity.ok("Your skin type is: " + skinType.getSkinName());
            } else {
                return ResponseEntity.badRequest().body("Could not determine skin type.");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid user ID format.");
        } catch (ClassCastException e) {
            return ResponseEntity.badRequest().body("Invalid answers format.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/details")
    public ApiResponse<QuizQuestionResponse> getQuizQuestionWithAllAnswers(@PathVariable("id") Long id) {
        return ApiResponse.<QuizQuestionResponse>builder()
                .result(quizAnswerService.getQuizQuestionWithAllAnswer(id))
                .build();
    }





}
