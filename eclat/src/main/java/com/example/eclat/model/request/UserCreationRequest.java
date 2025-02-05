package com.example.eclat.model.request;


import com.example.eclat.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {

    @Size(min = 4)
    @Column(unique = true)
    String username;
    @Size(min = 8, message = "password at least 8 characters")
    String password;
    //Role role;
    String email;
    String phone;
    String address;
    LocalDate create_at;
    LocalDate update_at;


//
}
