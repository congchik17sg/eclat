package com.example.eclat.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OptionRequest {
   // @NotNull
   // private Long productId;

    @NotNull
    private String optionValue;

    @NotNull
    private int quantity;

    @NotNull
    private BigDecimal optionPrice;

    private BigDecimal discPrice;
}
