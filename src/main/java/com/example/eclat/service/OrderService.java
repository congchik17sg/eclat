package com.example.eclat.service;


import com.example.eclat.entities.Order;
import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.ProductOption;
import com.example.eclat.entities.User;
import com.example.eclat.mapper.OrderDetailMapper;
import com.example.eclat.mapper.OrderMapper;
import com.example.eclat.model.request.OrderRequest;
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
import org.springframework.stereotype.Service;

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

    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

}
