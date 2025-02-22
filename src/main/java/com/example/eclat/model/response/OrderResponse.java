package com.example.eclat.model.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
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


}
