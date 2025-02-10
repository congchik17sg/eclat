package com.example.eclat.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String productName;

    private String description;
    private String usageInstruct;
    private String originCountry;

    @NotNull(message = "Tag ID is required")
    private Long tagId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotNull(message = "SkinType ID is required")
    private Long skinTypeId;

    private String attribute;

}
