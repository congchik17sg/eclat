package com.example.eclat.controller;


import com.example.eclat.entities.Brand;
import com.example.eclat.model.request.BrandRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.BrandRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

    @RestController
    @RequestMapping(path = "/api/Brands")
    @Tag(name = "Brand", description ="Managing Brand" )
    public class BrandController {

        @Autowired
        private BrandRepository repository;

        // Lấy tất cả các thương hiệu
        @GetMapping("")
        public List<Brand> getAllBrands() {
            return repository.findAll();
        }

        // Lấy chi tiết từng thương hiệu theo ID
        @GetMapping("/{id}")
        public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
            Optional<Brand> foundBrand = repository.findById(id);
            if (foundBrand.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("ok", "Tìm thấy thương hiệu", foundBrand)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy thương hiệu với ID: " + id, "")
                );
            }
        }

        // Thêm mới một thương hiệu
        @PostMapping("/insert")
        public ResponseEntity<ResponseObject> insertBrand(@RequestBody @Valid BrandRequest requestDTO) {
            List<Brand> foundBrands = repository.findByBrandName(requestDTO.getBrandName().trim());
            if (!foundBrands.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("failed", "Tên thương hiệu đã tồn tại", "")
                );
            }
            Brand newBrand = new Brand();
            newBrand.setBrandName(requestDTO.getBrandName());
            newBrand.setImgUrl(requestDTO.getImgUrl());

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Thêm thương hiệu thành công", repository.save(newBrand))
            );
        }

        // Cập nhật thương hiệu
        @PutMapping("/{id}")
        public ResponseEntity<ResponseObject> updateBrand(@RequestBody Brand newBrand, @PathVariable Long id) {
            Brand updatedBrand = repository.findById(id)
                    .map(brand -> {
                        brand.setBrandName(newBrand.getBrandName());
                        brand.setImgUrl(newBrand.getImgUrl());
                        return repository.save(brand);
                    }).orElseGet(() -> {
                        newBrand.setBrandId(id);
                        return repository.save(newBrand);
                    });
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Cập nhật thương hiệu thành công", updatedBrand)
            );
        }

        // Xóa thương hiệu
        @DeleteMapping("/{id}")
        public ResponseEntity<ResponseObject> deleteBrand(@PathVariable Long id) {
            boolean exists = repository.existsById(id);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy thương hiệu để xóa", "")
                );
            }
            repository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Xóa thương hiệu thành công", "")
            );
        }
    }

