package com.ruben.Expedientes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactiva CSRF (si no es necesario)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permitir todas las solicitudes sin autenticación
                )
                .httpBasic(httpBasic -> httpBasic.disable()) // Desactiva autenticación básica
                .formLogin(formLogin -> formLogin.disable()); // Desactiva el formulario de inicio de sesión

        return http.build();
    }
}
