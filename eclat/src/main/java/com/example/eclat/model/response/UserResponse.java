package com.example.eclat.model.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {

    String id;
    String username;
    String password;
    //    Role role;
    String email;
    String phone;
    String address;
    LocalDate create_at;
    LocalDate update_at;
    boolean status;

}
