package com.example.eclat.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId; // ID tự động tăng

    private String optionId;

    private String imageUrl;
}
