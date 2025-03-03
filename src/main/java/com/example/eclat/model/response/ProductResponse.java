package com.example.eclat.model.response;

import com.example.eclat.entities.Brand;
import com.example.eclat.entities.Image;
import com.example.eclat.entities.SkinType;
import com.example.eclat.entities.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    com.example.eclat.entities.Tag tag;
    Brand brand;
    SkinType skinType;
    List<OptionResponse> options;
    List<String> images;
    String attribute;


}

