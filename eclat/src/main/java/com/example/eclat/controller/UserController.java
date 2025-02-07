package com.example.eclat.controller;


import com.example.eclat.entities.User;
import com.example.eclat.model.request.UserCreationRequest;
import com.example.eclat.model.request.UserUpdateRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.UserResponse;
import com.example.eclat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@Tag(name = "User API", description = "API for managing users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    @Operation(summary = "Tạo tài khoản cho Staff ")
    ApiResponse<UserResponse> createStaff(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createStaff(request))
                .build();
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả người dùng ")
    ApiResponse<List<UserResponse>> getAllUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority ->
                log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUser())
                .build();
    }

    @GetMapping("/{userId}")
    ApiResponse<UserResponse> getUser(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    @GetMapping("/myInfo")
    ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/{userId}")
    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserById(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Message trả deleted nhưng chỉ set account disable ")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }


}
