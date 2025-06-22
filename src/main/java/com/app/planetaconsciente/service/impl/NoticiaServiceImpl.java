package com.app.planetaconsciente.service.impl;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.repository.NoticiaRepository;
import com.app.planetaconsciente.service.FileStorageService;
import com.app.planetaconsciente.service.NoticiaService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class NoticiaServiceImpl implements NoticiaService {

    private final NoticiaRepository noticiaRepository;
    private final FileStorageService fileStorageService;

    public NoticiaServiceImpl(NoticiaRepository noticiaRepository, FileStorageService fileStorageService) {
        this.noticiaRepository = noticiaRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public Page<Noticia> findAll(Pageable pageable) {
        return noticiaRepository.findAll(pageable);
    }

    @Override
    public Page<Noticia> findByFiltros(String busqueda, String fuente, LocalDate fechaDesde, LocalDate fechaHasta, Pageable pageable) {
        return noticiaRepository.findByFiltros(
                busqueda, 
                fuente, 
                fechaDesde, 
                fechaHasta, 
                pageable);
    }

    @Override
    public Noticia findById(Long id) {
        return noticiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Noticia no encontrada con id: " + id));
    }

    @Override
    public Noticia save(Noticia noticia, MultipartFile imagen) {
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen);
            noticia.setImagenUrl("/uploads/" + fileName);
        }
        return noticiaRepository.save(noticia);
    }

    @Override
    public Noticia update(Long id, Noticia noticia, MultipartFile imagen) {
        Noticia noticiaExistente = findById(id);
        
        noticiaExistente.setTitulo(noticia.getTitulo());
        noticiaExistente.setResumen(noticia.getResumen());
        noticiaExistente.setContenido(noticia.getContenido());
        noticiaExistente.setFechaPublicacion(noticia.getFechaPublicacion());
        noticiaExistente.setFuente(noticia.getFuente());
        
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen);
            noticiaExistente.setImagenUrl("/uploads/" + fileName);
        }
        
        return noticiaRepository.save(noticiaExistente);
    }

    @Override
    public void delete(Long id) {
        Noticia noticia = findById(id);
        noticiaRepository.delete(noticia);
    }

    @Override
    public List<String> findAllFuentes() {
        return noticiaRepository.findDistinctFuentes();
    }
}