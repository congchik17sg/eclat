package com.example.eclat.service;

import com.example.eclat.entities.User;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {

    @Autowired
    final UserRepository userRepository;

    @Autowired
    final UserMapper userMapper;

    public User createUser(UserCreationRequest request) {


        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public UserResponse getUserById(String id) {
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("user not found")));
    }

    public UserResponse updateUserById(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));


        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));

    }

    public void deleteUserById(String userId) {
        userRepository.deleteById(userId);
    }

}
