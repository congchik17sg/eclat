package com.example.eclat.controller;

import com.example.eclat.entities.Category;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Categories")
@Tag(name = "Category", description = "Managing Category")
public class CategoryController {
    // DI = Dependencies Injection
    @Autowired
    private CategoryRepository repository;

    // Lấy tất cả categories
    @GetMapping("")
    public List<Category> getAllCategories() {
        // This request is: http://localhost:8080/api/v1/Categories
        return repository.findAll();
    }

    // Lấy chi tiết từng category
    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Category> foundCategory = repository.findById(id);
        if (foundCategory.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Tìm thấy category", foundCategory)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy category với id: " + id, "")
            );
        }
    }

    // Thêm mới category
    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertCategory(@RequestBody Category newCategory) {
        // Two categories must not have the same name
        List<Category> foundCategories = repository.findByCategoryName(newCategory.getCategoryName().trim());
        if (foundCategories.size() > 0) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Tên category đã tồn tại", "")
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Thêm category thành công", repository.save(newCategory))
        );
    }
}
