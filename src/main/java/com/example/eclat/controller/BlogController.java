package com.example.eclat.controller;

import com.example.eclat.entities.Blog;
import com.example.eclat.entities.BlogImage;
import com.example.eclat.entities.User;
import com.example.eclat.model.response.BlogResponse;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.BlogImageRepository;
import com.example.eclat.repository.BlogRepository;
import com.example.eclat.repository.UserRepository;
import com.example.eclat.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;
    private final BlogImageRepository blogImageRepository;

    @Autowired
    public BlogController(BlogRepository blogRepository,
                          UserRepository userRepository,
                          CloudinaryService cloudinaryService,
                          BlogImageRepository blogImageRepository) {
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.cloudinaryService = cloudinaryService;
        this.blogImageRepository = blogImageRepository;
    }

    @PostMapping(value = "/blogs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> createBlog(
            @Parameter(description = "Tiêu đề blog", required = true)
            @RequestParam("title") String title,

            @Parameter(description = "Nội dung blog", required = true)
            @RequestParam("content") String content,

            @Parameter(description = "ID của người dùng", required = true)
            @RequestParam("userId") String userId,

            @Parameter(description = "Danh sách file ảnh cho blog", required = false)
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            // Kiểm tra user tồn tại
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + userId));

            // Tạo blog
            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setContent(content);
            blog.setUser(user);
            blog.setCreateAt(LocalDateTime.now());
            blog.setUpdateAt(LocalDateTime.now());

            List<String> imageUrls = new ArrayList<>();

            // Upload và lưu ảnh nếu có
            if (images != null && images.length > 0) {
                List<BlogImage> blogImages = Arrays.stream(images)
                        .filter(file -> !file.isEmpty())
                        .map(file -> {
                            String imageUrl = cloudinaryService.uploadFile(file);
                            imageUrls.add(imageUrl); // Thêm URL vào list để trả response
                            BlogImage blogImage = new BlogImage();
                            blogImage.setImageUrl(imageUrl);
                            blogImage.setCreateAt(LocalDate.now());
                            blogImage.setUpdateAt(LocalDate.now());
                            blogImage.setBlog(blog);
                            return blogImage;
                        })
                        .collect(Collectors.toList());
                blog.setImages(blogImages);
            }

            // Lưu blog
            blogRepository.save(blog);

            // Tạo response DTO
            BlogResponse blogResponse = new BlogResponse(
                    blog.getBlogId(),
                    blog.getTitle(),
                    blog.getContent(),
                    blog.getCreateAt(),
                    blog.getUpdateAt(),
                    user.getUsername(),
                    imageUrls
            );

            return ResponseEntity.ok(new ResponseObject("ok", "Tạo blog thành công!", blogResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("failed", "Lỗi khi tạo blog: " + e.getMessage(), ""));
        }
    }



    @GetMapping("/blogs/{id}")
    public ResponseEntity<ResponseObject> getBlogById(@PathVariable("id") Long id) {
        try {
            Optional<Blog> blogOptional = blogRepository.findById(id);
            if (blogOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy Blog với ID: " + id, "")
                );
            }
            Blog blog = blogOptional.get();
            BlogResponse blogResponse = new BlogResponse(
                    blog.getBlogId(),
                    blog.getTitle(),
                    blog.getContent(),
                    blog.getCreateAt(),
                    blog.getUpdateAt(),
                    blog.getUser().getUsername(),
                    blog.getImages().stream().map(BlogImage::getImageUrl).collect(Collectors.toList())
            );
            return ResponseEntity.ok(new ResponseObject("ok", "Lấy blog thành công!", blogResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("failed", "Lỗi khi lấy blog: " + e.getMessage(), "")
            );
        }
    }

    @GetMapping("/blogs")
    public ResponseEntity<ResponseObject> getAllBlogs() {
        try {
            List<Blog> blogs = blogRepository.findAll();
            List<BlogResponse> blogResponses = blogs.stream().map(blog ->
                    new BlogResponse(
                            blog.getBlogId(),
                            blog.getTitle(),
                            blog.getContent(),
                            blog.getCreateAt(),
                            blog.getUpdateAt(),
                            blog.getUser().getUsername(),
                            blog.getImages().stream().map(BlogImage::getImageUrl).collect(Collectors.toList())
                    )
            ).collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseObject("ok", "Lấy danh sách blog thành công!", blogResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("failed", "Lỗi khi lấy danh sách blog: " + e.getMessage(), ""));
        }
    }


    @PutMapping(value = "/blogs/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> updateBlog(
            @Parameter(description = "ID blog cần cập nhật", required = true)
            @PathVariable("id") Long id,

            @Parameter(description = "Tiêu đề mới cho blog", required = true)
            @RequestParam("title") String title,

            @Parameter(description = "Nội dung mới cho blog", required = true)
            @RequestParam("content") String content,

            @Parameter(description = "Danh sách file ảnh mới cho blog", required = false)
            @RequestPart(value = "images", required = false) MultipartFile[] images
    ) {
        try {
            // Kiểm tra blog tồn tại
            Blog blog = blogRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy blog với ID: " + id));

            // Cập nhật thông tin
            blog.setTitle(title);
            blog.setContent(content);
            blog.setUpdateAt(LocalDateTime.now());

            List<String> imageUrls = blog.getImages() != null ?
                    blog.getImages().stream().map(BlogImage::getImageUrl).toList() : new ArrayList<>();

            // Nếu có ảnh mới, upload & lưu
            if (images != null && images.length > 0) {
                List<BlogImage> blogImages = Arrays.stream(images)
                        .filter(file -> !file.isEmpty())
                        .map(file -> {
                            String imageUrl = cloudinaryService.uploadFile(file);
                            imageUrls.add(imageUrl); // Cập nhật URL
                            BlogImage blogImage = new BlogImage();
                            blogImage.setImageUrl(imageUrl);
                            blogImage.setCreateAt(LocalDate.now());
                            blogImage.setUpdateAt(LocalDate.now());
                            blogImage.setBlog(blog);
                            return blogImage;
                        })
                        .collect(Collectors.toList());
                blog.getImages().addAll(blogImages);
            }

            // Lưu blog
            blogRepository.save(blog);

            // Trả response DTO
            BlogResponse blogResponse = new BlogResponse(
                    blog.getBlogId(),
                    blog.getTitle(),
                    blog.getContent(),
                    blog.getCreateAt(),
                    blog.getUpdateAt(),
                    blog.getUser().getUsername(),
                    imageUrls
            );

            return ResponseEntity.ok(new ResponseObject("ok", "Cập nhật blog thành công!", blogResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("failed", "Lỗi khi cập nhật blog: " + e.getMessage(), ""));
        }
    }

    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<ResponseObject> deleteBlog(
            @Parameter(description = "ID của blog cần xóa", required = true)
            @PathVariable("id") Long id
    ) {
        try {
            Optional<Blog> blogOptional = blogRepository.findById(id);
            if (blogOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy blog với ID: " + id, "")
                );
            }
            blogRepository.deleteById(id);
            return ResponseEntity.ok(
                    new ResponseObject("ok", "Xóa blog thành công!", "")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("failed", "Lỗi khi xóa blog: " + e.getMessage(), "")
            );
        }
    }

}