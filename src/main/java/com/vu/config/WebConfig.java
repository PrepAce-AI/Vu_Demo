package com.vu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình map đường dẫn web (/uploads/...) với thư mục thực tế trên máy tính
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}