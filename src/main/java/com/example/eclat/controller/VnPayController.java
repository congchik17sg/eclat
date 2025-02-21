package com.example.eclat.controller;


import com.example.eclat.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @GetMapping("/create")
    public String createPayment(@RequestParam long amount, @RequestParam String orderInfo, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        return vnPayService.createPaymentUrl(amount, orderInfo, ipAddress);
    }

}
