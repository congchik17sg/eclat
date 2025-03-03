package com.example.eclat.service;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.ProductOption;
import com.example.eclat.entities.Transaction;
import com.example.eclat.model.response.OptionResponse;
import com.example.eclat.model.response.OrderDetailResponse;
import com.example.eclat.model.response.OrderResponse;
import com.example.eclat.model.response.TransactionResponse;
import com.example.eclat.repository.OptionRepository;
import com.example.eclat.repository.OrderRepository;
import com.example.eclat.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    @Autowired
    private OptionRepository productOptionRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedRate = 60000) // ✅ Chạy mỗi phút
    public void cancelExpiredTransactions() {
        LocalDateTime now = LocalDateTime.now();
        List<Transaction> expiredTransactions = transactionRepository.findByTransactionStatusAndExpireAtBefore("PENDING", now);

        for (Transaction transaction : expiredTransactions) {
            transaction.setTransactionStatus("CANCELED");
            transactionRepository.save(transaction);
            System.out.println("⚠️ Giao dịch " + transaction.getVnpTxnRef() + " đã bị hủy do quá thời gian!");
        }
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public Optional<TransactionResponse> getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId).map(this::mapToTransactionResponse);
    }

    public List<TransactionResponse> getTransactionsByUserId(String userId) {
        return transactionRepository.findByOrder_User_Id(userId).stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .order(mapToOrderResponse(transaction.getOrder()))
                .amount(transaction.getAmount()) // Đổi kiểu dữ liệu thành BigDecimal
                .status(transaction.getTransactionStatus())
                .createAt(transaction.getCreateAt())
                .updateAt(transaction.getExpireAt()) // Đổi sang getUpdatedAt() nếu cần
                .build();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getId())
                .totalPrices(order.getTotalPrices())
                .address(order.getAddress())
                .status(order.getStatus())
                .orderDetails(order.getOrderDetails().stream()
                        .map(this::mapToOrderDetailResponse)
                        .collect(Collectors.toList()))
                .build();
    }

//    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail orderDetail) {
//        return OrderDetailResponse.builder()
//                .orderDetailId(orderDetail.getOrderDetailId())
//                .quantity(orderDetail.getQuantity())
//                .price(orderDetail.getPrice())
//                .optionId(orderDetail.getProductOption() != null ? orderDetail.getProductOption().getOptionId() : null)
//                .build();
//    }


    private OrderDetailResponse mapToOrderDetailResponse(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .orderDetailId(orderDetail.getOrderDetailId())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .optionId(orderDetail.getProductOption() != null ? orderDetail.getProductOption().getOptionId() : null)
                .optionResponse(orderDetail.getProductOption() != null ?
                        List.of(mapToOptionResponse(orderDetail.getProductOption())) : null)
                .build();
    }

    private OptionResponse mapToOptionResponse(ProductOption productOption) {
        return OptionResponse.builder()
                .optionId(productOption.getOptionId())
                .optionValue(productOption.getOptionValue()) // Lấy giá trị của option
                .quantity(productOption.getQuantity()) // Số lượng option
                .optionPrice(productOption.getOptionPrice()) // Giá gốc của option
                .discPrice(productOption.getDiscPrice()) // Giá sau khi giảm
                .createAt(productOption.getCreateAt()) // Ngày tạo
                .updateAt(productOption.getUpdateAt()) // Ngày cập nhật
                .optionImages(productOption.getOptionImages()) // Danh sách ảnh
                .build();
    }


}
