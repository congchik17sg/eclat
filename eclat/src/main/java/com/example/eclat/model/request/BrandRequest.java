package com.example.eclat.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandRequest {
    @NotBlank(message = "Brand name is required")
    private String brandName;
    private String imgUrl;
}

