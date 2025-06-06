package com.app.planetaconsciente.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.planetaconsciente.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByUsername(String username);
}