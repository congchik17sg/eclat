package com.example.eclat.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String answerText;

    @ManyToOne
    @JoinColumn(name = "quiz_question_id", nullable = false)
    QuizQuestion quizQuestion;


    @ManyToOne
    @JoinColumn(name = "skin_type_id", nullable = true)
    SkinType skinType;


}
