package com.example.eclat.service;

import com.example.eclat.entities.User;
import com.example.eclat.enums.Role;
import com.example.eclat.exception.AppException;
import com.example.eclat.exception.ErrorCode;
import com.example.eclat.mapper.UserMapper;
import com.example.eclat.model.request.user.UserCreationRequest;
import com.example.eclat.model.request.user.UserUpdateEmailRequest;
import com.example.eclat.model.request.user.UserUpdatePasswordRequest;
import com.example.eclat.model.response.user.UserResponse;
import com.example.eclat.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


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

    final EmailService emailService;

    // Lưu OTP tạm thời (chỉ nên dùng cache, Redis hoặc database thay thế)
    private static final ConcurrentHashMap<String, String> otpStorage = new ConcurrentHashMap<>();



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
    public UserResponse updateUserEmailById(String userId, UserUpdateEmailRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("user not found"));

        userMapper.updateUser(user, request);
        user.setStatus(false);
        userRepository.save(user);
        sendVerificationEmail(user);
//        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userMapper.toUserResponse(userRepository.save(user));

    }

    public UserResponse updateUserPasswordById(String userId, UserUpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(request.getOld_password(), user.getPassword())) {
            throw new AppException(ErrorCode.INCORRECT_PASSWORD);
        }

        // Mã hóa mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNew_password()));

        // Cập nhật trạng thái xác thực lại nếu cần
        user.setStatus(true);
        userRepository.save(user);

        // Gửi email thông báo thay đổi mật khẩu (tuỳ chọn)
        emailService.sendPasswordChangeNotification(user.getEmail());

        return userMapper.toUserResponse(user);
    }




    //    @PreAuthorize("hasRole('Admin')")
    public void deleteUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(false);
        userRepository.save(user);
    }

    // Tạo mã OTP 6 ký tự
    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    // Gửi OTP qua email
    public void sendResetPasswordOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        String otp = generateOtp();
        otpStorage.put(email, otp);

        String subject = "Mã OTP đặt lại mật khẩu";
        String content = "Mã OTP của bạn là: <b>" + otp + "</b>. Vui lòng không chia sẻ với ai.";
        emailService.sendEmail(email, subject, content);
    }

    // Xác minh OTP
    public boolean verifyOtp(String email, String otp) {
        return otp.equals(otpStorage.get(email));
    }

    // Đặt lại mật khẩu mới
    public void resetPassword(String email, String otp, String newPassword) {
        if (!verifyOtp(email, otp)) {
            throw new AppException(ErrorCode.INVALID_OTP);
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setStatus(true);
        userRepository.save(user);

        otpStorage.remove(email); // Xóa OTP sau khi sử dụng
    }

}
