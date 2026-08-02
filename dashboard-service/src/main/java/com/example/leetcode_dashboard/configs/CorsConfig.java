package com.example.leetcode_dashboard.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    // Set ALLOWED_ORIGIN env variable on EC2 to your frontend EC2 public IP or domain
    // e.g. ALLOWED_ORIGIN=http://1.2.3.4 or ALLOWED_ORIGIN=https://yourdomain.com
    @Value("${ALLOWED_ORIGIN:http://localhost:*}")
    private String allowedOrigin;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOrigin, "http://localhost:*", "http://127.0.0.1:*")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
