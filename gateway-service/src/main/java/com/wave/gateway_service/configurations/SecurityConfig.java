package com.wave.gateway_service.configurations;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityWebFilterChain security(ServerHttpSecurity http) {
        return http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/site/**").permitAll()
                .pathMatchers("/static/**").permitAll()
                .pathMatchers("/auth/**").permitAll()
                .pathMatchers("/telegram/**").permitAll()
                .pathMatchers("/notification/notifications/send").permitAll()
                .pathMatchers(HttpMethod.POST, "/callback/message").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthConverter.converter())
                )
            )
        .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration config = new CorsConfiguration();

       config.setAllowedOrigins(List.of(
           "https://192.168.1.10:9443"));
       config.addAllowedMethod("*"); // Разрешить GET, POST, OPTIONS и т.д.
       config.addAllowedHeader("*");
       config.setAllowCredentials(true);

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", config);
       return source;
    }
}
