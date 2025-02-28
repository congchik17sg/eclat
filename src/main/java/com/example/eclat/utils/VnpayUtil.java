package com.example.eclat.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VnpayUtil {

    private static final String HASH_SECRET = "IPP9SVUOHPV01QLL279F6V72PXJZNMCZ";
    // Thay bằng Secret Key của bạn

    public static String hashAllFields(Map<String, String> fields, String secretKey) throws Exception {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames); // Sắp xếp tham số theo thứ tự tăng dần

        StringBuilder data = new StringBuilder();
        for (String field : fieldNames) {
            String value = fields.get(field);
            if (value != null && !value.isEmpty()) {
                data.append(field).append('=').append(value).append('&');
            }
        }

        // Xóa ký tự `&` cuối cùng
        if (data.length() > 0) {
            data.deleteCharAt(data.length() - 1);
        }

        return hmacSHA512(secretKey, data.toString());
    }

    private static String hmacSHA512(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac.init(secretKey);
        byte[] hashBytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hashHex = new StringBuilder();
        for (byte b : hashBytes) {
            hashHex.append(String.format("%02x", b));
        }
        return hashHex.toString();
    }
}
