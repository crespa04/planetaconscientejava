package com.app.planetaconsciente.service;

import com.app.planetaconsciente.model.Noticia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface NoticiaService {
    Page<Noticia> findAll(Pageable pageable);
    Page<Noticia> findByFiltros(String busqueda, String fuente, LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable);
    Noticia findById(Long id);
    Noticia save(Noticia noticia, MultipartFile imagen);
    Noticia update(Long id, Noticia noticia, MultipartFile imagen);
    void delete(Long id);
    List<String> findAllFuentes();
}