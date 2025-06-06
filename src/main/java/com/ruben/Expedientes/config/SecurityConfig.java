package com.ruben.Expedientes.config;

import com.ruben.Expedientes.filter.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtRequestFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        // ✅ ENDPOINTS PÚBLICOS
                        .requestMatchers("/api/login", "/api/register").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // ✅ WEBSOCKET ENDPOINTS (públicos para la conexión inicial)
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/app/**").permitAll()

                        // ✅ ENDPOINTS DE CHAT (requieren autenticación)
                        .requestMatchers("/api/chat/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/conversations").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/conversations/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat/messages").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat/messages/read/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/chat/messages/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/chat/search").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/users").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/unread-count").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/chat/statistics").authenticated()

                        // ✅ ENDPOINTS DE EXPEDIENTES (autenticados)
                        .requestMatchers("/api/expedientesprincipales/**").authenticated()
                        .requestMatchers("/api/expedientessecundarios/**").authenticated()
                        .requestMatchers("/api/peticionarios/**").authenticated()
                        .requestMatchers("/api/empresas/**").authenticated()
                        .requestMatchers("/api/clasificaciones/**").authenticated()
                        .requestMatchers("/api/departamentos/**").authenticated()
                        .requestMatchers("/api/estadosexpedientes/**").authenticated()

                        // ✅ ENDPOINTS DE ARCHIVOS (autenticados)
                        .requestMatchers(HttpMethod.POST, "/api/files/upload").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/files/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/files/**").authenticated()

                        // ✅ ENDPOINTS DE TICKETS (autenticados)
                        .requestMatchers("/api/tickets/**").authenticated()

                        // ✅ ENDPOINTS DE ADMINISTRACIÓN (solo ADMIN)
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/register").hasRole("ADMIN")
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // ✅ RESTO DE ENDPOINTS (requieren autenticación)
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir orígenes específicos - MEJORADO
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://143.131.204.234:*",
                "https://143.131.204.234:*",
                "*" // Temporal para debugging - cambiar en producción
        ));

        // Métodos permitidos
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"
        ));

        // Headers permitidos - AMPLIADO
        configuration.setAllowedHeaders(List.of(
                "Authorization", 
                "Content-Type", 
                "X-Requested-With", 
                "Accept", 
                "Origin", 
                "Access-Control-Request-Method", 
                "Access-Control-Request-Headers",
                "*"
        ));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Headers expuestos
        configuration.setExposedHeaders(List.of(
                "Authorization", 
                "Content-Disposition", 
                "Content-Type",
                "Content-Length"
        ));

        // Tiempo de caché para preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}