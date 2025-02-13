package com.example.eclat.controller;

import com.example.eclat.entities.Product;
import com.example.eclat.entities.Brand;
import com.example.eclat.entities.SkinType;
import com.example.eclat.entities.Tag;
import com.example.eclat.model.request.ProductRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.ProductRepository;
import com.example.eclat.repository.BrandRepository;
import com.example.eclat.repository.SkinTypeRepository;
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
@RequestMapping(path = "/api/Products")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Product", description = "Managing Products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SkinTypeRepository skinTypeRepository;

    @GetMapping("")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Product found", foundProduct)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Product not found with id: " + id, "")
            );
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertProduct(@RequestBody @Valid ProductRequest requestDTO) {
        Optional<Tag> tag = tagRepository.findById(requestDTO.getTagId());
        Optional<Brand> brand = brandRepository.findById(requestDTO.getBrandId());
        Optional<SkinType> skinType = skinTypeRepository.findById(requestDTO.getSkinTypeId());

        if (tag.isEmpty() || brand.isEmpty() || skinType.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("failed", "Invalid Tag, Brand, or SkinType ID", "")
            );
        }
        Product newProduct = new Product();
        newProduct.setProductName(requestDTO.getProductName());
        newProduct.setDescription(requestDTO.getDescription());
        newProduct.setUsageInstruct(requestDTO.getUsageInstruct());
        newProduct.setOriginCountry(requestDTO.getOriginCountry());
        newProduct.setTag(tag.get());
        newProduct.setBrand(brand.get());
        newProduct.setSkinType(skinType.get());
        newProduct.setAttribute(requestDTO.getAttribute());
        newProduct.setCreateAt(LocalDateTime.now());
        newProduct.setUpdateAt(LocalDateTime.now());
        newProduct.setStatus(true);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Product added successfully", productRepository.save(newProduct))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateProduct(@RequestBody Product newProduct, @PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setProductName(newProduct.getProductName());
                    product.setDescription(newProduct.getDescription());
                    product.setUsageInstruct(newProduct.getUsageInstruct());
                    product.setOriginCountry(newProduct.getOriginCountry());
                    product.setTag(newProduct.getTag());
                    product.setBrand(newProduct.getBrand());
                    product.setSkinType(newProduct.getSkinType());
                    product.setAttribute(newProduct.getAttribute());
                    product.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian sửa đổi
                    productRepository.save(product);
                    return ResponseEntity.status(HttpStatus.OK).body(
                            new ResponseObject("ok", "Cập nhật sản phẩm thành công", product)
                    );
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy sản phẩm với ID: " + id, "")
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id) {
        boolean exists = productRepository.existsById(id);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Product not found", "")
            );
        }
        productRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Product deleted successfully", "")
        );
    }
    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchProducts(@RequestParam String name) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(name);

        if (products.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "No products found with name: " + name, "")
            );
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Products found", products)
        );
    }
}
