package com.example.eclat.service;

import com.example.eclat.entities.*;
import com.example.eclat.exception.AppException;
import com.example.eclat.exception.ErrorCode;
import com.example.eclat.model.request.quiz.FeedbackRequest;
import com.example.eclat.model.response.*;
import com.example.eclat.model.response.quiz.FeedbackResponse;
import com.example.eclat.repository.FeedbackRepository;
import com.example.eclat.repository.OrderDetailRepository;
import com.example.eclat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .create_at(LocalDateTime.now()) // Gán ngày tạo
                .update_at(LocalDateTime.now()) // Gán ngày cập nhật
                .build();

        // Lưu vào database
        FeedBack savedFeedBack = feedbackRepository.save(feedBack);

        // Trả về phản hồi với đầy đủ thông tin
        return FeedbackResponse.builder()
                .text(savedFeedBack.getText())
                .rating(savedFeedBack.getRating())
                .username(user.getUsername())
                .orderDetailId(orderDetail.getOrderDetailId())
                .createAt(savedFeedBack.getCreate_at()) // Gán ngày tạo
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
                        .createAt(feedback.getCreate_at())
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
                .createAt(feedback.getCreate_at())
                .update_at(feedback.getUpdate_at())
                .build()).collect(Collectors.toList());
    }

    public FeedbackResponse updateFeedbackById(Long feedbackId, FeedbackRequest request) {
        FeedBack feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy feedback"));

        feedback.setText(request.getText());
        feedback.setRating(request.getRating());
        feedback.setUpdate_at(LocalDateTime.now());

        FeedBack updatedFeedback = feedbackRepository.save(feedback);

        return FeedbackResponse.builder()
                .text(updatedFeedback.getText())
                .rating(updatedFeedback.getRating())
                .username(updatedFeedback.getUser().getUsername())
                .orderDetailId(updatedFeedback.getOrderDetail().getOrderDetailId())
                .createAt(updatedFeedback.getCreate_at())
                .update_at(updatedFeedback.getUpdate_at())
                .build();
    }

    public void deleteFeedbackById(Long feedbackId) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw new RuntimeException("Không tìm thấy feedback để xoá");
        }
        feedbackRepository.deleteById(feedbackId);
    }

//    public List<FeedbackResponse> getFeedbackByProductId(Long productId) {
//        List<FeedBack> feedbacks = feedbackRepository.findByOrderDetail_ProductOption_Product_ProductId(productId);
//
//        return feedbacks.stream().map(feedback -> {
//            Product product = feedback.getOrderDetail().getProductOption().getProduct();
//            ProductResponseV2 productResponseV2 = mapToProductResponseV2(product);
//
//            return FeedbackResponse.builder()
//                    .feedbackId(feedback.getFeedback_id()) // Sửa lỗi đặt tên
//                    .text(feedback.getText())
//                    .userId(feedback.getUser() != null ? feedback.getUser().getId().toString() : null) // Kiểm tra null tránh lỗi
//                    .username(feedback.getUser().getUsername())
//                    .rating(feedback.getRating())
//                    .createAt(feedback.getCreate_at())
//                    .update_at(feedback.getUpdate_at())
//                    .orderDetailId(feedback.getOrderDetail().getOrderDetailId())
//                    .product(productResponseV2) // Gán product vào response
//                    .build();
//        }).toList();
//    }


    public List<FeedbackResponseV3> getFeedbackByProductIdV3(Long productId) {
        List<FeedBack> feedbacks = feedbackRepository.findByOrderDetail_ProductOption_Product_ProductId(productId);

        return feedbacks.stream().map(feedback -> {
            // Lấy thông tin sản phẩm
            Product product = feedback.getOrderDetail().getProductOption().getProduct();

            // Tạo đối tượng ProductResponseV3 (chỉ chứa productId và productName)
            ProductResponseV3 productResponse = ProductResponseV3.builder()
                    .productId(product.getProductId())
                    .productName(product.getProductName())
                    .build();

            // Lấy thông tin OrderDetail
            OrderDetail orderDetail = feedback.getOrderDetail();
            OrderDetailResponse orderDetailResponse = OrderDetailResponse.builder()
                    .orderDetailId(orderDetail.getOrderDetailId())
                    .orderDetailId(orderDetail.getOrder().getOrderId())
                    .quantity(orderDetail.getQuantity())
                    .price(orderDetail.getPrice())
                    .optionValue(orderDetail.getProductOption().getOptionValue())
//                    .(orderDetail.getOrderDate())
                    .optionId(orderDetail.getProductOption().getOptionId())
                    .build();

            return FeedbackResponseV3.builder()
                    .feedbackId(feedback.getFeedback_id()) // Sửa lỗi đặt tên
                    .text(feedback.getText())
                    .userId(feedback.getUser() != null ? feedback.getUser().getId().toString() : null) // Kiểm tra null tránh lỗi
                    .username(feedback.getUser().getUsername())
                    .rating(feedback.getRating())
                    .createAt(feedback.getCreate_at())
                    .update_at(feedback.getUpdate_at())
                    .orderDetail(orderDetailResponse) // Gán orderDetail đầy đủ vào response
                    .product(productResponse) // Gán product chỉ có productId và productName
                    .build();
        }).toList();
    }


    private ProductResponseV2 mapToProductResponseV2(Product product) {
        ProductResponseV2 productResponse = ProductResponseV2.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .usageInstruct(product.getUsageInstruct())
                .originCountry(product.getOriginCountry())
                .createAt(product.getCreateAt())
                .updateAt(product.getUpdateAt())
                .status(product.getStatus())
                .tag(product.getTag())
                .brand(product.getBrand())
                .skinType(product.getSkinType())
                .images(product.getImages().stream()
                        .map(Image::getImageUrl)
                        .toList())
                .attribute(product.getAttribute())
                .build();

        // Chuyển đổi danh sách options
        List<OptionResponseV2> optionResponses = product.getOptions().stream()
                .map(option -> new OptionResponseV2(
                        option.getOptionId(),
                        option.getOptionValue(),
                        option.getQuantity(),
                        option.getOptionPrice(),
                        option.getDiscPrice(),
                        option.getCreateAt(),
                        option.getUpdateAt(),
                        option.getImages().stream().map(Image::getImageUrl).toList(),
                        productResponse // Gán ProductResponseV2 vào OptionResponseV2
                ))
                .toList();

        productResponse.setOptions(optionResponses);

        return productResponse;
    }







}
