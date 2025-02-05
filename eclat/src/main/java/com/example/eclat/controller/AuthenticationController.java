package com.example.eclat.controller;

import com.example.eclat.model.request.AuthenticationRequest;
import com.example.eclat.model.request.IntrospectRequest;
import com.example.eclat.model.request.UserCreationRequest;
import com.example.eclat.model.response.ApiResponse;
import com.example.eclat.model.response.AuthenticationResponse;
import com.example.eclat.model.response.IntrospectResponse;
import com.example.eclat.model.response.UserResponse;
import com.example.eclat.service.AuthenticationService;
import com.example.eclat.service.UserService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Authentication API", description = "API authen and author")
public class AuthenticationController {

    AuthenticationService authenticationService;
    private final UserService userService;


    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/log-in")
    ApiResponse<AuthenticationResponse> authenticationResponse(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticationResponse(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }


    @GetMapping("/verify")
    @Operation(summary = "Chỉ để xác thực gmail , fe ko cần sử dụng ")
    public ResponseEntity<String> verifyUser(@RequestParam String email) {
        boolean verified = userService.verifyUser(email);
        if (verified) {
            return ResponseEntity.ok("Xác thực tài khoản thành công!");
        }
        return ResponseEntity.badRequest().body("Xác thực thất bại!");
    }
}
