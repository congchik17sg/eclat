package com.example.eclat.controller;

import com.example.eclat.entities.Category;
import com.example.eclat.model.request.CategoryRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.CategoryRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    public ResponseEntity<ResponseObject> insertCategory(@RequestBody @Valid CategoryRequest requestDTO) {
        // Kiểm tra category name có tồn tại chưa
        List<Category> foundCategories = repository.findByCategoryName(requestDTO.getCategoryName().trim());
        if (!foundCategories.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseObject("failed", "Tên category đã tồn tại", "")
            );
        }

        // Tạo đối tượng Category từ DTO
        Category newCategory = new Category();
        newCategory.setCategoryName(requestDTO.getCategoryName());
        newCategory.setDescription(requestDTO.getDescription());
        newCategory.setCreateAt(LocalDateTime.now());
        newCategory.setUpdateAt(LocalDateTime.now());
        // Các field createAt, updateAt tự động được set
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Thêm category thành công", repository.save(newCategory))
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateCategory(@RequestBody @Valid CategoryRequest requestDTO, @PathVariable Long id) {
        return repository.findById(id)
                .map(category -> {
                    category.setCategoryName(requestDTO.getCategoryName());
                    category.setDescription(requestDTO.getDescription());
                    category.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian chỉnh sửa
                    repository.save(category);
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject("ok", "Cập nhật danh mục thành công", category)
                    );
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy danh mục với ID: " + id, "")
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteCategory(@PathVariable Long id) {
        boolean exists = repository.existsById(id);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Không tìm thấy danh mục", "")
            );
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Danh mục đã được xóa thành công", "")
        );
    }
}
