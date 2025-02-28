package com.example.eclat.controller;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.Transaction;
import com.example.eclat.repository.TransactionRepository;
import com.example.eclat.service.OrderService;
import com.example.eclat.service.TransactionService;
import com.example.eclat.service.VnPayService;
import com.example.eclat.utils.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderService orderService;

    private static final String HASH_SECRET = "IPP9SVUOHPV01QLL279F6V72PXJZNMCZ";

    private static final Logger logger = LoggerFactory.getLogger(VnPayController.class);
    private VnpayUtil vnpayUtil;

    public VnPayController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/create")
    public String createPayment(@RequestParam int amount,
                                @RequestParam String orderInfo,
                                @RequestParam Long orderId,
                                HttpServletRequest request) throws Exception {
        String ipAddress = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }

        // Lấy thông tin đơn hàng
        Optional<Order> orderOpt = orderService.getOrderById(orderId);
        if (orderOpt.isEmpty()) {
            throw new IllegalArgumentException("Order không tồn tại!");
        }
        Order order = orderOpt.get();

        // Kiểm tra nếu đã có giao dịch cho đơn hàng này
        Optional<Transaction> existingTransaction = transactionRepository.findByOrder(order);
        if (existingTransaction.isPresent()) {
            throw new IllegalStateException("Đơn hàng này đã có giao dịch!");
        }

        // Tạo mã giao dịch duy nhất (txnRef)
        String txnRef = vnPayService.generateTxnRef(); // Viết hàm này trong VnPayService

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
        return vnPayService.createPaymentUrl(amount, orderInfo, ipAddress, txnRef);
    }


    @GetMapping("/vnpay-return")
    public ResponseEntity<?> handleReturnUrl(HttpServletRequest request) {
        try {
            // 🔹 Tạo bản sao Mutable của request.getParameterMap()
            Map<String, String[]> params = new HashMap<>(request.getParameterMap());

            String vnpTxnRef = request.getParameter("vnp_TxnRef");
            String vnpResponseCode = request.getParameter("vnp_ResponseCode");

            // 🔹 Xác thực chữ ký VNPAY
            boolean isValid = vnPayService.validateSignature(params);
            if (!isValid) {
                return ResponseEntity.badRequest().body("❌ Xác thực chữ ký không hợp lệ!");
            }

            // 🔹 Lấy giao dịch từ DB
            Optional<Transaction> transactionOpt = transactionRepository.findByVnpTxnRef(vnpTxnRef);
            if (transactionOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ Không tìm thấy giao dịch!");
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
                orderService.save(order);
            }

            // 🔹 Lưu transaction vào DB
            transactionRepository.save(transaction);

            return ResponseEntity.ok("✅ Thanh toán " + status);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ Lỗi hệ thống: " + e.getMessage());
        }
    }



}






