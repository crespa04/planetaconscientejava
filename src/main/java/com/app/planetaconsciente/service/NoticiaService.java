package com.app.planetaconsciente.service;

import com.app.planetaconsciente.model.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface NoticiaService {
    // Métodos básicos
    Page<Noticia> findAll(Pageable pageable);
    
    Page<Noticia> findByFiltros(String busqueda, String fuente, 
                              LocalDate fechaDesde, LocalDate fechaHasta,
                              Pageable pageable);
    
    Noticia findById(Long id);
    
    List<String> findAllFuentes();
    
    // Métodos de gestión
    Noticia save(Noticia noticia, MultipartFile imagen);
    
    Noticia update(Long id, Noticia noticia, MultipartFile imagen);
    
    void delete(Long id);
    
    // Método adicional recomendado
    default boolean noticiaExists(Long id) {
        // Implementación por defecto opcional
        try {
            return findById(id) != null;
        } catch (Exception e) {
            return false;
        }
    }
}