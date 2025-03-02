package com.example.eclat.controller;

import com.example.eclat.entities.ProductOption;
import com.example.eclat.entities.Product;
import com.example.eclat.model.request.OptionRequest;
import com.example.eclat.model.response.ResponseObject;
import com.example.eclat.repository.OptionRepository;
import com.example.eclat.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/Options")
@io.swagger.v3.oas.annotations.tags.Tag(name = "Option", description = "Managing Product Options")
public class OptionController {

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("")
    public List<ProductOption> getAllOptions() {
        return optionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> findById(@PathVariable Long id) {
        Optional<ProductOption> foundOption = optionRepository.findById(id);
        return foundOption.map(option ->
                ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("ok", "Option found", option))
        ).orElseGet(() ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("failed", "Option not found", ""))
        );
    }

    @PostMapping("/insert/{id}")
    public ResponseEntity<ResponseObject> insertOption(@PathVariable("productId") Long id , @RequestBody @Valid OptionRequest requestDTO) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("failed", "Invalid Product ID", "")
            );
        }

        ProductOption newOption = new ProductOption();
        newOption.setProduct(product.get());
        newOption.setOptionValue(requestDTO.getOptionValue());
        newOption.setQuantity(requestDTO.getQuantity());
        newOption.setOptionPrice(requestDTO.getOptionPrice());
        newOption.setDiscPrice(requestDTO.getDiscPrice());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Option added successfully", optionRepository.save(newOption))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateOption(@PathVariable Long id, @RequestBody OptionRequest request) {
        return optionRepository.findById(id)
                .map(option -> {
                    option.setOptionValue(request.getOptionValue());
                    option.setQuantity(request.getQuantity());
                    option.setOptionPrice(request.getOptionPrice());
                    option.setDiscPrice(request.getDiscPrice());
                    option.setUpdateAt(LocalDateTime.now());

                    optionRepository.save(option);
                    return ResponseEntity.ok(new ResponseObject("OK", "Cập nhật option thành công", option));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("FAILED", "Không tìm thấy option với ID: " + id, "")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteOption(@PathVariable Long id) {
        boolean exists = optionRepository.existsById(id);
        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "Option not found", "")
            );
        }
        optionRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("ok", "Option deleted successfully", "")
        );
    }
    @PatchMapping("/options/{id}/quantity")
    public ResponseEntity<ResponseObject> updateProductOptionQuantity(@PathVariable Long id, @RequestParam int quantity) {
        Optional<ProductOption> foundOption =optionRepository.findById(id);

        if (foundOption.isPresent()) {
            ProductOption productOption = foundOption.get();
            productOption.setQuantity(quantity); // Cập nhật số lượng của ProductOption
            productOption.setUpdateAt(LocalDateTime.now()); // Cập nhật thời gian sửa đổi
            optionRepository.save(productOption);

            return ResponseEntity.ok(new ResponseObject("ok", "ProductOption quantity updated successfully", productOption));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("failed", "ProductOption not found with id: " + id, "")
            );
        }
    }


}
