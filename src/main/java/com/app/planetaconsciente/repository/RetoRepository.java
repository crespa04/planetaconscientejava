package com.app.planetaconsciente.repository;

import com.app.planetaconsciente.model.Reto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetoRepository extends JpaRepository<Reto, Long> {
    // Consultas personalizadas si son necesarias
}