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
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) Long productId;
    String productName;
    int quantity;
    String description;
    String usageInstruct;
    String originCountry;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    Tag tag;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    Brand brand;

    @ManyToOne
    @JoinColumn(name = "skintype_id")
    SkinType skinType;
    BigDecimal price;
    LocalDateTime createAt;
    LocalDateTime updateAt;
    Boolean status;
}
