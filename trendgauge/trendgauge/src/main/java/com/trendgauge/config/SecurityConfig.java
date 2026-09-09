package com.trendgauge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authReq -> authReq
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/error").permitAll()
                        .requestMatchers("/admin/login", "/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/manager/**").hasRole("STORE_MANAGER")
                        .requestMatchers("/main", "/sales/new", "/sales/import").hasRole("STORE_TERMINAL")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}
