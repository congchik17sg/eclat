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

    @PostMapping("/insert")
    public ResponseEntity<ResponseObject> insertOption(@RequestBody @Valid OptionRequest requestDTO) {
        Optional<Product> product = productRepository.findById(requestDTO.getProductId());

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
    public ResponseEntity<ResponseObject> updateOption(@RequestBody ProductOption newOption, @PathVariable Long id) {
        ProductOption updatedOption = optionRepository.findById(id)
                .map(option -> {
                    option.setOptionValue(newOption.getOptionValue());
                    option.setQuantity(newOption.getQuantity());
                    option.setOptionPrice(newOption.getOptionPrice());
                    option.setDiscPrice(newOption.getDiscPrice());
                    return optionRepository.save(option);
                }).orElseGet(() -> {
                    newOption.setOptionId(id);
                    return optionRepository.save(newOption);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Option updated successfully", updatedOption)
        );
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
}
