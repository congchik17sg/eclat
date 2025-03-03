package com.example.eclat.service;

import com.example.eclat.entities.FeedBack;
import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.User;
import com.example.eclat.exception.AppException;
import com.example.eclat.exception.ErrorCode;
import com.example.eclat.model.request.quiz.FeedbackRequest;
import com.example.eclat.model.response.quiz.FeedbackResponse;
import com.example.eclat.repository.FeedbackRepository;
import com.example.eclat.repository.OrderDetailRepository;
import com.example.eclat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;

    public FeedbackResponse createFeedback(FeedbackRequest request) {

        // Kiểm tra xem người dùng có tồn tại không
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra xem OrderDetail có tồn tại không
        OrderDetail orderDetail = orderDetailRepository.findById(request.getOrderDetailId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy OrderDetail"));

        // Tạo đối tượng FeedBack mới
        FeedBack feedBack = FeedBack.builder()
                .text(request.getText())
                .rating(request.getRating())
                .user(user)
                .orderDetail(orderDetail)
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
                .orderDetailId(orderDetail.getOrderDetailId())
                .create_at(savedFeedBack.getCreate_at()) // Gán ngày tạo
                .update_at(savedFeedBack.getUpdate_at()) // Gán ngày cập nhật
                .build();
    }

    public List<FeedbackResponse> getAllFeedback() {
        return feedbackRepository.findAll().stream()
                .map(feedback -> FeedbackResponse.builder()
                        .text(feedback.getText())
                        .rating(feedback.getRating())
                        .username(feedback.getUser().getUsername())
                        .orderDetailId(feedback.getOrderDetail().getOrderDetailId())
                        .create_at(feedback.getCreate_at())
                        .update_at(feedback.getUpdate_at())
                        .build())
                .collect(Collectors.toList());
    }

    public List<FeedbackResponse> getFeedbackByUserId(String userId) {
        return feedbackRepository.findByUserId(userId).stream().map(feedback -> FeedbackResponse.builder()
                .text(feedback.getText())
                .rating(feedback.getRating())
                .username(feedback.getUser().getUsername())
                .orderDetailId(feedback.getOrderDetail().getOrderDetailId())
                .create_at(feedback.getCreate_at())
                .update_at(feedback.getUpdate_at())
                .build()).collect(Collectors.toList());
    }

    public FeedbackResponse updateFeedbackById(Long feedbackId, FeedbackRequest request) {
        FeedBack feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy feedback"));

        feedback.setText(request.getText());
        feedback.setRating(request.getRating());
        feedback.setUpdate_at(LocalDate.now());

        FeedBack updatedFeedback = feedbackRepository.save(feedback);

        return FeedbackResponse.builder()
                .text(updatedFeedback.getText())
                .rating(updatedFeedback.getRating())
                .username(updatedFeedback.getUser().getUsername())
                .orderDetailId(updatedFeedback.getOrderDetail().getOrderDetailId())
                .create_at(updatedFeedback.getCreate_at())
                .update_at(updatedFeedback.getUpdate_at())
                .build();
    }

    public void deleteFeedbackById(Long feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Không tìm thấy feedback để xoá");
        }
        feedbackRepository.deleteById(feedbackId);
    }
}
