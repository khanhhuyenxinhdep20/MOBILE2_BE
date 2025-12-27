package com.nguyenthithuhuyen.example10.controllers;

import com.nguyenthithuhuyen.example10.security.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final CloudinaryService cloudinaryService;

    public UploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

@PostMapping("/image")
public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
    String url = cloudinaryService.uploadImage(file); // dùng đúng tên phương thức
    return ResponseEntity.ok(Map.of("url", url));
}
}
