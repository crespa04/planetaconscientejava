package com.app.planetaconsciente.service;

import com.app.planetaconsciente.model.User;
import com.app.planetaconsciente.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(User user) {
        // Validación de email existente
        userRepository.findByEmail(user.getEmail())
            .ifPresent(u -> {
                throw new RuntimeException("El email ya está registrado");
            });

        // Validación de contraseña
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("La contraseña no puede estar vacía");
        }
        
        // Encriptación y guardado
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }
}