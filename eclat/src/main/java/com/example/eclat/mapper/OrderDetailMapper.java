package com.example.eclat.mapper;


import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.ProductOption;
import com.example.eclat.model.request.OrderDetailRequest;
import com.example.eclat.model.response.OrderDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(target = "productOption", expression = "java(getProductOption(request.getOptionId()))")
    OrderDetail toEntity(OrderDetailRequest request);

    default ProductOption getProductOption(Long optionId) {
        if (optionId == null) {
            return null;
        }
        ProductOption productOption = new ProductOption();
        productOption.setOptionId(optionId);
        return productOption;
    }

    @Mapping(target = "optionId", expression = "java(orderDetail.getProductOption() != null ? orderDetail.getProductOption().getOptionId() : null)")
    OrderDetailResponse toResponse(OrderDetail orderDetail);
}

