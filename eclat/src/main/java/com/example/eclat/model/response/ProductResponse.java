package com.example.eclat.model.response;

import com.example.eclat.entities.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {
    Long productId;
    String productName;
    String description;
    String usageInstruct;
    String originCountry;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime createAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime updateAt;
    Boolean status;

    Long tagId;
    Long brandId;
    Long skinTypeId;

    private List<OptionResponse> options;
    List<Image> productImages;
    String attribute;
}
