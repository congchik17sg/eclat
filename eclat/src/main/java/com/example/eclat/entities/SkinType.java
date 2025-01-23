package com.example.eclat.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class SkinType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String skinName;

    String description;

    @OneToMany(mappedBy = "skinType", cascade = CascadeType.ALL, orphanRemoval = true)
    List<QuizAnswer> quizAnswers;

}
