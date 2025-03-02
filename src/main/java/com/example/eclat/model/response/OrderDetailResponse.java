package com.example.eclat.model.response;


import com.example.eclat.entities.Order;
import com.example.eclat.entities.ProductOption;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailResponse {


    Long orderDetailId;
    Integer quantity;
    BigDecimal price;
    Long optionId;
}
