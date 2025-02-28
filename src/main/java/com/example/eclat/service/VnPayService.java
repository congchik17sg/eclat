package com.example.eclat.service;

import com.example.eclat.entities.Order;
import com.example.eclat.entities.Transaction;
import com.example.eclat.repository.OrderRepository;
import com.example.eclat.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
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
    private static final String IPN_URL = "http://localhost:8080/eclat/api/payment/ipn";

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OrderRepository orderRepository; // Repository để lấy thông tin Order

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
            queryUrl.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII)).append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII)).append("&");
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
        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = fields.get(name);
            if (value != null && !value.isEmpty()) {
                hashData.append(URLEncoder.encode(name, StandardCharsets.US_ASCII)).append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.US_ASCII));
                if (i < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        return hmacSHA512(secretKey, hashData.toString());
    }


//    public String hashAllFields(Map<String, String> fields, String secretKey) throws Exception {
//        List<String> fieldNames = new ArrayList<>(fields.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        for (String name : fieldNames) {
//            String value = fields.get(name);
//            if (value != null && !value.isEmpty()) {
//                hashData.append(URLEncoder.encode(name, StandardCharsets.UTF_8)).append('=')
//                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
//            }
//        }
//        hashData.setLength(hashData.length() - 1); // 🔥 Xóa dấu & cuối cùng
//        return hmacSHA512(secretKey, hashData.toString());
//    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKeySpec);
        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : hashBytes) hash.append(String.format("%02x", b));
        return hash.toString();
    }

    public boolean validateSignature(Map<String, String[]> params) throws Exception {
        String[] secureHashArr = params.get("vnp_SecureHash");
        if (secureHashArr == null || secureHashArr.length == 0) {
            throw new RuntimeException("Missing vnp_SecureHash parameter");
        }
        String vnp_SecureHash = secureHashArr[0];

        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        Map<String, String> vnp_Params = new HashMap<>();
        params.forEach((key, value) -> vnp_Params.put(key, value[0]));

        String signValue = hashAllFields(vnp_Params, HASH_SECRET);
        return signValue.equalsIgnoreCase(vnp_SecureHash);
    }


    // Hỗ trợ lấy tham số từ map
    private String getParamValue(Map<String, String[]> params, String key) {
        String[] values = params.get(key);
        if (values == null || values.length == 0) {
            throw new RuntimeException("Missing parameter: " + key);
        }
        return values[0];
    }
    public String generateTxnRef() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10); // Sinh mã ngẫu nhiên 10 ký tự
    }



}
