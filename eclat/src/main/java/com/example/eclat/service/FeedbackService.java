package com.example.eclat.service;

import com.example.eclat.entities.FeedBack;
import com.example.eclat.entities.Product;
import com.example.eclat.entities.User;
import com.example.eclat.exception.AppException;
import com.example.eclat.exception.ErrorCode;
import com.example.eclat.model.request.quiz.FeedbackRequest;
import com.example.eclat.model.response.quiz.FeedbackResponse;
import com.example.eclat.repository.FeedbackRepository;
import com.example.eclat.repository.ProductRepository;
import com.example.eclat.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
public class FeedbackService {


    FeedbackRepository feedbackRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public FeedbackResponse createFeedback(FeedbackRequest request) {

        // Kiểm tra xem người dùng có tồn tại không
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra xem sản phẩm có tồn tại không
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Tạo đối tượng FeedBack mới
        FeedBack feedBack = FeedBack.builder()
                .text(request.getText())
                .rating(request.getRating())
                .user(user)
                .product(product)
                .create_at(LocalDate.now()) // Gán ngày tạo
                .update_at(LocalDate.now()) // Gán ngày cập nhật
                .build();

        // Lưu vào database
        FeedBack savedFeedBack = feedbackRepository.save(feedBack);

        // Trả về phản hồi với đầy đủ thông tin
        return FeedbackResponse.builder()
                .text(savedFeedBack.getText())
                .rating(savedFeedBack.getRating())
                .username(user.getUsername())
                .productname(product.getProductName())
                .create_at(savedFeedBack.getCreate_at()) // Gán ngày tạo
                .update_at(savedFeedBack.getUpdate_at()) // Gán ngày cập nhật
                .build();


    }

}
