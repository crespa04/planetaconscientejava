package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.Evento;
import com.app.planetaconsciente.service.EventoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;

    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public String listarEventos(Model model) {
        model.addAttribute("eventos", eventoService.listarTodos());
        return "eventos/lista-eventos";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("evento", new Evento());
        return "eventos/form-evento";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarEvento(@ModelAttribute Evento evento) {
        eventoService.guardar(evento);
        return "redirect:/eventos";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(id));
        return "eventos/form-evento";
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminar(id);
        return "redirect:/eventos";
    }
}