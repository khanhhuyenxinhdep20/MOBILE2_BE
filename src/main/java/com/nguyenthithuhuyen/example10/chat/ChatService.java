package com.nguyenthithuhuyen.example10.chat;

import com.nguyenthithuhuyen.example10.dto.ProductResponseDto;
import com.nguyenthithuhuyen.example10.mapper.ProductMapper;
import com.nguyenthithuhuyen.example10.payload.response.ChatResponse;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import com.nguyenthithuhuyen.example10.security.services.GeminiService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final GeminiService geminiService;
    private final ProductRepository productRepo;

    public ChatResponse handleChat(String message) {

        Map<String, Object> ai = geminiService.askGeminiForIntent(message);
        String intent = ai.getOrDefault("intent", "UNKNOWN").toString();

        String keyword = (String) ai.get("keyword");
        BigDecimal maxPrice = null;

        if (ai.get("maxPrice") != null) {
            maxPrice = new BigDecimal(ai.get("maxPrice").toString());
        }

        /* ===== SHOW / FILTER ===== */
        if (intent.equals("SHOW_PRODUCTS") || intent.equals("FILTER_PRICE")) {

            List<ProductResponseDto> products =
                productRepo.searchByChat(
                        keyword,
                        maxPrice,
                        PageRequest.of(0, 5)
                )
                .stream()
                .map(ProductMapper::toResponse)
                .toList();

            if (products.isEmpty()) {
                return ChatResponse.text(
                    "Dạ hiện chưa có bánh phù hợp mức giá này 😥"
                );
            }

            return ChatResponse.products(
                "Em gợi ý vài mẫu bánh phù hợp cho bạn nè 🍰",
                products
            );
        }

        if (intent.equals("TRACK_ORDER")) {
            return ChatResponse.text(
                "Bạn gửi giúp em mã đơn hàng để em kiểm tra nha 📦"
            );
        }

        return ChatResponse.text(
            "Bạn muốn tìm bánh theo giá, hương vị hay dịp ạ? 😊"
        );
    }
}
