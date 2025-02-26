package com.example.eclat.controller;

import com.example.eclat.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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



//    @GetMapping("/vnpay-return")
//    public String handleReturnUrl(HttpServletRequest request) throws Exception {
//        Map<String, String[]> params = request.getParameterMap();
//        boolean isValid = vnPayService.validateSignature(params);
//        return isValid ? "Giao dịch thành công! ✅" : "❌ Xác thực chữ ký không hợp lệ!";
//    }
//
//    @RequestMapping(value = "/vnpay-ipn", method = {RequestMethod.GET, RequestMethod.POST})
//    public String handleIpnUrl(HttpServletRequest request) throws Exception {
//        Map<String, String[]> params = request.getParameterMap();
//        params.forEach((key, value) -> System.out.println(key + " = " + String.join(",", value)));
//
//        boolean isValid = vnPayService.validateSignature(params);
//        return isValid ? "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}"
//                : "{\"RspCode\":\"97\",\"Message\":\"Invalid Signature\"}";
//    }
}
