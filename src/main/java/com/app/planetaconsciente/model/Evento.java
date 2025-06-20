package com.app.planetaconsciente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @NotBlank(message = "La ubicación es obligatoria")
    private String ubicacion;

    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    // Constructores, getters y setters
    public Evento() {}

    // Getters y setters para todos los campos...
}