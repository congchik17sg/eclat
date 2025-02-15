package com.example.eclat.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "ProductOption")
public class ProductOption {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long optionId;

        @ManyToOne
        @JoinColumn(name = "product_id", nullable = false)
        @JsonBackReference // Tránh lỗi vòng lặp khi serialize JSON
        private Product product;

        private String optionValue;
        private int quantity;
        private BigDecimal optionPrice;
        private BigDecimal discPrice;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        private LocalDateTime createAt;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
        private LocalDateTime updateAt;

        @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Image> images = new ArrayList<>();
}
