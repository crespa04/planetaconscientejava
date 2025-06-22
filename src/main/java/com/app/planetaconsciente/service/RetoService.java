package com.app.planetaconsciente.service;

import java.util.List;

import com.app.planetaconsciente.model.Reto;


public interface RetoService {
    List<Reto> listarTodos();
    Reto guardar(Reto reto);
    Reto buscarPorId(Long id);
    void eliminar(Long id);
    Object obtenerRetosAgrupadosPorMes();
}