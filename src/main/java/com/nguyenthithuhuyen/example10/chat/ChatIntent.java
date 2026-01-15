package com.nguyenthithuhuyen.example10.chat;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ChatIntent {

    private String keyword;     // bánh kem, chocolate, matcha
    private BigDecimal maxPrice;   // 300000
    private Integer people;     // 2 người
    private String occasion;    // sinh nhật, kỷ niệm
}
