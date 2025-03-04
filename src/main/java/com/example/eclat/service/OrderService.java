package com.example.eclat.service;


import com.example.eclat.entities.Order;
import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.ProductOption;
import com.example.eclat.entities.User;
import com.example.eclat.mapper.OrderDetailMapper;
import com.example.eclat.mapper.OrderMapper;
import com.example.eclat.model.request.OrderRequest;
import com.example.eclat.model.response.OptionResponse;
import com.example.eclat.model.response.OrderDetailResponse;
import com.example.eclat.model.response.OrderResponse;
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

        // 2. Tạo đối tượng Order
        Order order = Order.builder()
                .user(user)
                .totalPrices(request.getTotalPrices())
                .address(request.getAddress())
                .status(request.getStatus())
                .paymentMethod(request.getPaymentMethod()) // ✅ Thêm paymentMethod
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        // 💾 3. Lưu order vào database trước
        order = orderRepository.save(order);

        // 4. Thêm danh sách OrderDetail vào đơn hàng
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

        // 💾 5. Lưu danh sách OrderDetail
        orderDetailRepository.saveAll(orderDetails);

        // 6. Chuyển đổi sang Response và trả về
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .totalPrices(order.getTotalPrices())
                .address(order.getAddress())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod()) // ✅ Thêm paymentMethod vào response
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

                    // Tạo OptionResponse trực tiếp
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

                    // ✅ Tạo OptionResponse từ ProductOption
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
