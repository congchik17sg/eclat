package com.example.eclat.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @JoinColumn(name = "user_id", referencedColumnName = "id") // Liên kết với User
    User user;

    @ManyToOne
    @JoinColumn(name = "order_detail_id", referencedColumnName = "orderDetailId") // Liên kết với OrderDetail
    OrderDetail orderDetail;

    int rating;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime create_at;  // Đổi từ LocalDate thành LocalDateTime

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    LocalDateTime update_at;  // Đổi từ LocalDate thành LocalDateTime
}
