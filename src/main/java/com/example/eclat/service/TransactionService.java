package com.example.eclat.service;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.Transaction;
import com.example.eclat.repository.OrderRepository;
import com.example.eclat.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;


@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }
    public Transaction saveTransaction(Map<String, String> vnpayResponse) {
        String txnRef = vnpayResponse.get("vnp_TxnRef");
        String vnpResponseCode = vnpayResponse.get("vnp_ResponseCode");
        String transactionStatus = vnpayResponse.get("vnp_TransactionStatus");

        try {
            // Lấy order từ txnRef (txnRef chính là orderId)
            Long orderId = Long.parseLong(txnRef);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found for txnRef: " + txnRef));

            // Tạo transaction mới
            Transaction transaction = Transaction.builder()
                    .order(order)
                    .amount(new BigDecimal(vnpayResponse.get("vnp_Amount")).divide(BigDecimal.valueOf(100))) // Chuyển về đúng giá trị
                    .transactionStatus(transactionStatus)
                    .paymentMethod("VNPAY")
                    .vnpTxnRef(txnRef)
                    .vnpResponseCode(vnpResponseCode)
                    .vnpSecureHash(vnpayResponse.get("vnp_SecureHash"))
                    .createAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(transaction);
            logger.info("Saved transaction for Order ID: {}", orderId);

            // Nếu thanh toán thành công, cập nhật trạng thái đơn hàng
            if ("00".equals(transactionStatus)) {
                order.setStatus("PAID");
                orderRepository.save(order);
                logger.info("Updated order status to PAID for Order ID: {}", orderId);
            }

            return transaction;
        } catch (Exception e) {
            logger.error("Error saving transaction for txnRef: " + txnRef, e);
            throw new RuntimeException("Transaction processing failed", e);
        }
    }


}
