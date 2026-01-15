package com.nguyenthithuhuyen.example10.chat;
import lombok.Data;
import java.math.BigDecimal;    


@Data
public class ChatContext {
    private ChatIntent lastIntent;
    private String keyword;
    private BigDecimal maxPrice;
    private String size;
}
