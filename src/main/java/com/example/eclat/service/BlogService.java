package com.example.eclat.service;

import com.example.eclat.entities.Blog;
import com.example.eclat.entities.BlogImage;
import com.example.eclat.entities.User;
import com.example.eclat.model.request.BlogRequest;
import com.example.eclat.model.response.BlogResponse;
import com.example.eclat.repository.BlogRepository;
import com.example.eclat.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;



    public BlogService(BlogRepository blogRepository, UserRepository userRepository, CloudinaryService cloudinaryService) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
    }
    @Transactional
    public BlogResponse createBlog(BlogRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setUser(user);
        blog.setCreateAt(LocalDateTime.now());
        blog.setUpdateAt(LocalDateTime.now());

        if (request.getImages() != null) {
            List<BlogImage> images = Arrays.stream(request.getImages()) // Đây nè, Arrays.stream
                    .map(file -> {
                        String imageUrl = cloudinaryService.uploadFile(file);
                        BlogImage blogImage = new BlogImage();
                        blogImage.setImageUrl(imageUrl);
                        blogImage.setBlog(blog);
                        return blogImage;
                    })
                    .collect(Collectors.toList());
            blog.setImages(images);
        }

        blogRepository.save(blog);
        return toResponse(blog);
    }


    public List<BlogResponse> getAllBlogs() {
        return blogRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BlogResponse getBlogById(Long id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));
        return toResponse(blog);
    }

    public BlogResponse updateBlog(Long id, BlogRequest request) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blog not found"));

        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setUpdateAt(LocalDateTime.now());

        // Xóa ảnh cũ và thêm ảnh mới
        blog.getImages().clear();
        List<BlogImage> updatedImages = Arrays.stream(request.getImages())
                .map(file -> {
                    String imageUrl = cloudinaryService.uploadFile(file);
                    BlogImage blogImage = new BlogImage();
                    blogImage.setImageUrl(imageUrl);
                    blogImage.setBlog(blog);
                    return blogImage;
                })
                .collect(Collectors.toList());
        blog.setImages(updatedImages);

        blogRepository.save(blog);
        return toResponse(blog);
    }

    public void deleteBlog(Long id) {
        if (!blogRepository.existsById(id)) {
            throw new RuntimeException("Blog not found");
        }
        blogRepository.deleteById(id);
    }

    private BlogResponse toResponse(Blog blog) {
        BlogResponse response = new BlogResponse();
        response.setId(blog.getBlogId());
        response.setTitle(blog.getTitle());
        response.setContent(blog.getContent());
        response.setCreateAt(blog.getCreateAt());
        response.setUpdateAt(blog.getUpdateAt());
        response.setImageUrls(blog.getImages().stream().map(BlogImage::getImageUrl).toList());
        return response;
    }
}
