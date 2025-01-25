package com.ruben.Expedientes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Cambia esto si necesitas un mapeo diferente
                .allowedOrigins("http://raiben2123.zapto.org", "https://raiben2123.zapto.org") // Permite solo este dominio
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Métodos permitidos
                .allowedHeaders("*") // Permite todos los encabezados
                .allowCredentials(true); // Permite credenciales (si es necesario)
    }
}
