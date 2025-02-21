package com.example.eclat.controller;


import com.example.eclat.model.request.quiz.SkinTypeRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.quiz.SkinTypeResponse;
import com.example.eclat.service.SkinTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skintype")
@Slf4j
@Tag(name = "Skin Type API", description = "API for managing skinType")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SkinTypeController {


    @Autowired
    SkinTypeService skinTypeService;

    @PostMapping
    ApiResponse<SkinTypeResponse> createSkinType(@RequestBody SkinTypeRequest request) {
        return ApiResponse.<SkinTypeResponse>builder()
                .result(skinTypeService.createSkinType(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<SkinTypeResponse>> getSkinType() {
        return ApiResponse.<List<SkinTypeResponse>>builder()
                .result(skinTypeService.getSkinType())
                .build();
    }

    @PutMapping("{skintypeId}")
    ApiResponse<SkinTypeResponse> updateSkinType(@PathVariable Long skintypeId, @RequestBody SkinTypeRequest request) {
        return ApiResponse.<SkinTypeResponse>builder()
                .result(skinTypeService.updateSkinType(skintypeId, request))
                .build();
    }

    @DeleteMapping("{skintypeId}")
    ApiResponse<String> deleteSkintype(@PathVariable Long skintypeId) {
        return ApiResponse.<String>builder()
                .result("Skintype Disbled")
                .build();
    }

    @GetMapping("{skintypeId}")
    ApiResponse<SkinTypeResponse> getSkinTypeById(@PathVariable Long skintypeId) {
    return ApiResponse.<SkinTypeResponse>builder()
            .result(skinTypeService.getSkinTypeById(skintypeId))
            .build();
}


}
