package com.app.planetaconsciente.service.impl;

import com.app.planetaconsciente.model.Reto;
import com.app.planetaconsciente.repository.RetoRepository;
import com.app.planetaconsciente.service.RetoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetoServiceImpl implements RetoService {

    private final RetoRepository retoRepository;

    public RetoServiceImpl(RetoRepository retoRepository) {
        this.retoRepository = retoRepository;
    }

    @Override
    public List<Reto> listarTodos() {
        return retoRepository.findAll();
    }

    @Override
    public Reto guardar(Reto reto) {
        return retoRepository.save(reto);
    }

    @Override
    public Reto buscarPorId(Long id) {
        return retoRepository.findById(id).orElse(null);
    }

    @Override
    public void eliminar(Long id) {
        retoRepository.deleteById(id);
    }

    public RetoRepository getRetoRepository() {
        return retoRepository;
    }
}