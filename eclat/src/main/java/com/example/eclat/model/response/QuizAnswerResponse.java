package com.example.eclat.model.response;


import com.example.eclat.entities.QuizQuestion;
import com.example.eclat.entities.SkinType;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

public class QuizAnswerResponse {

    Long id;           // ID của câu trả lời
    String answerText; // Nội dung câu trả lời
    Long questionId;   // ID của câu hỏi
    String questionText; // Nội dung của câu hỏi
    Long skinTypeId;   // ID của loại da
    String skinName;   // Tên loại da
    String skinDescription; // Mô tả loại da
}
