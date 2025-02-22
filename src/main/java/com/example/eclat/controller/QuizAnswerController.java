package com.example.eclat.controller;


import com.example.eclat.model.request.quiz.QuizAnswerRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.quiz.QuizAnswerResponse;
import com.example.eclat.service.QuizAnswerService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quizAnswer")
@Slf4j
@Tag(name = "Quiz Answer API", description = "API for managing quizAnswer")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class QuizAnswerController {

    QuizAnswerService quizAnswerService;

    @PostMapping
    public ApiResponse<QuizAnswerResponse> createAnswer(@RequestBody QuizAnswerRequest request) {
        //        QuizAnswerResponse response = quizAnswerService.createAnswer(request);
        //        return ResponseEntity.status(HttpStatus.CREATED).body(response);

        return ApiResponse.<QuizAnswerResponse>builder()
                .result(quizAnswerService.createAnswer(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<QuizAnswerResponse>> getQuizAnswer() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<QuizAnswerResponse>>builder()
                .result(quizAnswerService.getAllAnswer())
                .build();
    }

    @GetMapping("/{quizAnswerId}")
    public ApiResponse<QuizAnswerResponse> getQuizAnswerById(@PathVariable Long quizAnswerId) {
        return ApiResponse.<QuizAnswerResponse>builder()
                .result(quizAnswerService.getQuizAnswerById(quizAnswerId))
                .build();
    }


    @PutMapping("{quizAnswerId}")
    ApiResponse<QuizAnswerResponse> updateQuizAnswer(@PathVariable Long quizAnswerId, @RequestBody QuizAnswerRequest request) {
        return ApiResponse.<QuizAnswerResponse>builder()
                .result(quizAnswerService.updateAnswer(quizAnswerId, request))
                .build();
    }

    @DeleteMapping("{quizAnswerId}")
    ApiResponse<String> deleteQuizAnswer(@PathVariable Long quizAnswerId) {
        return ApiResponse.<String>builder()
                .result("Answer has been deleted")
                .build();
    }

}
