package com.example.eclat.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long feedback_id;

    String text;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id") // Liên kết đúng với User
    User user;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productId") // Liên kết đúng với Product
    Product product;

    int rating;

    LocalDate create_at;
    LocalDate update_at;

}
