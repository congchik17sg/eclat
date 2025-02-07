package com.example.eclat.model.request;


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
public class UserUpdateRequest {


    String password;
    //Role role;
//    String email;
    String phone;
//    String address;
//    LocalDate create_at;
    LocalDate update_at;
    //boolean status;
    List<String> roles;



}
