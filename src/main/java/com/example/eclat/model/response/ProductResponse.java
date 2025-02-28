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

        List<FeedbackResponse> feedbacks;

        public ProductResponse(Long productId, String productName, String description, String usageInstruct, String originCountry,
                               LocalDateTime createAt, LocalDateTime updateAt, Boolean status, Long tagId, Long brandId, Long skinTypeId,
                               List<OptionResponse> options, List<Image> productImages, String attribute, List<FeedbackResponse> feedbacks) {
            this.productId = productId;
            this.productName = productName;
            this.description = description;
            this.usageInstruct = usageInstruct;
            this.originCountry = originCountry;
            this.createAt = createAt;
            this.updateAt = updateAt;
            this.status = status;
            this.tagId = tagId;
            this.brandId = brandId;
            this.skinTypeId = skinTypeId;
            this.options = options;
            this.productImages = productImages;
            this.attribute = attribute;
            this.feedbacks = feedbacks; // Gán danh sách feedback vào response
        }

        // Getter và Setter
    }

