package com.videoguard.config;

import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadsDir;
    private final AuthInterceptor authInterceptor;

    public WebConfig(@Value("${videoguard.uploads-dir:uploads}") String uploadsDir, AuthInterceptor authInterceptor) {
        this.uploadsDir = uploadsDir;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        List<String> uploadLocations = UploadPathResolver.candidateRoots(uploadsDir).stream()
                .map(Path::toUri)
                .map(uri -> uri.toString() + "/")
                .toList();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocations.toArray(String[]::new));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://127.0.0.1:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/auth/login", "/api/auth/register");
    }
}
