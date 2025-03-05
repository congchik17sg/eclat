package com.example.eclat.model.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductResponseV3 {
    private Long productId;
    private String productName;
}
