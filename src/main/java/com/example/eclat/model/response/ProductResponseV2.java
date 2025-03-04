package com.example.eclat.model.response;

import com.example.eclat.entities.Brand;
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
public class ProductResponseV2 {
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
    Tag tag;
    Brand brand;
    SkinType skinType;

    // Đổi kiểu từ List<OptionResponse> thành List<OptionResponseV2>
    List<OptionResponseV2> options;

    List<String> images;
    String attribute;
}
