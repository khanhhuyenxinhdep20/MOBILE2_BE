package com.nguyenthithuhuyen.example10.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        // Tự động đọc CLOUDINARY_URL từ environment
        return new Cloudinary(System.getenv("CLOUDINARY_URL"));
    }
}
