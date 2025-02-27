package com.example.eclat.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Getter
@Setter
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    Long feedbackId;
    String text;
    String userId;
    String username;
    int rating;

    public FeedbackResponse(Long feedbackId, String text, String userId, String username, int rating) {
        this.feedbackId = feedbackId;
        this.text = text;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
    }

    // Getter và Setter
}
