package com.app.planetaconsciente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configuración general compatible con tus efectos CSS/JS
            .csrf(AbstractHttpConfigurer::disable)  // Mantenemos deshabilitado para simplificar formularios
            .headers(headers -> headers
                .frameOptions().sameOrigin()  // Para efectos visuales como partículas
                .httpStrictTransportSecurity().disable()  // Desarrollo solamente
            )
            
            // Autorizaciones adaptadas a tu estructura
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos (prioritarios para el diseño)
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/fonts/**",
                    "/webjars/**",
                    "/favicon.ico"
                ).permitAll()
                
                // Páginas públicas (todas las referenciadas en tu navbar/footer)
                .requestMatchers(
                    "/",
                    "/home",
                    "/inicio",
                    "/login",
                    "/register",
                    "/calculadora/**",
                    "/medio_ambiente",
                    "/eventos",
                    "/noticias",
                    "/access-denied",
                    "/error"
                ).permitAll()
                
                // Protección de áreas administrativas
                .requestMatchers(
                    "/admin/**",
                    "/noticias/nueva/**",
                    "/noticias/editar/**",
                    "/noticias/eliminar/**",
                    "/eventos/nuevo/**",
                    "/eventos/editar/**",
                    "/eventos/eliminar/**"
                ).hasRole("ADMIN")
                
                // Área de usuario (dashboard compatible con tus estilos)
                .requestMatchers(
                    "/dashboard",
                    "/mi-perfil/**",
                    "/configuracion"
                ).authenticated()
                
                // Cualquier otra ruta
                .anyRequest().authenticated()
            )
            
            // Login configurado para tu diseño premium
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")  // Redirige al área con estilos personalizados
                .failureUrl("/login?error=true")  // Para mostrar feedback en tu diseño
                .permitAll()
            )
            
            // Logout optimizado para tu navbar
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/?logout=true")  // Para mostrar confirmación
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
            )
            
            // Manejo de errores estilizado
            .exceptionHandling(handling -> handling
                .accessDeniedPage("/access-denied")  // Página con tus estilos CSS
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}