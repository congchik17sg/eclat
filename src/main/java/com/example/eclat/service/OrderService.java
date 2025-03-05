package com.example.eclat.service;


import com.example.eclat.entities.*;
import com.example.eclat.mapper.OrderDetailMapper;
import com.example.eclat.mapper.OrderMapper;
import com.example.eclat.model.request.OrderRequest;
import com.example.eclat.model.response.OptionResponse;
import com.example.eclat.model.response.OrderDetailResponse;
import com.example.eclat.model.response.OrderResponse;
import com.example.eclat.model.response.ProductResponse;
import com.example.eclat.repository.OptionRepository;
import com.example.eclat.repository.OrderDetailRepository;
import com.example.eclat.repository.OrderRepository;
import com.example.eclat.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderService {


    OrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;
    UserRepository userRepository;
    OptionRepository productOptionRepository;
    OrderMapper orderMapper;
    OrderDetailMapper orderDetailMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Tìm user theo ID
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Xác định status dựa vào paymentMethod
        String paymentMethod = request.getPaymentMethod().trim().toLowerCase();
        String status = paymentMethod.equals("cash") ? "SUCCESS" : "PENDING";

        // 3. Tạo đối tượng Order
        Order order = Order.builder()
                .user(user)
                .totalPrices(request.getTotalPrices())
                .address(request.getAddress())
                .status(status) // ✅ Set status tự động
                .paymentMethod(request.getPaymentMethod())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        // 💾 4. Lưu order vào database trước
        order = orderRepository.save(order);

        // 5. Thêm danh sách OrderDetail vào đơn hàng
        Order finalOrder = order;
        List<OrderDetail> orderDetails = request.getOrderDetails().stream()
                .map(detailRequest -> {
                    ProductOption option = productOptionRepository.findById(detailRequest.getOptionId())
                            .orElseThrow(() -> new RuntimeException("Option not found!"));

                    return OrderDetail.builder()
                            .order(finalOrder)
                            .productOption(option)
                            .quantity(detailRequest.getQuantity())
                            .price(detailRequest.getPrice())
                            .orderDate(LocalDateTime.now())
                            .build();
                }).collect(Collectors.toList());

        // 💾 6. Lưu danh sách OrderDetail
        orderDetailRepository.saveAll(orderDetails);

        // 7. Chuyển đổi sang Response và trả về
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalPrices(order.getTotalPrices())
                .address(order.getAddress())
                .status(order.getStatus()) // ✅ Trả về status đã được cập nhật tự động
                .paymentMethod(order.getPaymentMethod())
                .createAt(order.getCreateAt())
                .updateAt(order.getUpdateAt())
                .orderDetails(orderDetails.stream().map(orderDetailMapper::toResponse).collect(Collectors.toList()))
                .build();
    }

    // ham nay de xai trong vnpaycontroller
    public Optional<Order> getOrderByIdV2(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }
    // ✅ Lấy tất cả đơn hàng
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
    }


    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));

        // Ánh xạ danh sách OrderDetailResponse
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream()
                .map(orderDetail -> {
                    ProductOption productOption = orderDetail.getProductOption();

                    // Ánh xạ ProductResponse
                    ProductResponse productResponse = null;
                    if (productOption.getProduct() != null) {
                        Product product = productOption.getProduct();
                        productResponse = ProductResponse.builder()
                                .productId(product.getProductId())
                                .productName(product.getProductName())
                                .description(product.getDescription())
                                .usageInstruct(product.getUsageInstruct())
                                .originCountry(product.getOriginCountry())
                                .createAt(product.getCreateAt())
                                .updateAt(product.getUpdateAt())
                                .status(product.getStatus())
                                .tag(product.getTag())
                                .brand(product.getBrand())
                                .skinType(product.getSkinType())
                                .images(product.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                                .attribute(product.getAttribute())
                                .build();
                    }

                    // Tạo OptionResponse có ProductResponse
                    OptionResponse optionResponse = new OptionResponse(
                            productOption.getOptionId(),
                            productOption.getOptionValue(),
                            productOption.getQuantity(),
                            productOption.getOptionPrice(),
                            productOption.getDiscPrice(),
                            productOption.getCreateAt(),
                            productOption.getUpdateAt(),
                            productOption.getOptionImages()
                    );
                    optionResponse.setProduct(productResponse); // ✅ Gán product vào optionResponse

                    return OrderDetailResponse.builder()
                            .orderDetailId(orderDetail.getOrderDetailId())
                            .quantity(orderDetail.getQuantity())
                            .price(orderDetail.getPrice())
                            .optionId(productOption.getOptionId())
                            .optionResponse(List.of(optionResponse)) // ✅ Thêm OptionResponse vào danh sách
                            .build();
                }).collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getId()) // Đảm bảo lấy userId kiểu String
                .totalPrices(order.getTotalPrices())
                .address(order.getAddress())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createAt(order.getCreateAt())
                .updateAt(order.getUpdateAt())
                .orderDetails(orderDetailResponses) // ✅ Trả về danh sách OrderDetailResponse có OptionResponse
                .build();
    }



    // ✅ Lấy danh sách đơn hàng theo User ID
    public List<OrderResponse> getOrdersByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(this::convertToOrderResponse).collect(Collectors.toList());
    }
    private OrderResponse convertToOrderResponse(Order order) {
        List<OrderDetailResponse> orderDetailResponses = order.getOrderDetails().stream()
                .map(orderDetail -> {
                    ProductOption productOption = orderDetail.getProductOption();

                    // Ánh xạ ProductResponse từ ProductOption
                    ProductResponse productResponse = null;
                    if (productOption.getProduct() != null) {
                        Product product = productOption.getProduct();
                        productResponse = ProductResponse.builder()
                                .productId(product.getProductId())
                                .productName(product.getProductName())
                                .description(product.getDescription())
                                .usageInstruct(product.getUsageInstruct())
                                .originCountry(product.getOriginCountry())
                                .createAt(product.getCreateAt())
                                .updateAt(product.getUpdateAt())
                                .status(product.getStatus())
                                .tag(product.getTag())
                                .brand(product.getBrand())
                                .skinType(product.getSkinType())
                                .images(product.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                                .attribute(product.getAttribute())
                                .build();
                    }

                    // ✅ Tạo OptionResponse và gán ProductResponse vào
                    OptionResponse optionResponse = new OptionResponse(
                            productOption.getOptionId(),
                            productOption.getOptionValue(),
                            productOption.getQuantity(),
                            productOption.getOptionPrice(),
                            productOption.getDiscPrice(),
                            productOption.getCreateAt(),
                            productOption.getUpdateAt(),
                            productOption.getOptionImages()
                    );
                    optionResponse.setProduct(productResponse); // ✅ Đảm bảo ProductResponse không bị null

                    return OrderDetailResponse.builder()
                            .orderDetailId(orderDetail.getOrderDetailId())
                            .quantity(orderDetail.getQuantity())
                            .price(orderDetail.getPrice())
                            .optionId(productOption.getOptionId())
                            .optionResponse(List.of(optionResponse)) // ✅ Thêm OptionResponse vào danh sách
                            .build();
                }).collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getId()) // Đảm bảo lấy userId kiểu String
                .totalPrices(order.getTotalPrices())
                .address(order.getAddress())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createAt(order.getCreateAt())
                .updateAt(order.getUpdateAt())
                .orderDetails(orderDetailResponses) // ✅ Trả về danh sách OrderDetailResponse có OptionResponse
                .build();
    }




}
