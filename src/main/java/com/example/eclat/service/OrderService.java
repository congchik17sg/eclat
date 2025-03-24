package com.example.eclat.service;


import com.example.eclat.entities.*;
import com.example.eclat.mapper.OrderDetailMapper;
import com.example.eclat.mapper.OrderMapper;
import com.example.eclat.model.request.OrderRequest;
import com.example.eclat.model.response.OptionResponse;
import com.example.eclat.model.response.OrderDetailResponse;
import com.example.eclat.model.response.OrderResponse;
import com.example.eclat.model.response.ProductResponse;
import com.example.eclat.repository.*;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
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
    TransactionRepository transactionRepository;

//    @Transactional
//    public OrderResponse createOrder(OrderRequest request) {
//        // 1. Tìm user theo ID
//        User user = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found!"));
//
//        // 2. Xác định status dựa vào paymentMethod
//        String paymentMethod = request.getPaymentMethod().trim().toLowerCase();
//        String status = paymentMethod.equals("cash") ? "SUCCESS" : "PENDING";
//
//        // 3. Tạo đối tượng Order
//        Order order = Order.builder()
//                .user(user)
//                .totalPrices(request.getTotalPrices())
//                .address(request.getAddress())
//                .status(status) // ✅ Set status tự động
//                .paymentMethod(request.getPaymentMethod())
//                .createAt(LocalDateTime.now())
//                .updateAt(LocalDateTime.now())
//                .build();
//
//        // 💾 4. Lưu order vào database trước
//        order = orderRepository.save(order);
//
//        // 5. Thêm danh sách OrderDetail vào đơn hàng
//        Order finalOrder = order;
//        List<OrderDetail> orderDetails = request.getOrderDetails().stream()
//                .map(detailRequest -> {
//                    ProductOption option = productOptionRepository.findById(detailRequest.getOptionId())
//                            .orElseThrow(() -> new RuntimeException("Option not found!"));
//
//                    return OrderDetail.builder()
//                            .order(finalOrder)
//                            .productOption(option)
//                            .quantity(detailRequest.getQuantity())
//                            .price(detailRequest.getPrice())
//                            .orderDate(LocalDateTime.now())
//                            .build();
//                }).collect(Collectors.toList());
//
//        // 💾 6. Lưu danh sách OrderDetail
//        orderDetailRepository.saveAll(orderDetails);
//
//        // 7. Nếu paymentMethod là "cash", cập nhật quantity của ProductOption
//        if ("cash".equalsIgnoreCase(request.getPaymentMethod())) {
//            for (OrderDetail detail : orderDetails) {
//                ProductOption option = detail.getProductOption();
//                int newQuantity = option.getQuantity() - detail.getQuantity();
//                if (newQuantity < 0) {
//                    throw new RuntimeException("Not enough stock for product option: " + option.getOptionId());
//                }
//                option.setQuantity(newQuantity);
//                productOptionRepository.save(option);
//            }
//        }
//
//        // 8. Chuyển đổi sang Response và trả về
//        return OrderResponse.builder()
//                .orderId(order.getOrderId())
//                .totalPrices(order.getTotalPrices())
//                .address(order.getAddress())
//                .status(order.getStatus()) // ✅ Trả về status đã được cập nhật tự động
//                .paymentMethod(order.getPaymentMethod())
//                .createAt(order.getCreateAt())
//                .updateAt(order.getUpdateAt())
//                .orderDetails(orderDetails.stream().map(orderDetailMapper::toResponse).collect(Collectors.toList()))
//                .build();
//    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Tìm user theo ID
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // 2. Xác định status dựa vào paymentMethod
        String paymentMethod = request.getPaymentMethod().trim().toLowerCase();
        String orderStatus = paymentMethod.equals("cash") ? "SUCCESS" : "PENDING";

        // 3. Tạo Order
        Order order = Order.builder()
                .user(user)
                .totalPrices(request.getTotalPrices())
                .address(request.getAddress())
                .status(orderStatus) // ✅ Set trạng thái tự động
                .paymentMethod(request.getPaymentMethod())
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        // 4. Lưu order vào database trước
        Order savedOrder = orderRepository.save(order); // Đặt tên khác để tránh vấn đề scope

