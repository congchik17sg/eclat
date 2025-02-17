package com.example.eclat.mapper;


import com.example.eclat.entities.Order;
import com.example.eclat.model.request.OrderRequest;
import com.example.eclat.model.response.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = OrderDetailMapper.class)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // Chuyển từ Request -> Entity
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "orderDetails", target = "orderDetails")
    Order toEntity(OrderRequest request);

    // Chuyển từ Entity -> Response
    @Mapping(source = "user.id", target = "userId")  // Giữ nguyên kiểu String cho userId
    @Mapping(source = "orderDetails", target = "orderDetails")
    OrderResponse toResponse(Order order);
}

