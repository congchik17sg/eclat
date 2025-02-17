package com.example.eclat.controller;

import com.example.eclat.model.request.quiz.FeedbackRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.quiz.FeedbackResponse;
import com.example.eclat.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Feedback API", description = "Comment section api")
public class FeedbackController {

    FeedbackService feedbackService;

    @PostMapping
    public ApiResponse<FeedbackResponse> createFeedback(@RequestBody FeedbackRequest request) {

        return ApiResponse.<FeedbackResponse>builder()
                .result((feedbackService.createFeedback(request)))
                .build();
    }

}
