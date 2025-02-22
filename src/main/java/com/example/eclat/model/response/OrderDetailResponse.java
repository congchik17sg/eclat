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
    Order order;
    ProductOption productOption;  // Liên kết với bảng ProductOption
    LocalDateTime orderDate;  // Ngày đặt hàng
    int quantity;  // Số lượng sản phẩm
    BigDecimal price;  // Giá của từng sản phẩm
    Long optionId;

}
