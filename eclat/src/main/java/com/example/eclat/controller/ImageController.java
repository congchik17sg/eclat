package com.example.eclat.controller;

import com.example.eclat.entities.Image;
import com.example.eclat.model.request.ImageRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.ImageRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Images")
@Tag(name = "Image", description = "Managing Images")
public class ImageController {

    @Autowired
    private ImageRepository repository;

    // Lấy tất cả images
    @GetMapping("")
    public List<Image> getAllImages() {
        return repository.findAll();
    }

    // Lấy chi tiết từng image
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Image> foundImage = repository.findById(id);
        if (foundImage.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Tìm thấy image", foundImage.get())
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy image với id: " + id, "")
            );
        }
    }

    // Thêm mới image
    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertImage(@RequestBody @Valid ImageRequest requestDTO) {
        // Tạo đối tượng Image từ DTO
        Image newImage = new Image();
        newImage.setOptionId(requestDTO.getOptionId());
        newImage.setImageUrl(requestDTO.getImageUrl());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Thêm image thành công", repository.save(newImage))
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateImage(@RequestBody @Valid ImageRequest requestDTO, @PathVariable Long id) {
        Optional<Image> existingImage = repository.findById(id);

        if (existingImage.isPresent()) {
            Image updatedImage = existingImage.get();
            updatedImage.setOptionId(requestDTO.getOptionId());
            updatedImage.setImageUrl(requestDTO.getImageUrl());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Cập nhật image thành công", repository.save(updatedImage))
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy image với id: " + id, "")
            );
        }
    }

    // Xóa image
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteImage(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy image với id: " + id, "")
            );
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Xóa image thành công", "")
        );
    }
}