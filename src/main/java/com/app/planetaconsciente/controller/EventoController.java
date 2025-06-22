package com.app.planetaconsciente.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.planetaconsciente.model.Evento;
import com.app.planetaconsciente.model.Reto;
import com.app.planetaconsciente.service.EventoService;
import com.app.planetaconsciente.service.RetoService;

@Controller
@RequestMapping("/eventos")
public class EventoController {

    private final EventoService eventoService;
    private final RetoService retoService;

    public EventoController(EventoService eventoService, RetoService retoService) {
        this.eventoService = eventoService;
        this.retoService = retoService;
    }

    // Vista combinada: eventos + retos
    @GetMapping
    public String listarEventosYRetos(Model model) {
        String mesActual = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        model.addAttribute("eventos", eventoService.listarTodos());
        model.addAttribute("retos", retoService.listarTodos());
        model.addAttribute("mesActual", mesActual);
        return "eventos/lista-combinada";
    }

    // --------------------- EVENTOS ---------------------
    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioEventoNuevo(Model model) {
        model.addAttribute("evento", new Evento());
        return "eventos/form-evento";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarEvento(@ModelAttribute Evento evento) {
        eventoService.guardar(evento);
        return "redirect:/eventos";
    }

    @GetMapping("/{id}")
    public String verEvento(@PathVariable Long id, Model model) {
        Evento evento = eventoService.buscarPorId(id);
        if (evento == null) {
            return "redirect:/eventos";
        }
        model.addAttribute("evento", evento);
        return "eventos/detalle-evento";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioEventoEditar(@PathVariable Long id, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(id));
        return "eventos/form-evento";
    }

    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarEvento(@PathVariable Long id) {
        eventoService.eliminar(id);
        return "redirect:/eventos";
    }

    // --------------------- RETOS ---------------------
    @GetMapping("/retos/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioRetoNuevo(Model model) {
        model.addAttribute("reto", new Reto());
        return "eventos/form-retos";
    }

    @PostMapping("/retos/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardarReto(@ModelAttribute Reto reto) {
        retoService.guardar(reto);
        return "redirect:/eventos";
    }

    @GetMapping("/retos/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String mostrarFormularioRetoEditar(@PathVariable Long id, Model model) {
        model.addAttribute("reto", retoService.buscarPorId(id));
        return "eventos/form-retos";
    }

    @PostMapping("/retos/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarReto(@PathVariable Long id) {
        retoService.eliminar(id);
        return "redirect:/eventos";
    }

    @GetMapping({"/retos/mensuales", "/retos-mensuales"})
    public String verRetosMensuales(Model model) {
        Map<String, java.util.List<Reto>> retosPorMes = retoService.obtenerRetosAgrupadosPorMes();
        model.addAttribute("retosPorMes", retosPorMes);
        return "eventos/retos-mensuales";
    }

    @GetMapping("/retos/{id}")
    public String verReto(@PathVariable Long id, Model model) {
        Reto reto = retoService.buscarPorId(id);
        if (reto == null) {
            return "redirect:/eventos";
        }
        model.addAttribute("reto", reto);
        return "eventos/detalle-reto";
    }
}
