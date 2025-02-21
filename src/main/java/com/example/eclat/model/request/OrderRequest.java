package com.example.eclat.model.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private String userId;
    private BigDecimal totalPrices;
    private String address;
    private String status;
    private List<OrderDetailRequest> orderDetails;


}
