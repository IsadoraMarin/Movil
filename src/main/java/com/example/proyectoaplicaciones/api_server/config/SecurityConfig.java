package com.example.proyectoaplicaciones.api_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Este es el "hasher" de contraseñas que usaremos
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF, que no es necesario para una API REST sin estado
                .csrf(AbstractHttpConfigurer::disable)

                // Configurar las reglas de autorización de las peticiones HTTP
                .authorizeHttpRequests(authz -> authz
                        // Permitir que cualquiera acceda a los endpoints de registro y login
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()

                        // Permitir que cualquiera acceda a los endpoints de posts y comentarios por ahora
                        .requestMatchers("/api/posts/**").permitAll()

                        // Cualquier otra petición que no coincida con las reglas anteriores, debe ser denegada.
                        // (En un futuro, aquí se pondrían reglas para usuarios autenticados)
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}