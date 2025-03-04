package com.example.eclat.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {

     Long orderId;
     String userId;
     BigDecimal totalPrices;
     String address;
     String status;
     List<OrderDetailResponse> orderDetails;
     String paymentMethod;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
     LocalDateTime createAt;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
     LocalDateTime updateAt;
}
