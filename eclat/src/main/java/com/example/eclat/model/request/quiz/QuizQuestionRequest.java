package com.example.eclat.model.request.quiz;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizQuestionRequest {

//    String id;
    String question_text;
    LocalDate create_at;
    LocalDate update_at;


}
