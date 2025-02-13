package com.example.eclat.controller;

import com.example.eclat.entities.Category;
import com.example.eclat.entities.Tag;
import com.example.eclat.model.request.TagRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.CategoryRepository;
import com.example.eclat.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping(path = "/api/Tags")
    @io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Managing Tag")
    public class TagController {
        // Dependency Injection
        @Autowired
        private TagRepository repository;
        @Autowired
        private CategoryRepository categoryRepository;

        @GetMapping("")
        public List<Tag> getAllTags() {
            return repository.findAll();
        }

        // Get tag details by ID
        @GetMapping("/{id}")
        public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
            Optional<Tag> foundTag = repository.findById(id);
            if (foundTag.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Tag found", foundTag)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Tag not found with id: " + id, "")
                );
            }
        }

        @PostMapping("/insert")
        public ResponseEntity<ResponseObject> insertTag(@RequestBody @Valid TagRequest requestDTO) {
            // Tìm category theo categoryId
            Optional<Category> category = categoryRepository.findById(requestDTO.getCategoryId());
            if (category.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("failed", "Category không tồn tại", "")
                );
            }

            // Kiểm tra tag có trùng tên trong category hay không
            List<Tag> foundTags = repository.findByTagNameAndCategory(requestDTO.getTagName().trim(), category.get());
            if (!foundTags.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                        new ResponseObject("failed", "Tag name đã tồn tại trong category", "")
                );
            }

            // Tạo Tag mới từ DTO
            Tag newTag = new Tag();
            newTag.setTagName(requestDTO.getTagName());
            newTag.setDescription(requestDTO.getDescription());
            newTag.setCategory(category.get());
            newTag.setUpdateAt(LocalDateTime.now());
            newTag.setCreateAt(LocalDateTime.now());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Tag added successfully", repository.save(newTag))
            );
        }

        @PutMapping("/{id}")
        public ResponseEntity<ResponseObject> updateTag(@RequestBody @Valid TagRequest requestDTO, @PathVariable Long id) {
            return repository.findById(id)
                    .map(tag -> {
                        tag.setTagName(requestDTO.getTagName());
                        tag.setDescription(requestDTO.getDescription());
                        tag.setCategory(categoryRepository.findById(requestDTO.getCategoryId()).orElse(null));
                        tag.setUpdateAt(LocalDateTime.now());
                        repository.save(tag);
                        return ResponseEntity.status(HttpStatus.OK).body(
                                new ResponseObject("ok", "Cập nhật tag thành công", tag)
                        );
                    }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObject("failed", "Không tìm thấy tag với ID: " + id, "")
                    ));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseObject> deleteTag(@PathVariable Long id) {
            boolean exists = repository.existsById(id);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy tag", "")
                );
            }
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Tag đã được xóa thành công", "")
            );
        }
    }

