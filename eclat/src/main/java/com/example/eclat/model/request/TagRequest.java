package com.example.eclat.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TagRequest {
    @NotBlank(message = "Tag name không được để trống")
    private String tagName;
    private String description;
    @NotNull(message = "Category ID không được để trống")
    private Long categoryId;
}