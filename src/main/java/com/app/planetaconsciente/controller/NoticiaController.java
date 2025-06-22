package com.app.planetaconsciente.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.service.NoticiaService; // Corrige el import

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/noticias")
public class NoticiaController {

    private final NoticiaService noticiaService;

    public NoticiaController(NoticiaService noticiaService) { // Corrige el tipo del parámetro
        this.noticiaService = noticiaService;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) String fecha_desde,
            @RequestParam(required = false) String fecha_hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        LocalDate fechaDesde = null;
        LocalDate fechaHasta = null;
        try {
            if (fecha_desde != null && !fecha_desde.isEmpty()) {
                fechaDesde = LocalDate.parse(fecha_desde);
            }
            if (fecha_hasta != null && !fecha_hasta.isEmpty()) {
                fechaHasta = LocalDate.parse(fecha_hasta);
            }
        } catch (Exception e) {
            // Puedes agregar un mensaje de error si lo deseas
        }

        Page<Noticia> noticias = noticiaService.findByFiltros(busqueda, fuente, fechaDesde, fechaHasta, pageable);
        List<String> fuentes = noticiaService.findAllFuentes();

        model.addAttribute("noticias", noticias);
        model.addAttribute("fuentes", fuentes);
        model.addAttribute("filtros", new Filtros(busqueda, fuente, fecha_desde, fecha_hasta));

        return "noticias/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Noticia noticia = noticiaService.findById(id);
        model.addAttribute("noticia", noticia);
        return "noticias/show";
    }

    @GetMapping("/nueva")
    public String nuevaNoticia(Model model) {
        model.addAttribute("noticia", new Noticia());
        return "noticias/form";
    }

    @GetMapping("/{id}/editar")
    public String editarNoticia(@PathVariable Long id, Model model) {
        Noticia noticia = noticiaService.findById(id);
        model.addAttribute("noticia", noticia);
        return "noticias/form";
    }

    @PostMapping
    public String guardarNoticia(
            @ModelAttribute("noticia") @Valid Noticia noticia,
            BindingResult result,
            @RequestParam("imagen") MultipartFile imagen,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "noticias/form";
        }

        Noticia savedNoticia = noticiaService.save(noticia, imagen);
        redirectAttributes.addFlashAttribute("successMessage", "Noticia creada exitosamente!");
        return "redirect:/noticias/" + savedNoticia.getId();
    }

    @PostMapping("/{id}")
    public String actualizarNoticia(
            @PathVariable Long id,
            @ModelAttribute("noticia") @Valid Noticia noticia,
            BindingResult result,
            @RequestParam("imagen") MultipartFile imagen,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "noticias/form";
        }

        Noticia updatedNoticia = noticiaService.update(id, noticia, imagen);
        redirectAttributes.addFlashAttribute("successMessage", "Noticia actualizada exitosamente!");
        return "redirect:/noticias/" + updatedNoticia.getId();
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarNoticia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        noticiaService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Noticia eliminada exitosamente!");
        return "redirect:/noticias";
    }

    // Clase interna para manejar filtros
    private static class Filtros {
        private String busqueda;
        private String fuente;
        private String fechaDesde;
        private String fechaHasta;

        public Filtros(String busqueda, String fuente, String fechaDesde, String fechaHasta) {
            this.busqueda = busqueda;
            this.fuente = fuente;
            this.fechaDesde = fechaDesde;
            this.fechaHasta = fechaHasta;
        }

        // Getters
        public String getBusqueda() { return busqueda; }
        public String getFuente() { return fuente; }
        public String getFechaDesde() { return fechaDesde; }
        public String getFechaHasta() { return fechaHasta; }
    }
}