package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.service.NoticiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/noticias")
@RequiredArgsConstructor
public class NoticiaController {

    private final NoticiaService noticiaService;

    @GetMapping
    public String listarNoticias(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false, name = "fecha_desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false, name = "fecha_hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false, name = "generar_pdf") String generarPdf,
            Model model) {

        // Limpieza de parámetros vacíos
        if (busqueda != null && busqueda.trim().isEmpty())
            busqueda = null;
        if (fuente != null && fuente.trim().isEmpty())
            fuente = null;

        // Si el parámetro generar_pdf es "1", redirigir a exportar PDF con mismos
        // filtros
        if ("1".equals(generarPdf)) {
            String url = UriComponentsBuilder.fromPath("/exportar/noticias/pdf")
                    .queryParamIfPresent("busqueda", Optional.ofNullable(busqueda))
                    .queryParamIfPresent("fuente", Optional.ofNullable(fuente))
                    .queryParamIfPresent("fecha_desde", Optional.ofNullable(fechaDesde))
                    .queryParamIfPresent("fecha_hasta", Optional.ofNullable(fechaHasta))
                    .build()
                    .toUriString();

            return "redirect:" + url;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaPublicacion").descending());

        Page<Noticia> noticias;

        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            model.addAttribute("errorFiltro", "La fecha 'Desde' no puede ser posterior a la fecha 'Hasta'.");
            model.addAttribute("paramFechaDesde", null);
            model.addAttribute("paramFechaHasta", null);
            noticias = noticiaService.filtrarNoticias(busqueda, fuente, null, null, pageable);
        } else {
            noticias = noticiaService.filtrarNoticias(busqueda, fuente, fechaDesde, fechaHasta, pageable);
            model.addAttribute("paramFechaDesde", fechaDesde);
            model.addAttribute("paramFechaHasta", fechaHasta);
        }

        model.addAttribute("noticias", noticias);
        model.addAttribute("fuentes", noticiaService.obtenerTodasLasFuentes());
        model.addAttribute("paramBusqueda", busqueda);
        model.addAttribute("paramFuente", fuente);

        return "noticias/index";
    }

    // Los otros métodos no cambian...

    @GetMapping("/{id}")
    public String verNoticia(@PathVariable Long id, Model model) {
        model.addAttribute("noticia", noticiaService.obtenerPorId(id));
        return "noticias/show";
    }

    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("noticia", new Noticia());
        return "noticias/form";
    }

    @PostMapping
    public String guardarNoticia(@ModelAttribute Noticia noticia) {
        noticiaService.guardar(noticia);
        return "redirect:/noticias";
    }

    @PostMapping("/{id}")
    public String actualizarNoticia(@PathVariable Long id, @ModelAttribute Noticia noticia) {
        noticia.setId(id);
        noticiaService.guardar(noticia);
        return "redirect:/noticias";
    }

    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        model.addAttribute("noticia", noticiaService.obtenerPorId(id));
        return "noticias/form";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarNoticia(@PathVariable Long id) {
        noticiaService.eliminar(id);
        return "redirect:/noticias";
    }
}
