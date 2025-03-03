package com.example.eclat.model.response.quiz;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedbackResponse {


    String text;
    int rating;
    String username;
    Long orderDetailId;

    LocalDate create_at;
    LocalDate update_at;

}
