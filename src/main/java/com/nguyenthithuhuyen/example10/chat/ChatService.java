package com.nguyenthithuhuyen.example10.chat;

import com.nguyenthithuhuyen.example10.dto.ProductResponseDto;
import com.nguyenthithuhuyen.example10.mapper.ProductMapper;
import com.nguyenthithuhuyen.example10.payload.response.ChatResponse;
import com.nguyenthithuhuyen.example10.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ProductRepository productRepo;

    public ChatResponse handleChat(String prompt) {

        ChatIntent intent = parseIntent(prompt);

        List<ProductResponseDto> products = productRepo
                .searchByChat(
                        intent.getKeyword(),
                        intent.getMaxPrice(),
                        PageRequest.of(0, 5)
                )
                .stream()
                .map(ProductMapper::toResponse)
                .toList();

        String reply = buildReply(intent, products);

        return new ChatResponse(reply, products);
    }

    /* ================= PARSE CHAT ================= */

    private ChatIntent parseIntent(String prompt) {

        ChatIntent intent = new ChatIntent();
        String text = prompt.toLowerCase();

        // 🎂 dịp
        if (text.contains("sinh nhật")) intent.setOccasion("sinh nhật");

        // 👥 số người
        if (text.contains("2 người")) intent.setPeople(2);
        if (text.contains("4 người")) intent.setPeople(4);

        // 💰 giá
        intent.setMaxPrice(extractPrice(text));

        // 🍰 keyword
        if (text.contains("socola") || text.contains("chocolate"))
            intent.setKeyword("socola");
        else if (text.contains("matcha") || text.contains("trà xanh"))
            intent.setKeyword("trà xanh");
        else
            intent.setKeyword("bánh");

        return intent;
    }

    private BigDecimal extractPrice(String text) {
        try {
            if (text.contains("k")) {
                String num = text.replaceAll("\\D+", "");
                return new BigDecimal(num).multiply(BigDecimal.valueOf(1000));
            }
        } catch (Exception ignored) {}
        return BigDecimal.valueOf(500_000);
    }

    /* ================= REPLY ================= */

    private String buildReply(ChatIntent intent, List<ProductResponseDto> products) {

        if (products.isEmpty()) {
            return "Dạ quán chưa có bánh phù hợp mức giá này 😥 "
                 + "Bạn tăng ngân sách giúp em nha 💕";
        }

        return "Dạ em gợi ý vài mẫu bánh "
                + intent.getKeyword()
                + " phù hợp cho "
                + (intent.getOccasion() != null ? intent.getOccasion() : "dịp của bạn")
                + " 🍰\n"
                + "Bánh có nhiều size, ghi chữ miễn phí ạ 💖";
    }
}
