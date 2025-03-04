package com.example.eclat.model.response;

import com.example.eclat.model.response.ProductResponseV2;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OptionResponseV2 {
    Long optionId;
    String optionValue;
    int quantity;
    BigDecimal optionPrice;
    BigDecimal discPrice;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime updateAt;

    List<String> optionImages;

    @JsonIgnore // Ngăn vòng lặp khi serialize JSON
    ProductResponseV2 product;
}
