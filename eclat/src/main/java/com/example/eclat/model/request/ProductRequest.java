package com.example.eclat.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String productName;

    private String description;
    private String usageInstruct;
    private String originCountry;
    private String attribute;

    @NotNull(message = "Tag ID không được để trống")
    private Long tagId;

    @NotNull(message = "Brand ID không được để trống")
    private Long brandId;

    @NotNull(message = "SkinType ID không được để trống")
    private Long skinTypeId;

    private List<OptionRequest> options; // Danh sách option đi kèm


}
