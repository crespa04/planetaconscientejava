package com.app.planetaconsciente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                // Rutas públicas
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Retos - Acceso público para lectura
                .requestMatchers(HttpMethod.GET, "/eventos/retos").permitAll()
                
                // Retos - Operaciones de administración
                .requestMatchers("/eventos/retos/**").hasRole("ADMIN")
                
                // Eventos - Acceso público para lectura
                .requestMatchers(HttpMethod.GET, "/eventos", "/eventos/").permitAll()
                
                // Eventos - Operaciones de administración
                .requestMatchers("/eventos/nuevo", "/eventos/editar/**", "/eventos/eliminar/**").hasRole("ADMIN")
                
                // Dashboard accesible solo para autenticados
                .requestMatchers("/dashboard").authenticated()
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}