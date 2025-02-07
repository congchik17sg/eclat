package com.example.eclat.service;

import com.example.eclat.entities.User;
import com.example.eclat.enums.Role;
import com.example.eclat.exception.AppException;
import com.example.eclat.exception.ErrorCode;
import com.example.eclat.mapper.UserMapper;
import com.example.eclat.model.request.UserCreationRequest;
import com.example.eclat.model.request.UserUpdateRequest;
import com.example.eclat.model.response.UserResponse;
import com.example.eclat.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserService {

    @Autowired
    final UserRepository userRepository;

    @Autowired
    final UserMapper userMapper;

    final PasswordEncoder passwordEncoder;

    private final EmailService emailService;


    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);
        if(userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.Customer.name());
        user.setRole(roles);

        // Mặc định chưa xác thực
        user.setStatus(false);
        userRepository.save(user);

        // Gửi email xác thực
        sendVerificationEmail(user);

        return userMapper.toUserResponse(user);
    }

    private void sendVerificationEmail(User user) {
        String verificationUrl = "http://localhost:8080/eclat/auth/verify?email=" + user.getEmail();
        emailService.sendVerificationEmail(user.getEmail(), verificationUrl);
    }

    public boolean verifyUser(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        User user = optionalUser.get();
        user.setStatus(true); // Đánh dấu là đã xác thực
        userRepository.save(user);
        return true;
    }

//    @PreAuthorize("hasRole('Admin')")
    public UserResponse createStaff(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.Staff.name());
        user.setRole(roles);

        // Mặc định chưa xác thực
        user.setStatus(true);
        userRepository.save(user);

//         Gửi email xác thực
//        sendVerificationEmail(user);

        return userMapper.toUserResponse(user);
    }


    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

//    @PreAuthorize("hasRole('Admin')")
    public List<UserResponse> getAllUser() {
        log.info("In method get users");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

//    @PreAuthorize("hasRole('Admin')")
    public UserResponse getUserById(String id) {
        log.info("In method getUserById");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found")));
    }

//    @PostAuthorize("returnObject.username == authentication.name ")
    public UserResponse updateUserById(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));

    }

//    @PreAuthorize("hasRole('Admin')")
    public void deleteUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(false);
        userRepository.save(user);
    }

}
