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
import com.fasterxml.jackson.databind.ObjectMapper;

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

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ResponseObject> createProduct(
            @RequestParam("product") String productJson,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            ProductRequest productRequest = objectMapper.readValue(productJson, ProductRequest.class);
            Product product = new Product();
            product.setProductName(productRequest.getProductName());
            product.setDescription(productRequest.getDescription());
            product.setUsageInstruct(productRequest.getUsageInstruct());
            product.setOriginCountry(productRequest.getOriginCountry());
            product.setCreateAt(LocalDateTime.now());
            product.setUpdateAt(LocalDateTime.now());
//            product.setStatus(productRequest.getStatus());
            product.setTag(tagRepository.findById(productRequest.getTagId()).orElse(null));
            product.setBrand(brandRepository.findById(productRequest.getBrandId()).orElse(null));
            product.setSkinType(skinTypeRepository.findById(productRequest.getSkinTypeId()).orElse(null));

            productRepository.save(product);

            if (images != null && !images.isEmpty()) {
                for (MultipartFile image : images) {
                    String imageUrl = cloudinaryService.uploadFile(image);
                    Image img = new Image();
                    img.setImageUrl(imageUrl);
                    img.setProduct(product);
                    imageRepository.save(img);
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject("ok", "Product created successfully", product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("failed", "Error creating product", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateProduct(@PathVariable Long id, @RequestBody ProductRequest productRequest) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Product not found with id: " + id, ""));
        }
        Product product = foundProduct.get();
        product.setProductName(productRequest.getProductName());
        product.setDescription(productRequest.getDescription());
        product.setUsageInstruct(productRequest.getUsageInstruct());
        product.setOriginCountry(productRequest.getOriginCountry());
        product.setUpdateAt(LocalDateTime.now());
//        product.setStatus(productRequest.getStatus());
        product.setTag(tagRepository.findById(productRequest.getTagId()).orElse(null));
        product.setBrand(brandRepository.findById(productRequest.getBrandId()).orElse(null));
        product.setSkinType(skinTypeRepository.findById(productRequest.getSkinTypeId()).orElse(null));

        productRepository.save(product);
        return ResponseEntity.ok(new ResponseObject("ok", "Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        if (foundProduct.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Product not found with id: " + id, ""));
        }
        productRepository.deleteById(id);
        return ResponseEntity.ok(new ResponseObject("ok", "Product deleted successfully", ""));
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllProducts() {
        List<Product> products = productRepository.findAll();

        List<ProductResponse> productResponses = products.stream().map(product -> {
            List<String> productImages = product.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList());

            List<OptionResponse> optionResponses = product.getOptions().stream()
                    .map(option -> new OptionResponse(
                            option.getOptionId(),
                            option.getOptionValue(),
                            option.getQuantity(),
                            option.getOptionPrice(),
                            option.getDiscPrice(),
                            option.getCreateAt(),
                            option.getUpdateAt(),
                            option.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()),
                            null
                    ))
                    .collect(Collectors.toList());

            return new ProductResponse(
                    product.getProductId(),
                    product.getProductName(),
                    product.getDescription(),
                    product.getUsageInstruct(),
                    product.getOriginCountry(),
                    product.getCreateAt(),
                    product.getUpdateAt(),
                    product.getStatus(),
                    product.getTag(),
                    product.getBrand(),
                    product.getSkinType(),
                    optionResponses,
                    productImages,
                    product.getAttribute()
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseObject("ok", "List of products", productResponses));
    }
}
