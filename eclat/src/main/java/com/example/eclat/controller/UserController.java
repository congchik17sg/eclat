package com.example.eclat.controller;


import com.example.eclat.model.request.user.UserCreationRequest;
import com.example.eclat.model.request.user.UserUpdateEmailRequest;
import com.example.eclat.model.request.user.UserUpdatePasswordRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.user.UserResponse;
import com.example.eclat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PutMapping("/updateEmail/{userId}")
    @Operation(summary = "Cập nhật email người dùng và số điện thoại ( có xác thực lại mail mới ) ")
    ApiResponse<UserResponse> updateEmailUser(@PathVariable String userId, @RequestBody UserUpdateEmailRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserEmailById(userId, request))
                .build();
    }

    @PutMapping("/updatePassword/{userId}")
    @Operation(summary = "Cập nhật mật khẩu mới ( có gửi mail thông báo ) ")
    ApiResponse<UserResponse> updatePasswordUser(@PathVariable String userId, @RequestBody UserUpdatePasswordRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUserPasswordById(userId , request))
                .build();
    }



    @DeleteMapping("/{userId}")
    @Operation(summary = "Message trả deleted nhưng chỉ set account disable ")
    ApiResponse<String> deleteUser(@PathVariable String userId) {
        userService.deleteUserById(userId);
        return ApiResponse.<String>builder().result("User has been deleted").build();
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "bước 1 gửi email quên password ")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.sendResetPasswordOtp(email);
        return ResponseEntity.ok("OTP đã được gửi đến email của bạn.");
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "bước 2 xác thực otp ")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = userService.verifyOtp(email, otp);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP không hợp lệ.");
        }
        return ResponseEntity.ok("OTP hợp lệ.");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "bước 3 nhập pass mới ")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String otp,
                                                @RequestParam String newPassword) {
        userService.resetPassword(email, otp, newPassword);
        return ResponseEntity.ok("Mật khẩu đã được đặt lại thành công.");
    }


}
