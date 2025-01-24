package com.example.eclat.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity

@Table(name = "Tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long tagId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    String tagName;
    String description;
    LocalDateTime createAt;
    LocalDateTime updateAt;

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", category=" + category +
                ", tagName='" + tagName + '\'' +
                ", description='" + description + '\'' +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
