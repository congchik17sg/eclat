package com.example.eclat.entities;


import com.example.eclat.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String username;
    String password;
    Set<String> role;
    String email;
    String phone;
//    String address;
    LocalDate create_at;
    LocalDate update_at;
    boolean status;




}
