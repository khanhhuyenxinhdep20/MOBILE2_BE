package com.nguyenthithuhuyen.example10.payment;

import com.nguyenthithuhuyen.example10.entity.Order;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import com.nguyenthithuhuyen.example10.repository.OrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final OrderRepository orderRepository;
    private final VnPayService vnPayService;

    // ================== TẠO LINK THANH TOÁN ==================
    @PostMapping("/vnpay/{orderId}")
    public Map<String, String> payByVnPay(
            @PathVariable Long orderId,
            HttpServletRequest request
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order not payable");
        }

        order.setPaymentMethod("VNPAY");
        orderRepository.save(order);

        // ✅ LẤY IP THẬT
        String ip = request.getRemoteAddr();

        String paymentUrl = vnPayService.createPaymentUrl(order, ip);

        return Map.of("paymentUrl", paymentUrl);
    }

    // ================== CALLBACK VNPAY ==================
    @GetMapping("/vnpay-return")
    public String vnpayReturn(
            @RequestParam Map<String, String> params
    ) {

        // 1️⃣ VERIFY CHỮ KÝ
        boolean valid = vnPayService.verifyCallback(params);
        if (!valid) {
            return "Invalid VNPay signature";
        }

        // 2️⃣ LẤY DATA
        Long orderId = Long.valueOf(params.get("vnp_TxnRef"));
        String responseCode = params.get("vnp_ResponseCode");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 3️⃣ CẬP NHẬT TRẠNG THÁI
        if ("00".equals(responseCode)) {
            order.setStatus(OrderStatus.PAID);
        } else {
            order.setStatus(OrderStatus.CANCELLED);
        }

        orderRepository.save(order);

        // 4️⃣ TRẢ KẾT QUẢ (có thể redirect frontend)
        return "Payment result: " + order.getStatus();
    }
}
