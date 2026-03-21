package com.codedemonbr.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:3000"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // Permite o H2 Console (iframe)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

                .authorizeHttpRequests(auth -> auth
                        // === Endpoints públicos ===
                        .requestMatchers("/hello").permitAll()
                        .requestMatchers("/jwt-test/**").permitAll()
                        .requestMatchers("/users/**").permitAll()        // cadastro
                        .requestMatchers("/login").permitAll()        // futuro login

                        // === Documentação ===
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()

                        // === H2 Console - IMPORTANTE ===
                        .requestMatchers("/h2-console/**").permitAll()

                        // === Actuator ===
                        .requestMatchers("/actuator/**").permitAll()

                        // === Erros ===
                        .requestMatchers("/error").permitAll()

                        // Tudo o resto exige autenticação
                        .anyRequest().authenticated()
                )

                // Desabilita CSRF apenas para desenvolvimento
                .csrf(csrf -> csrf.disable())

                // Desabilita login padrão e Basic Auth
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
}
