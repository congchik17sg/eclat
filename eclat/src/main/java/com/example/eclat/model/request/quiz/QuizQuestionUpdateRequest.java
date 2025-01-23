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

public class QuizQuestionUpdateRequest {

    String question_text;
    LocalDate update_at;
}
