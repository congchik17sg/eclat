package com.example.eclat.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
     User user;  // Liên kết với bảng User

     BigDecimal totalPrices;  // Tổng tiền của đơn hàng

     String address;  // Địa chỉ giao hàng

     String status;  // Trạng thái đơn hàng (VD: PENDING, SHIPPED, COMPLETED)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
     LocalDateTime createAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
     LocalDateTime updateAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
     List<OrderDetail> orderDetails = new ArrayList<>();

    String paymentMethod;



    @PrePersist
    protected void onCreate() {
        createAt = LocalDateTime.now();
        updateAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateAt = LocalDateTime.now();
    }

    public BigDecimal getTotalPrices() {
        return totalPrices;
    }


}
