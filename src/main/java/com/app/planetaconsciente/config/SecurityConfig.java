package com.app.planetaconsciente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Permite acceso público a estos endpoints
                .requestMatchers(
                    "/", 
                    "/home",
                    "/login", 
                    "/register", 
                    "/css/**", 
                    "/js/**", 
                    "/images/**",
                    "/eventos",
                    "/calculadora",
                    "/medio_ambiente",
                    "/noticia/**"
                ).permitAll()
                // Todas las demás requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                // Cambiado para redirigir a la página de inicio después del login
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                // Redirige a la página principal con parámetro de logout
                .logoutSuccessUrl("/?logout")
                .permitAll()
            )
            // Opcional: para recordar sesión
            .rememberMe(remember -> remember
                .key("uniqueAndSecret")
                .tokenValiditySeconds(86400) // 1 día
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}