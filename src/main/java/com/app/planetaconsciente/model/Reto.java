package com.app.planetaconsciente.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Reto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String mes;
    private String tema;
    private String dificultad;
    
    @ElementCollection
    @CollectionTable(name = "reto_acciones", joinColumns = @JoinColumn(name = "reto_id"))
    @Column(name = "accion")
    private List<String> acciones;
    
    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMes() { return mes; }
    public void setMes(String mes) { this.mes = mes; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getDificultad() { return dificultad; }
    public void setDificultad(String dificultad) { this.dificultad = dificultad; }
    public List<String> getAcciones() { return acciones; }
    public void setAcciones(List<String> acciones) { this.acciones = acciones; }
}