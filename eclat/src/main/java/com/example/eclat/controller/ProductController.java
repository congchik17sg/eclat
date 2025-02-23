package com.example.eclat.controller;

import com.cloudinary.Cloudinary;
import com.example.eclat.entities.*;
import com.example.eclat.model.request.OptionRequest;
import com.example.eclat.model.request.ProductRequest;
import com.example.eclat.model.response.OptionResponse;
import com.example.eclat.model.response.ProductResponse;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.*;
import com.example.eclat.service.CloudinaryService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/Products")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Product", description = "Managing Products")
public class ProductController {

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private ProductRepository productRepository;

    private final CloudinaryService cloudinaryService;
    @Autowired
    public ProductController (CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

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

        if (foundProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Product not found with id: " + id, ""));
        }

        Product product = foundProduct.get();

        // Lấy danh sách ảnh của Product
        List<Image> productImages = product.getImages().stream()
                // .map(Image::getImageUrl)
                .collect(Collectors.toList());

        // Lấy danh sách Option và danh sách ảnh trong từng Option
        List<OptionResponse> optionResponses = product.getOptions().stream()
                .map(option -> new OptionResponse(
                        option.getOptionId(),
                        option.getOptionValue(),
                        option.getQuantity(),
                        option.getOptionPrice(),
                        option.getDiscPrice(),
                        option.getCreateAt(),
                        option.getUpdateAt(),
                        option.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()) // Lấy ảnh của Option
                ))
                .collect(Collectors.toList());

        // Chuyển đổi Product sang ProductResponse
        ProductResponse productResponse = new ProductResponse(
                product.getProductId(),
                product.getProductName(),
                product.getDescription(),
                product.getUsageInstruct(),
                product.getOriginCountry(),
                product.getCreateAt(),
                product.getUpdateAt(),
                product.getStatus(),
                product.getTag() != null ? product.getTag().getTagId() : null,
                product.getBrand() != null ? product.getBrand().getBrandId() : null,
                product.getSkinType() != null ? product.getSkinType().getId() : null,
                optionResponses,
                productImages, // Thêm danh sách ảnh của Product
                product.getAttribute()
        );

        return ResponseEntity.ok(new ResponseObject("ok", "Product found", productResponse));
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
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> uploadImage(
            @Parameter(description = "File ảnh cần upload", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "ID của sản phẩm (nếu muốn lưu vào sản phẩm)")
            @RequestParam(value = "productId", required = false) Long productId,

            @Parameter(description = "ID của option (nếu muốn lưu vào option)")
            @RequestParam(value = "optionId", required = false) Long optionId
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("failed", "File không được để trống!", "")
            );
        }
        // Upload file lên Cloudinary
        String imageUrl;
        try {
            imageUrl = cloudinaryService.uploadFile(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("failed", "Lỗi khi upload ảnh: " + e.getMessage(), "")
            );
        }
     //Logic Business
        Image newImage = new Image();
        newImage.setImageUrl(imageUrl);
        newImage.setCreateAt(LocalDateTime.now());
        newImage.setUpdateAt(LocalDateTime.now());
        if (productId != null) {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                newImage.setProduct(product.get());
                imageRepository.save(newImage);
                return ResponseEntity.ok(new ResponseObject("ok", "Ảnh đã được lưu vào Product", imageUrl));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy Product với ID: " + productId, "")
                );
            }
        } else if (optionId != null) {
            Optional<ProductOption> option = optionRepository.findById(optionId);
            if (option.isPresent()) {
                newImage.setOption(option.get());
                imageRepository.save(newImage);
                return ResponseEntity.ok(new ResponseObject("ok", "Ảnh đã được lưu vào Option", imageUrl));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("failed", "Không tìm thấy Option với ID: " + optionId, "")
                );
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject("failed", "Bạn cần cung cấp productId hoặc optionId", "")
        );
    }
}
