package com.app.planetaconsciente.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                // Rutas públicas
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()

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
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
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
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)  // Redirige a calculadora después de login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(
                    "/calculadora/calcular"  // Si necesitas deshabilitar CSRF para esta ruta
                )
            )
    
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // ← esto se necesita
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
