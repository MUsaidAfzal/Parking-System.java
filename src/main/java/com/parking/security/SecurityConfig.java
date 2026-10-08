package com.parking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/", "/index.html", "/error", "/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/slots").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/slots/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/slots/*/availability").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/slots/records").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/payments/*/received").hasRole("ADMIN")
                .anyRequest().authenticated())
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) -> writeError(response, 401, "Please log in first"))
                        .accessDeniedHandler((request, response, ex) -> writeError(response, 403, "You are not allowed to do this")))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private static void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
