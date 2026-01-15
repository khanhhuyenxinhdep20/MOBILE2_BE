package com.nguyenthithuhuyen.example10.controllers;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // Để Mobile App gọi không bị chặn
public class ChatController {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    @PostMapping
    public ResponseEntity<Map<String, Object>> getChatResponse(@RequestBody Map<String, String> request) {
        String userPrompt = request.get("prompt");
        
        RestTemplate restTemplate = new RestTemplate();
        
        // Cấu trúc JSON body theo yêu cầu của Google Gemini
        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", userPrompt)))
            )
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(GEMINI_URL + apiKey, body, Map.class);
            
            // Trích xuất text từ cấu trúc phức tạp của Gemini response
            // Cấu trúc mặc định: candidates[0].content.parts[0].text
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            String aiText = (String) firstPart.get("text");

            return ResponseEntity.ok(Map.of("text", aiText));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}