package com.example.eclat.controller;

import com.cloudinary.Cloudinary;
import com.example.eclat.entities.Image;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.ImageRepository;
import com.example.eclat.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Image", description = "Uploading Image for Product and Product Option")
public class ImageController {
    @Autowired
    private Cloudinary cloudinary;
    private final ImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;
        @Autowired
        public ImageController(ImageRepository imageRepository, CloudinaryService cloudinaryService) {
            this.imageRepository = imageRepository;
            this.cloudinaryService = cloudinaryService;
        }

        @GetMapping
        public ResponseEntity<ResponseObject> getImagesByProductOrOption(
                @RequestParam(required = false) Long productId,
                @RequestParam(required = false) Long optionId) {

            List<Image> images;

            if (productId != null) {
                images = imageRepository.findByProduct_ProductId(productId);
            } else if (optionId != null) {
                images = imageRepository.findByOption_OptionId(optionId);
            } else {
                return ResponseEntity.badRequest().body(new ResponseObject("failed", "Vui lòng cung cấp productId hoặc optionId", ""));
            }

            if (images.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("failed", "Không tìm thấy ảnh", ""));
            }

            List<String> imageUrls = images.stream().map(Image::getImageUrl).collect(Collectors.toList());

            return ResponseEntity.ok(new ResponseObject("ok", "Lấy danh sách ảnh thành công", imageUrls));
        }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteImage(@PathVariable Long id) {
        Optional<Image> imageOptional = imageRepository.findById(id);

        if (imageOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Không tìm thấy ảnh với ID: " + id, ""));
        }

        Image image = imageOptional.get();

        try {
            // Xóa ảnh trên Cloudinary (nếu bạn lưu ảnh trên Cloudinary)
            cloudinaryService.deleteFile(image.getImageUrl());

            // Xóa ảnh khỏi database
            imageRepository.deleteById(id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("ok", "Xóa ảnh thành công", ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("failed", "Lỗi khi xóa ảnh: " + e.getMessage(), ""));
        }
    }

}

