package com.example.eclat.model.request.quiz;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SkinTypeRequest {


    String skinName;
    String description;
    boolean status;


}
