package com.app.planetaconsciente.service.impl;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.repository.NoticiaRepository;
import com.app.planetaconsciente.service.FileStorageService;
import com.app.planetaconsciente.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.NoSuchElementException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NoticiaServiceImpl implements NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final FileStorageService storageService;

    @Autowired
    public NoticiaServiceImpl(NoticiaRepository noticiaRepository, FileStorageService storageService) {
        this.noticiaRepository = noticiaRepository;
        this.storageService = storageService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Noticia> findByFiltros(String busqueda, String fuente, 
                                     LocalDate fechaDesde, LocalDate fechaHasta,
                                     Pageable pageable) {
        // Validación adicional de fechas
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new IllegalArgumentException("La fecha desde no puede ser mayor que la fecha hasta");
        }
        
        return noticiaRepository.findByFiltros(
            busqueda, 
            fuente, 
            fechaDesde, 
            fechaHasta, 
            pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Noticia> findByFiltrosForExport(String busqueda, String fuente, 
                                              LocalDate fechaDesde, LocalDate fechaHasta) {
        return noticiaRepository.findByFiltrosForExport(
            busqueda, 
            fuente, 
            fechaDesde, 
            fechaHasta
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllFuentes() {
        return noticiaRepository.findAllFuentesDistinct();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Noticia> findById(Long id) {
        return noticiaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Noticia getById(Long id) {
        return noticiaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Noticia no encontrada con ID: " + id));

    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return noticiaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Noticia> findDestacadas() {
        return noticiaRepository.findByDestacadaTrueOrderByFechaPublicacionDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Noticia> findRecientes() {
        return noticiaRepository.findTop5ByOrderByFechaPublicacionDesc();
    }

    @Override
    @Transactional
    public Noticia save(Noticia noticia, MultipartFile imagen) {
        // Validaciones básicas
        if (noticia.getTitulo() == null || noticia.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la noticia es requerido");
        }
        
        // Verificar unicidad del título
        if (existeNoticiaConTitulo(noticia.getTitulo())) {
            throw new IllegalArgumentException("Ya existe una noticia con este título");
        }
        
        // Establecer fecha de publicación si no está definida
        if (noticia.getFechaPublicacion() == null) {
            noticia.setFechaPublicacion(LocalDate.now());
        }

        // Manejo de la imagen
        if (imagen != null && !imagen.isEmpty()) {
            String imagenUrl = storageService.store(imagen);
            noticia.setImagenUrl(imagenUrl);
        }

        return noticiaRepository.save(noticia);
    }

    @Override
    @Transactional
    public Noticia update(Long id, Noticia noticia, MultipartFile imagen) {
        Noticia existente = getById(id);

        // Validaciones básicas
        if (noticia.getTitulo() == null || noticia.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la noticia es requerido");
        }
        
        // Verificar unicidad del título (excepto para sí misma)
        if (!existente.getTitulo().equalsIgnoreCase(noticia.getTitulo()) && 
            existeNoticiaConTitulo(noticia.getTitulo())) {
            throw new IllegalArgumentException("Ya existe una noticia con este título");
        }

        // Actualizar campos
        existente.setTitulo(noticia.getTitulo());
        existente.setResumen(noticia.getResumen());
        existente.setContenido(noticia.getContenido());
        existente.setFechaPublicacion(noticia.getFechaPublicacion());
        existente.setFuente(noticia.getFuente());
        existente.setDestacada(noticia.isDestacada());

        // Manejo de la imagen (solo actualizar si se proporciona una nueva)
        if (imagen != null && !imagen.isEmpty()) {
            // Eliminar la imagen anterior si existe
            if (existente.getImagenUrl() != null) {
                storageService.delete(existente.getImagenUrl());
            }
            
            // Guardar la nueva imagen
            String nuevaImagenUrl = storageService.store(imagen);
            existente.setImagenUrl(nuevaImagenUrl);
        }

        return noticiaRepository.save(existente);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Noticia noticia = getById(id);
        
        // Eliminar la imagen asociada si existe
        if (noticia.getImagenUrl() != null) {
            storageService.delete(noticia.getImagenUrl());
        }
        
        noticiaRepository.delete(noticia);
    }

    @Override
    @Transactional
    public Noticia destacarNoticia(Long id, boolean destacar) {
        Noticia noticia = getById(id);
        noticia.setDestacada(destacar);
        return noticiaRepository.save(noticia);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeNoticiaConTitulo(String titulo) {
        return noticiaRepository.existsByTituloIgnoreCase(titulo);
    }
}