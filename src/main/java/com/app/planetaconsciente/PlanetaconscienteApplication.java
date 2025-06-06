package com.app.planetaconsciente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.app.planetaconsciente.model.Usuario;
import com.app.planetaconsciente.repository.UsuarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;



@SpringBootApplication
public class PlanetaconscienteApplication implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(PlanetaconscienteApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario admin si no existe
        if(usuarioRepository.findByUsername("admin") == null) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@planetaconsciente.com");
            usuarioRepository.save(admin);
        }
    }
}