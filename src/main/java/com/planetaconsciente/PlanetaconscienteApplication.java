package com.planetaconsciente;

import org.springframework.boot.CommandLineRunner;
// ... otros imports ...

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