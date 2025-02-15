package com.example.eclat.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.eclat.entities.*;
import com.example.eclat.model.request.OptionRequest;
import com.example.eclat.model.request.ProductRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.*;
import com.example.eclat.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Products")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Product", description = "Managing Products")
public class ProductController {

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ProductRepository productRepository;

    private CloudinaryService cloudinaryService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private SkinTypeRepository skinTypeRepository;

    @Autowired
    private OptionRepository optionRepository;

    @GetMapping("")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);

        if (foundProduct.isPresent()) {
            Product product = foundProduct.get();
            List<ProductOption> options = product.getOptions(); // Lấy danh sách options
            return ResponseEntity.ok(new ResponseObject("ok", "Product found", product));
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
                    new ResponseObject("failed", "Tag, Brand hoặc SkinType không hợp lệ", "")
            );
        }

        // Tạo Product mới
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

        // Lưu Product vào DB trước để có ID
        Product savedProduct = productRepository.save(newProduct);

        // Xử lý danh sách Option nếu có
        List<ProductOption> options = new ArrayList<>();
        if (requestDTO.getOptions() != null && !requestDTO.getOptions().isEmpty()) {
            for (OptionRequest optionDTO : requestDTO.getOptions()) {
                ProductOption newOption = new ProductOption();
                newOption.setProduct(savedProduct);
                newOption.setOptionValue(optionDTO.getOptionValue());
                newOption.setQuantity(optionDTO.getQuantity());
                newOption.setOptionPrice(optionDTO.getOptionPrice());
                newOption.setDiscPrice(optionDTO.getDiscPrice());
                options.add(newOption);
            }
            optionRepository.saveAll(options); // Lưu tất cả options vào DB
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Thêm sản phẩm thành công", savedProduct)
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
    @PatchMapping("/{id}/status")
    public ResponseEntity<ResponseObject> updateProductStatus(@PathVariable Long id, @RequestParam Boolean status) {
        Optional<Product> foundProduct = productRepository.findById(id);

        if (foundProduct.isPresent()) {
            Product product = foundProduct.get();
            product.setStatus(status);
            product.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian sửa đổi
            productRepository.save(product);

            return ResponseEntity.ok(new ResponseObject("ok", "Product status updated successfully", product));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Product not found with id: " + id, "")
            );
        }
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

    @Operation(
            summary = "Upload ảnh cho Product hoặc Option",
            description = "Upload ảnh lên Cloudinary và lưu vào Product hoặc Option",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    @PostMapping("/upload-image")
    public ResponseEntity<ResponseObject> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "optionId", required = false) Long optionId
    ) {
        try {
            // Kiểm tra file hợp lệ
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("failed", "File ảnh không được để trống", "")
                );
            }

            // Kiểm tra chỉ có 1 trong 2 ID được gửi
            if ((productId != null && optionId != null) || (productId == null && optionId == null)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                        new ResponseObject("failed", "Phải cung cấp một trong hai: productId hoặc optionId", "")
                );
            }

            // Upload file lên Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("url").toString();

            // Xử lý lưu ảnh vào database
            Image image = new Image();
            image.setImageUrl(imageUrl);

            if (productId != null) {
                Optional<Product> product = productRepository.findById(productId);
                if (product.isPresent()) {
                    image.setProduct(product.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObject("failed", "Không tìm thấy Product với ID: " + productId, "")
                    );
                }
            } else {
                Optional<ProductOption> option = optionRepository.findById(optionId);
                if (option.isPresent()) {
                    image.setOption(option.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                            new ResponseObject("failed", "Không tìm thấy Option với ID: " + optionId, "")
                    );
                }
            }

            imageRepository.save(image);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("ok", "Upload ảnh thành công", image)
            );

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("failed", "Lỗi khi upload ảnh: " + e.getMessage(), "")
            );
        }
    }
}
