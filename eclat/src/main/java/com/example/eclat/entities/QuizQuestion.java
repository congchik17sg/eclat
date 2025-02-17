package com.example.eclat.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Column(name = "question_text") // Ánh xạ cột trong database với thuộc tính
     String questionText;

    @Column(name = "create_at") // Ánh xạ cột create_at
     LocalDate createAt;

    @Column(name = "update_at") // Ánh xạ cột update_at
     LocalDate updateAt;

    @OneToMany(mappedBy = "quizQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    List<QuizAnswer> answers;

    String img_url;


}
