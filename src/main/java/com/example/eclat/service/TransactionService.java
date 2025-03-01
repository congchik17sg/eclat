package com.example.eclat.service;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.ProductOption;
import com.example.eclat.entities.Transaction;
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
import java.util.Map;
import java.util.Optional;


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

    public void updateTransactionStatus(String vnpTxnRef, String status, String vnpResponseCode) {
        Optional<Transaction> transactionOpt = transactionRepository.findByVnpTxnRef(vnpTxnRef);
        if (transactionOpt.isEmpty()) {
            throw new IllegalStateException("Giao dịch không tồn tại!");
        }

        Transaction transaction = transactionOpt.get();
        Order order = transaction.getOrder();

        transaction.setTransactionStatus(status);
        transaction.setVnpResponseCode(vnpResponseCode);

        if ("SUCCESS".equals(status)) {
            order.setStatus("PAID");

            // 🔹 Giảm số lượng sản phẩm trong ProductOption
            for (OrderDetail orderDetail : order.getOrderDetails()) {
                ProductOption productOption = orderDetail.getProductOption();
                int newQuantity = productOption.getQuantity() - orderDetail.getQuantity();
                if (newQuantity < 0) {
                    throw new IllegalStateException("Không đủ hàng trong kho!");
                }
                productOption.setQuantity(newQuantity);
                productOptionRepository.save(productOption);
            }
        }

        orderRepository.save(order);
        transactionRepository.save(transaction);
    }



}
