package com.example.eclat.controller;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.OrderDetail;
import com.example.eclat.entities.ProductOption;
import com.example.eclat.entities.Transaction;
import com.example.eclat.model.response.TransactionResponse;
import com.example.eclat.repository.OptionRepository;
import com.example.eclat.repository.TransactionRepository;
import com.example.eclat.service.OrderService;
import com.example.eclat.service.TransactionService;
import com.example.eclat.service.VnPayService;
import com.example.eclat.utils.VnpayUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Vnpay api ")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private OptionRepository productOptionRepository;

    @Autowired
    private OrderService orderService;

    private static final String HASH_SECRET = "IPP9SVUOHPV01QLL279F6V72PXJZNMCZ";

    private static final Logger logger = LoggerFactory.getLogger(VnPayController.class);
    private VnpayUtil vnpayUtil;

    public VnPayController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/create")
    public ResponseEntity<?> createPayment(@RequestParam int amount,
                                           @RequestParam String orderInfo,
                                           @RequestParam Long orderId,
                                           HttpServletRequest request) throws Exception {
        try {
            String ipAddress = request.getRemoteAddr();
            if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
                ipAddress = "127.0.0.1";
            }

            // Lấy thông tin đơn hàng
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "❌ Order không tồn tại!"));
            }

            Order order = orderOpt.get();

            // Kiểm tra nếu đơn hàng đã có giao dịch thành công
            Optional<Transaction> existingTransaction = transactionRepository.findByOrder(order);
            if (existingTransaction.isPresent() && "SUCCESS".equals(existingTransaction.get().getTransactionStatus())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "❌ Đơn hàng đã được thanh toán!"));
            }

            // Tạo mã giao dịch duy nhất (txnRef)
            String txnRef = vnPayService.generateTxnRef();

            // Tạo transaction trạng thái PENDING
            Transaction transaction = Transaction.builder()
                    .order(order)
                    .amount(BigDecimal.valueOf(amount))
                    .transactionStatus("PENDING")
                    .paymentMethod("VNPAY")
                    .vnpTxnRef(txnRef)
                    .createAt(LocalDateTime.now())
                    .build();

            transactionRepository.save(transaction);

            // Tạo URL thanh toán và trả về
            String paymentUrl = vnPayService.createPaymentUrl(amount, orderInfo, ipAddress, txnRef);
            return ResponseEntity.ok(Map.of("paymentUrl", paymentUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "❌ Lỗi hệ thống: " + e.getMessage()));
        }
    }


    @GetMapping("/vnpay-return")
    public ResponseEntity<?> handleReturnUrl(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 🔹 Tạo bản sao Mutable của request.getParameterMap()
            Map<String, String[]> params = new HashMap<>(request.getParameterMap());

            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            String vnpResponseCode = request.getParameter("vnp_ResponseCode");

            // 🔹 Xác thực chữ ký VNPAY
            boolean isValid = vnPayService.validateSignature(params);
            if (!isValid) {
                response.sendRedirect("http://localhost:5173/payment-failed");
                return ResponseEntity.badRequest().body("Invalid signature");
            }

            // 🔹 Lấy giao dịch từ DB
            Optional<Transaction> transactionOpt = transactionRepository.findByVnpTxnRef(vnpTxnRef);
            if (transactionOpt.isEmpty()) {
                response.sendRedirect("http://localhost:5173/payment-not-found");
                return ResponseEntity.badRequest().body("Transaction not found");
            }

            Transaction transaction = transactionOpt.get();
            Order order = transaction.getOrder();

            // 🔹 Xác định trạng thái giao dịch
            String status = "00".equals(vnpResponseCode) ? "SUCCESS" : "FAILED";
            transaction.setTransactionStatus(status);
            transaction.setVnpResponseCode(vnpResponseCode);

            // 🔹 Cập nhật trạng thái đơn hàng nếu thanh toán thành công
            if ("SUCCESS".equals(status)) {
                order.setStatus("PAID");

                // 🔹 Giảm số lượng sản phẩm trong ProductOption
                for (OrderDetail orderDetail : order.getOrderDetails()) {
                    ProductOption productOption = orderDetail.getProductOption();
                    int newQuantity = productOption.getQuantity() - orderDetail.getQuantity();

                    if (newQuantity < 0) {
                    response.sendRedirect("http://localhost:5173/payment-failed");
                    return null;
                }
                    productOption.setQuantity(newQuantity);
                    productOptionRepository.save(productOption);
                }

                orderService.save(order);
            }

            // 🔹 Lưu transaction vào DB
            transactionRepository.save(transaction);

            response.sendRedirect("http://localhost:5173/payment-success?orderId=" + order.getOrderId());
            return null;
        } catch (Exception e) {
          e.printStackTrace();
          try {
            response.sendRedirect("http://localhost:5173/payment-error");
          } catch (Exception ignored) {}
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error");
        }
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        Optional<TransactionResponse> transaction = transactionService.getTransactionById(transactionId);
        return transaction.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(transactionService.getTransactionsByUserId(userId));
    }
}






