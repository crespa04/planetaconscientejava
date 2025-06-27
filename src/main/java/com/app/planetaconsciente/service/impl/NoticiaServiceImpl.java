package com.app.planetaconsciente.service.impl;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.repository.NoticiaRepository;
import com.app.planetaconsciente.service.NoticiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class NoticiaServiceImpl implements NoticiaService {

    private final NoticiaRepository noticiaRepository;

    @Autowired
    public NoticiaServiceImpl(NoticiaRepository noticiaRepository) {
        this.noticiaRepository = noticiaRepository;
    }

    @Override
    public Page<Noticia> findAll(Pageable pageable) {
        return noticiaRepository.findAll(pageable);
    }

    @Override
    public Page<Noticia> findByFiltros(
        String busqueda, 
        String fuente, 
        LocalDate fechaDesde, 
        LocalDate fechaHasta,
        Pageable pageable
    ) {
        return noticiaRepository.findByFiltros(
            busqueda, 
            fuente, 
            fechaDesde, 
            fechaHasta, 
            pageable
        );
    }

    @Override
    public List<Noticia> findByFiltrosForExport(
        String busqueda, 
        String fuente, 
        LocalDate fechaDesde, 
        LocalDate fechaHasta
    ) {
        return noticiaRepository.findByFiltrosForExport(
            busqueda, 
            fuente, 
            fechaDesde, 
            fechaHasta
        );
    }

    @Override
    public Optional<Noticia> findById(Long id) {
        return noticiaRepository.findById(id);
    }

    @Override
    public List<String> findAllFuentes() {
        return noticiaRepository.findAllFuentesDistinct();
    }

    @Override
    public Noticia save(Noticia noticia, MultipartFile imagen) {
        // Lógica para guardar la imagen (si es necesario)
        return noticiaRepository.save(noticia);
    }

    @Override
    public Noticia update(Long id, Noticia noticia, MultipartFile imagen) {
        // Lógica para actualizar la imagen (si es necesario)
        noticia.setId(id);
        return noticiaRepository.save(noticia);
    }

    @Override
    public void delete(Long id) {
        noticiaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return noticiaRepository.existsById(id);
    }
}