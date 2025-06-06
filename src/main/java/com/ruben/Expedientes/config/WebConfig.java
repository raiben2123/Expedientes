package com.ruben.Expedientes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*", 
                        "http://143.131.204.234:*",
                        "https://143.131.204.234:*"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders(
                        "Authorization", 
                        "Content-Type", 
                        "X-Requested-With", 
                        "Accept", 
                        "Origin", 
                        "Access-Control-Request-Method", 
                        "Access-Control-Request-Headers"
                )
                .allowCredentials(true)
                .exposedHeaders("Authorization", "Content-Disposition", "Content-Type", "Content-Length")
                .maxAge(3600);
    }
}