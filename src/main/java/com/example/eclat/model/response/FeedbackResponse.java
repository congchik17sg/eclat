package com.example.eclat.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {
    Long feedbackId;
    String text;
    String userId;
    String username;
    int rating;
    LocalDate create_at;
    LocalDate update_at;
    Long orderDetailId;
    ProductResponse product; // Thêm thông tin sản phẩm vào phản hồi
}
