package com.example.eclat.service;

import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.*;

@Service
public class VnPayService {

    private static final String VNPAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static final String TMN_CODE = "3OHAUFP3";
    private static final String HASH_SECRET = "H65JPP9358I22WXCKF16GHPTX3J0HSOW";
    private static final String RETURN_URL = "http://localhost:8080/eclat/api/payment/vnpay-return";

    public String createPaymentUrl(int amount, String orderInfo, String ipAddress, String txnRef) throws Exception {
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", TMN_CODE);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // ⚡ Nhân 100
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_CreateDate", getCurrentDateTime());
        vnp_Params.put("vnp_ExpireDate", getExpireDateTime());
        vnp_Params.put("vnp_ReturnUrl", RETURN_URL);

        // ⚡ Encode và hash
        String hashData = hashAllFields(vnp_Params, HASH_SECRET);
        vnp_Params.put("vnp_SecureHash", hashData);

        // 🏗️ Xây URL
        StringBuilder queryUrl = new StringBuilder(VNPAY_URL + "?");
        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
            queryUrl.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append("=")
                    .append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
        }
        return queryUrl.substring(0, queryUrl.length() - 1);
    }

    private String getCurrentDateTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"))
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private String getExpireDateTime() {
        return ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusMinutes(15)
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public String hashAllFields(Map<String, String> fields, String secretKey) throws Exception {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        for (String name : fieldNames) {
            String value = fields.get(name);
            if (value != null && !value.isEmpty()) {
                hashData.append(URLEncoder.encode(name, StandardCharsets.UTF_8)).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
            }
        }
        hashData.setLength(hashData.length() - 1); // 🔥 Xóa dấu & cuối cùng
        return hmacSHA512(secretKey, hashData.toString());
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKeySpec);
        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) hash.append(String.format("%02x", b));
        return hash.toString();
    }
}
