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
    Page<Noticia> findByFiltros(String busqueda, String fuente, LocalDate fechaDesde, 
                              LocalDate fechaHasta, Pageable pageable);
    
    List<Noticia> findByFiltrosForExport(String busqueda, String fuente, 
                                       LocalDate fechaDesde, LocalDate fechaHasta);
    
    List<String> findAllFuentes();
    
    Optional<Noticia> findById(Long id);
    
    Noticia getById(Long id);
    
    boolean existsById(Long id);
    
    List<Noticia> findDestacadas();
    
    List<Noticia> findRecientes();
    
    // Métodos de gestión
    Noticia save(Noticia noticia, MultipartFile imagen);
    
    Noticia update(Long id, Noticia noticia, MultipartFile imagen);
    
    void delete(Long id);
    
    // Métodos adicionales
    Noticia destacarNoticia(Long id, boolean destacar);
    
    boolean existeNoticiaConTitulo(String titulo);
}