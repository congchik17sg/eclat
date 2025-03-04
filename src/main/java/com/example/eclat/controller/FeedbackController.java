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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public ApiResponse<List<FeedbackResponse>> getAllFeedback() {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getAllFeedback())
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<FeedbackResponse>> getFeedbackByUserId(@PathVariable String userId) {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getFeedbackByUserId(userId))
                .build();
    }

    @PutMapping("/{feedbackId}")
    public ApiResponse<FeedbackResponse> updateFeedbackById(@PathVariable Long feedbackId, @RequestBody FeedbackRequest request) {
        return ApiResponse.<FeedbackResponse>builder()
                .result(feedbackService.updateFeedbackById(feedbackId, request))
                .build();
    }

    @DeleteMapping("/{feedbackId}")
    public ApiResponse<String> deleteFeedbackById(@PathVariable Long feedbackId) {
        feedbackService.deleteFeedbackById(feedbackId);
        return ApiResponse.<String>builder()
                .result("Feedback deleted successfully")
                .build();
    }
    @GetMapping("/product/{productId}")
    public ApiResponse<List<FeedbackResponse>> getFeedbackByProductId(@PathVariable Long productId) {
        return ApiResponse.<List<FeedbackResponse>>builder()
                .result(feedbackService.getFeedbackByProductId(productId))
                .build();
    }



}
