package com.nguyenthithuhuyen.example10.payment;

import com.nguyenthithuhuyen.example10.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public String createPaymentUrl(Order order, String clientIp) {

        // ✅ DÙNG TREEMAP → AUTO SORT A-Z
        Map<String, String> params = new TreeMap<>();

        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);

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

        // ✅ IP HỢP LỆ
        params.put("vnp_IpAddr",
                (clientIp == null || clientIp.equals("127.0.0.1"))
                        ? "0.0.0.0"
                        : clientIp);

        params.put("vnp_ReturnUrl", returnUrl);

        params.put("vnp_CreateDate",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // ===== HASH DATA (KHÔNG ENCODE) =====
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                hashData.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        hashData.setLength(hashData.length() - 1);

        String secureHash = hmacSHA512(hashSecret, hashData.toString());

        // ===== QUERY STRING (CÓ ENCODE) =====
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .append("&");
            }
        }

        query.append("vnp_SecureHashType=HmacSHA512")
             .append("&vnp_SecureHash=")
             .append(secureHash);

        // DEBUG
        System.out.println("HASH DATA = " + hashData);
        System.out.println("PAY URL = " + payUrl + "?" + query);

        return payUrl + "?" + query;
    }
public String createPaymentUrl(Order order, String ipAddr) {

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
    params.put("vnp_IpAddr", ipAddr);
    params.put("vnp_ReturnUrl", returnUrl.trim());
    params.put("vnp_CreateDate",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

    // ===== SORT =====
    List<String> keys = new ArrayList<>(params.keySet());
    Collections.sort(keys);

    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();

    for (String key : keys) {
        String value = params.get(key);
        if (value != null && !value.isEmpty()) {

            String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);

            // ✅ HASH DATA (PHẢI ENCODE)
            hashData.append(key)
                    .append("=")
                    .append(encodedValue)
                    .append("&");

            // ✅ QUERY STRING
            query.append(key)
                    .append("=")
                    .append(encodedValue)
                    .append("&");
        }
    }

    hashData.setLength(hashData.length() - 1);
    query.setLength(query.length() - 1);

    String secureHash = hmacSHA512(hashSecret.trim(), hashData.toString());

    return payUrl
            + "?"
            + query
            + "&vnp_SecureHashType=HmacSHA512"
            + "&vnp_SecureHash="
            + secureHash;
}
    private String hmacSHA512(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : raw) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
