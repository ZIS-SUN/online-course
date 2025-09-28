package com.example.learn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configure static resource mapping for uploaded videos
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/Users/zishen/Desktop/文件夹/毕业设计/zaixian/online-course-backend/uploads/")
                .setCachePeriod(3600) // Cache for 1 hour
                .resourceChain(true);
    }
}