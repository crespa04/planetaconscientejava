package com.app.planetaconsciente.service;

import com.app.planetaconsciente.model.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NoticiaService {
    // Métodos de consulta
    Page<Noticia> findAll(Pageable pageable);
    
    Page<Noticia> findByFiltros(
        String busqueda, 
        String fuente, 
        LocalDate fechaDesde, 
        LocalDate fechaHasta,
        Pageable pageable
    );
    
    // Nuevo método para exportar a PDF (sin paginación)
    List<Noticia> findByFiltrosForExport(
        String busqueda, 
        String fuente, 
        LocalDate fechaDesde, 
        LocalDate fechaHasta
    );
    
    Optional<Noticia> findById(Long id); // Usar Optional para mejor manejo de nulos
    
    List<String> findAllFuentes();
    
    // Métodos de gestión
    Noticia save(Noticia noticia, MultipartFile imagen);
    
    Noticia update(Long id, Noticia noticia, MultipartFile imagen);
    
    void delete(Long id);
    
    // Método de verificación de existencia
    boolean existsById(Long id);
}