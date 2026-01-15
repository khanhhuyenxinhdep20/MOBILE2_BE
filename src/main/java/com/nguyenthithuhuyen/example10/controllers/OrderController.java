package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.entity.Order;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import com.nguyenthithuhuyen.example10.security.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /* =====================================================
       ADMIN / MODERATOR – LẤY TẤT CẢ ORDER
       ===================================================== */
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    /* =====================================================
       USER / ADMIN – TẠO ORDER
       ===================================================== */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Order created = orderService.createOrder(order, username);
        return ResponseEntity.status(201).body(created);
    }

    /* =====================================================
       USER – LẤY ĐƠN HÀNG CỦA CHÍNH MÌNH
       ===================================================== */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(orderService.getOrdersByUsername(username));
    }

    /* =====================================================
       ADMIN / MODERATOR – UPDATE STATUS
       ===================================================== */
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, newStatus));
    }
}
