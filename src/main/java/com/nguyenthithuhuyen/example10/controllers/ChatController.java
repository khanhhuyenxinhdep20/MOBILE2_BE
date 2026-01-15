package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.dto.ProductResponseDto;
import com.nguyenthithuhuyen.example10.security.services.ProductService;
import com.nguyenthithuhuyen.example10.security.services.CategoryService;
import com.nguyenthithuhuyen.example10.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String MODEL_NAME = "gemini-2.5-flash"; 
    private final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";

    @PostMapping
    public ResponseEntity<?> getChatResponse(@RequestBody Map<String, String> request) {
        String userPrompt = request.get("prompt");

        // 1. LẤY DỮ LIỆU TỪ DATABASE (Hàm hay dùng)
        // Lấy danh sách sản phẩm và chuyển thành chuỗi text đơn giản cho AI đọc
        String productInfo = productService.getAllProducts().stream()
                .map(p -> String.format("- %s: %s (Số lượng: %d)", p.getName(), p.getDescription(), p.getStockQuantity()))
                .collect(Collectors.joining("\n"));

        // Lấy danh sách danh mục
        String categoryInfo = categoryService.getAllCategories().stream()
                .map(c -> c.getName())
                .collect(Collectors.joining(", "));

        // 2. TẠO PROMPT "THÔNG MINH" (Tiêm ngữ cảnh)
        String systemContext = "Bạn là nhân viên tư vấn bán hàng chuyên nghiệp. " +
                "Dưới đây là danh sách sản phẩm chúng tôi có:\n" + productInfo + 
                "\nCác danh mục hàng: " + categoryInfo +
                "\n\nCâu hỏi của khách: " + userPrompt + 
                "\nTrả lời ngắn gọn, lịch sự dựa trên dữ liệu trên. Nếu không có sản phẩm khách tìm, hãy gợi ý sản phẩm tương tự.";

        // 3. GỌI API GEMINI
        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = BASE_URL + MODEL_NAME + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", systemContext)))
            )
        );

        try {
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(fullUrl, body, Map.class);
            Map response = responseEntity.getBody();

            // Trích xuất kết quả trả về
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            String aiText = (String) ((Map) parts.get(0)).get("text");

            return ResponseEntity.ok(Map.of("text", aiText));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}