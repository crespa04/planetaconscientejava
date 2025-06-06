package com.app.planetaconsciente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    // Getters y setters
    // Constructor
}