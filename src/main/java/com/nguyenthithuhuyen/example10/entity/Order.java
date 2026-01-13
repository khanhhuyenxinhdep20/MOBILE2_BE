package com.nguyenthithuhuyen.example10.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.nguyenthithuhuyen.example10.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ================= USER ================= */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    /* ================= STATUS ================= */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /* ================= CUSTOMER INFO ================= */
    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(length = 255)
    private String note;

    /* ================= PAYMENT ================= */
    @Column(nullable = false)
    private String paymentMethod; // cash | momo | bank

    /* ================= MONEY ================= */
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "discount", precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "final_amount", precision = 10, scale = 2)
    private BigDecimal finalAmount = BigDecimal.ZERO;

    /* ================= PROMOTION ================= */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Promotion promotion;

    /* ================= ITEMS ================= */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    /* ================= AUDIT ================= */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /* ================= LIFECYCLE ================= */
    @PrePersist
    protected void onCreate() {
        calculateFinalAmount();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateFinalAmount();
        updatedAt = LocalDateTime.now();
    }

    /* ================= HELPER ================= */
    private void calculateFinalAmount() {
        if (totalAmount == null) totalAmount = BigDecimal.ZERO;
        if (discount == null) discount = BigDecimal.ZERO;
        finalAmount = totalAmount.subtract(discount);
    }
}