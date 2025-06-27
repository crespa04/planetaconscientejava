package com.app.planetaconsciente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers(
                    "/",
                    "/login",
                    "/register",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**",
                    "/calculadora",
                    "/calculadora/**",
                    "/calculadora/resultado"
                ).permitAll()

                // Noticias - Acceso público para lectura
                .requestMatchers(HttpMethod.GET, "/noticias", "/noticias/**").permitAll()
                // Noticias - Operaciones de administración
                .requestMatchers("/noticias/nueva", "/noticias/editar/**", "/noticias/eliminar/**").hasRole("ADMIN")    
                
                // Retos - Acceso público para lectura
                .requestMatchers(HttpMethod.GET, "/eventos/retos/**").permitAll()
                // Retos - Operaciones de administración
                .requestMatchers("/eventos/retos/**").hasRole("ADMIN")
                
                // Eventos - Acceso público para lectura
                .requestMatchers(HttpMethod.GET, "/eventos", "/eventos/").permitAll()
                // Eventos - Operaciones de administración
                .requestMatchers("/eventos/nuevo", "/eventos/editar/**", "/eventos/eliminar/**").hasRole("ADMIN")
                
                // Dashboard accesible solo para autenticados
                .requestMatchers("/dashboard").authenticated()
                
                // Accesos por roles
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/calculadora/calcular")
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}