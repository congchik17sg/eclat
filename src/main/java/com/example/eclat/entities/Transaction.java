package com.example.eclat.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private BigDecimal amount;  // Số tiền thanh toán

    private String transactionStatus;  // Trạng thái thanh toán (Success, Failed, Pending)

    private String paymentMethod;  // Phương thức thanh toán (VNPAY)

    private String vnpTxnRef;  // Mã giao dịch VNPAY

    private String vnpResponseCode;  // Mã phản hồi từ VNPAY

    private String vnpSecureHash;  // Chữ ký bảo mật VNPAY

    private LocalDateTime createAt;

    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
    }
}
