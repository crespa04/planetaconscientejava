package com.planetaconsciente.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String usernam;
    private String password;
    private String email;
    
    // Getters y Setters (genera estos con Alt+Insert en VS Code)
}
