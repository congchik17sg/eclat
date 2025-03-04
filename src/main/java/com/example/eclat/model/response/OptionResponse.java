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
public class OptionResponse {
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

    ProductResponse product;

    public OptionResponse(Long optionId, String optionValue, int quantity, BigDecimal optionPrice,
                          BigDecimal discPrice, LocalDateTime createAt, LocalDateTime updateAt, List<String> images) {
        this.optionId = optionId;
        this.optionValue = optionValue;
        this.quantity = quantity;
        this.optionPrice = optionPrice;
        this.discPrice = discPrice;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.optionImages = images;
    }


    // public <R> OptionResponse(Long optionId, String optionValue, int quantity, BigDecimal optionPrice, BigDecimal discPrice, LocalDateTime createAt, LocalDateTime updateAt, R collect) {
    // }
}
