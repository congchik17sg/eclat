package com.example.eclat.model.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlogRequest {
    String userId;
    String title;
    String content;
    MultipartFile[] images;  // Upload nhiều ảnh
}
