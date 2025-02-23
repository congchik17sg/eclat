package com.example.eclat.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Blog")
public class Blog {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long blogId;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false, referencedColumnName = "id")
        User user;

        String title;
        String content;
        LocalDateTime createAt;
        LocalDateTime updateAt;

        @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL)
        @JsonManagedReference
        List<BlogImage> images;
}
