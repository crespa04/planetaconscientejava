package com.app.planetaconsciente.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "noticias")
public class Noticia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 200, message = "El título no puede exceder los 200 caracteres")
    @Column(nullable = false, length = 200)
    private String titulo;

    @NotBlank(message = "El resumen no puede estar vacío")
    @Size(max = 500, message = "El resumen no puede exceder los 500 caracteres")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String resumen;

    @NotBlank(message = "El contenido no puede estar vacío")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @NotNull(message = "La fecha de publicación es requerida")
    @Column(nullable = false)
    private LocalDate fechaPublicacion;

    @Size(max = 100, message = "La fuente no puede exceder los 100 caracteres")
    private String fuente;

    private String imagenUrl;
    private boolean destacada = false;

    // Constructores
    public Noticia() {
        this.fechaPublicacion = LocalDate.now(); // Fecha actual por defecto
    }

    public Noticia(String titulo, String resumen, String contenido, LocalDate fechaPublicacion, String fuente) {
        this.titulo = titulo;
        this.resumen = resumen;
        this.contenido = contenido;
        this.fechaPublicacion = fechaPublicacion != null ? fechaPublicacion : LocalDate.now();
        this.fuente = fuente;
    }
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDate fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public boolean isDestacada() {
        return destacada;
    }

    public void setDestacada(boolean destacada) {
        this.destacada = destacada;
    }
    
    @Override
    public String toString() {
        return "Noticia{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                ", fuente='" + fuente + '\'' +
                ", destacada=" + destacada +
                '}';
    }
}