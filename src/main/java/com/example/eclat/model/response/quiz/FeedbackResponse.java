package com.example.eclat.model.response.quiz;

import com.example.eclat.model.response.ProductResponseV2;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    Long feedbackId;
    String text;
    String userId;
    String username;
    int rating;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime createAt;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime update_at;

    Long orderDetailId;

    ProductResponseV2 product; // Đổi kiểu dữ liệu từ ProductResponse -> ProductResponseV2
}
