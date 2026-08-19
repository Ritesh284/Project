package com.carrental.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "https://car-rental-swart-chi.vercel.app",
                        "http://localhost:5500",
                        "http://127.0.0.1:5500",
                        "http://localhost:3000",
                        "http://localhost:8080",
                        "http://127.0.0.1:8080"
                )
                .allowedOriginPatterns(
                        "https://car-rental-swart-chi.vercel.app",
                        "https://*.vercel.app",
                        "http://localhost:*",
                        "http://127.0.0.1:*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/admin").setViewName("forward:/admin.html");
        registry.addViewController("/admin/").setViewName("forward:/admin.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Uploaded images
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }
        String uploadPath = uploadFolder.getAbsolutePath().replace("\\", "/");
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);

        // Project root static folder fallback
        File rootFolder = new File("../");
        String rootPath = rootFolder.getAbsolutePath().replace("\\", "/");
        if (!rootPath.endsWith("/")) {
            rootPath += "/";
        }

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "classpath:/public/",
                        "file:" + rootPath,
                        "file:" + uploadPath
                );
    }
}
