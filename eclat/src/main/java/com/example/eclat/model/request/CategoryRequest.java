package com.example.eclat.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name không được để trống")
    private String categoryName;
    private String description;
}
