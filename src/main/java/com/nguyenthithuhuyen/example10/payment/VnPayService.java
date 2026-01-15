package com.nguyenthithuhuyen.example10.payment;

import com.nguyenthithuhuyen.example10.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class VnPayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.hashSecret}")
    private String hashSecret;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    public String createPaymentUrl(Order order) {

        Map<String, String> params = new HashMap<>();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode.trim());
        params.put("vnp_Amount",
                order.getFinalAmount()
                        .multiply(BigDecimal.valueOf(100))
                        .toBigInteger()
                        .toString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", String.valueOf(order.getId()));
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_IpAddr", "0.0.0.0");
        params.put("vnp_ReturnUrl", returnUrl.trim());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        params.put("vnp_CreateDate", LocalDateTime.now().format(formatter));

        // ===== SORT PARAMS =====
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        // ===== BUILD HASH DATA (KHÔNG ENCODE) =====
        // ===== BUILD HASH DATA + QUERY (ĐỀU ENCODE) =====
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String field : fieldNames) {
            String value = params.get(field);
            if (value != null && !value.isEmpty()) {

                String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);

                // HASH DATA (PHẢI ENCODE)
                hashData.append(field)
                        .append('=')
                        .append(encodedValue)
                        .append('&');

                // QUERY STRING
                query.append(field)
                        .append('=')
                        .append(encodedValue)
                        .append('&');
            }
        }

        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        // ===== HASH =====
        String secureHash = hmacSHA512(hashSecret.trim(), hashData.toString());

        // DEBUG
        // ===== DEBUG (RẤT QUAN TRỌNG) =====
        System.out.println("HASH DATA = " + hashData);
        System.out.println("PAY URL = " + payUrl + "?" + query + "&vnp_SecureHash=" + secureHash);

        return payUrl + "?" + query + "&vnp_SecureHash=" + secureHash;
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            mac.init(secretKey);
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : raw) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash error", e);
        }
    }
}
