package com.example.eclat.controller;

import com.example.eclat.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @GetMapping("/create")
    public String createPayment(@RequestParam int amount,
                                @RequestParam String orderInfo,
                                @RequestParam String txnRef,
                                HttpServletRequest request) throws Exception {
        String ipAddress = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
        }
        return vnPayService.createPaymentUrl(amount ,orderInfo, ipAddress, txnRef);
    }



    @GetMapping("/vnpay-return")
    public ResponseEntity<?> handleReturnUrl(HttpServletRequest request) {
        try {
            // Truyền đúng kiểu Map<String, String[]> cho service
            Map<String, String[]> params = new HashMap<>(request.getParameterMap());

            boolean isValid = vnPayService.validateSignature(params);
            return isValid ? ResponseEntity.ok("✅ Giao dịch thành công!")
                    : ResponseEntity.badRequest().body("❌ Xác thực chữ ký không hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 9999);
            errorResponse.put("message", "Uncategorized Exception: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @RequestMapping(value = "/IPN", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, String>> handleIpnUrl(HttpServletRequest request) throws Exception {
        // Sao chép ParameterMap để tránh lỗi "locked"
        Map<String, String[]> params = new HashMap<>(request.getParameterMap());

        Map<String, String> response = vnPayService.processIpn(params);
        return ResponseEntity.ok(response);
    }




}
