package com.example.eclat.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// DTO này tạo theo yêu cầu response của Lam Hoàng
public class FeedbackResponseV3 {
    Long feedbackId;
    String text;
    String userId;
    String username;
    int rating;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "Asia/Ho_Chi_Minh")
    LocalDateTime update_at;

    OrderDetailResponse orderDetail;
    ProductResponseV3 product;
}
