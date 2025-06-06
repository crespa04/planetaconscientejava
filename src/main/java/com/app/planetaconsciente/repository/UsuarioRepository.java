package com.planetaconsciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.planetaconsciente.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByUsername(String username);
}