// 5. Tạo danh sách OrderDetail
        List<OrderDetail> orderDetails = request.getOrderDetails().stream()
                .map(detailRequest -> {
                    ProductOption option = productOptionRepository.findById(detailRequest.getOptionId())
                            .orElseThrow(() -> new RuntimeException("Option not found!"));

                    return OrderDetail.builder()
                            .order(savedOrder) // Sử dụng biến mới
                            .productOption(option)
                            .quantity(detailRequest.getQuantity())
                            .price(detailRequest.getPrice())
                            .orderDate(LocalDateTime.now())
                            .build();
                }).collect(Collectors.toList());


        // 💾 6. Lưu danh sách OrderDetail
        orderDetailRepository.saveAll(orderDetails);

        // 7. Nếu paymentMethod là "cash", trừ sản phẩm trong kho ngay
        if ("cash".equalsIgnoreCase(paymentMethod)) {
            for (OrderDetail detail : orderDetails) {
                ProductOption option = detail.getProductOption();
                int newQuantity = option.getQuantity() - detail.getQuantity();
                if (newQuantity < 0) {
                    throw new RuntimeException("Not enough stock for product option: " + option.getOptionId());
                }
                option.setQuantity(newQuantity);
                productOptionRepository.save(option);
            }
        }
        // 8. Nếu thanh toán bằng VNPAY, tạo Transaction với trạng thái PENDING
        else if ("vnpay".equalsIgnoreCase(paymentMethod)) {
            Transaction transaction = Transaction.builder()
                    .order(order)
                    .amount(order.getTotalPrices())
                    .transactionStatus("PENDING") // ✅ Trạng thái ban đầu là PENDING
                    .createAt(LocalDateTime.now())
                    .expireAt(LocalDateTime.now().plusMinutes(15)) // ✅ Hết hạn sau 15 phút
                    .build();

            transactionRepository.save(transaction);
        }

        // 9. Chuyển đổi sang Response và trả về
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

        return convertToOrderResponse(order); // ✅ Gọi convertToOrderResponse để ánh xạ
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

                    // ✅ Kiểm tra nếu productOption không null
                    ProductResponse productResponse = null;
                    if (productOption != null && productOption.getProduct() != null) {
                        Product product = productOption.getProduct();

                        // ✅ Xử lý danh sách options, tránh bị null
                        List<OptionResponse> optionResponses = product.getOptions() != null
                                ? product.getOptions().stream().map(option -> new OptionResponse(
                                option.getOptionId(),
                                option.getOptionValue(),
                                option.getQuantity(),
                                option.getOptionPrice(),
                                option.getDiscPrice(),
                                option.getCreateAt(),
                                option.getUpdateAt(),
                                option.getOptionImages()
                        )).collect(Collectors.toList())
                                : Collections.emptyList(); // Trả về danh sách rỗng thay vì null

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
                                .options(optionResponses) // ✅ Đảm bảo options không null
                                .build();
                    }

                    // ✅ Tạo OptionResponse có ProductResponse
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
                    optionResponse.setProduct(productResponse);

                    return OrderDetailResponse.builder()
                            .orderDetailId(orderDetail.getOrderDetailId())
                            .quantity(orderDetail.getQuantity())
                            .price(orderDetail.getPrice())
                            .optionId(productOption.getOptionId())
                            .optionResponse(List.of(optionResponse))
                            .build();
                }).collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getId())
                .totalPrices(order.getTotalPrices())
                .address(order.getAddress())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .createAt(order.getCreateAt())
                .updateAt(order.getUpdateAt())
                .orderDetails(orderDetailResponses)
                .build();
    }





}
