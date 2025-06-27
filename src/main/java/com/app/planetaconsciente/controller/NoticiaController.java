package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.service.NoticiaService;
import com.app.planetaconsciente.util.PdfGenerator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/noticias")
public class NoticiaController {

    private final NoticiaService noticiaService;
    private final PdfGenerator pdfGenerator;
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB

    public NoticiaController(NoticiaService noticiaService, PdfGenerator pdfGenerator) {
        this.noticiaService = noticiaService;
        this.pdfGenerator = pdfGenerator;
    }

    @GetMapping
    public String index(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        if (fechasInvalidas(fecha_desde, fecha_hasta)) {
            model.addAttribute("errorFecha", "La fecha desde no puede ser mayor que la fecha hasta");
            model.addAttribute("filtros", new Filtros(busqueda, fuente, fecha_desde, fecha_hasta));
            return "noticias/index";
        }

        Page<Noticia> noticias = noticiaService.findByFiltros(busqueda, fuente, fecha_desde, fecha_hasta, pageable);
        model.addAttribute("noticias", noticias);
        model.addAttribute("fuentes", noticiaService.findAllFuentes());
        model.addAttribute("filtros", new Filtros(busqueda, fuente, fecha_desde, fecha_hasta));

        return "noticias/index";
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generarPdf(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_hasta) {

        List<Noticia> noticias = noticiaService.findByFiltrosForExport(
            busqueda,
            fuente,
            fecha_desde,
            fecha_hasta
        );

        byte[] pdfBytes = pdfGenerator.generateNoticiasPdf(noticias, busqueda, fuente, 
            fecha_desde != null ? fecha_desde.toString() : null, 
            fecha_hasta != null ? fecha_hasta.toString() : null);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=noticias.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Optional<Noticia> noticia = noticiaService.findById(id);
        if (noticia.isEmpty()) {
            return "redirect:/noticias";
        }
        model.addAttribute("noticia", noticia.get());
        return "noticias/show";
    }

    // --- Métodos de administración ---
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/nueva")
    public String nuevaNoticia(Model model) {
        model.addAttribute("noticia", new Noticia());
        return "noticias/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/editar")
    public String editarNoticia(@PathVariable Long id, Model model) {
        Optional<Noticia> noticia = noticiaService.findById(id);
        if (noticia.isEmpty()) {
            return "redirect:/noticias";
        }
        model.addAttribute("noticia", noticia.get());
        return "noticias/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String guardarNoticia(
            @ModelAttribute("noticia") @Valid Noticia noticia,
            BindingResult result,
            @RequestParam("imagen") MultipartFile imagen,
            RedirectAttributes redirectAttributes) {

        validarImagen(imagen, result);
        if (result.hasErrors()) {
            return manejarErrores(noticia, result, redirectAttributes);
        }

        Noticia savedNoticia = noticiaService.save(noticia, imagen);
        redirectAttributes.addFlashAttribute("successMessage", "Noticia creada exitosamente!");
        return "redirect:/noticias/" + savedNoticia.getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}")
    public String actualizarNoticia(
            @PathVariable Long id,
            @ModelAttribute("noticia") @Valid Noticia noticia,
            BindingResult result,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            RedirectAttributes redirectAttributes) {

        if (!noticiaService.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "La noticia no existe");
            return "redirect:/noticias";
        }

        if (imagen != null && !imagen.isEmpty()) {
            validarImagen(imagen, result);
        }

        if (result.hasErrors()) {
            return manejarErrores(noticia, result, redirectAttributes);
        }

        noticia.setId(id);
        Noticia updatedNoticia = noticiaService.update(id, noticia, imagen);
        redirectAttributes.addFlashAttribute("successMessage", "Noticia actualizada exitosamente!");
        return "redirect:/noticias/" + updatedNoticia.getId();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/eliminar")
    public String eliminarNoticia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!noticiaService.existsById(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "La noticia no existe");
        } else {
            noticiaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Noticia eliminada exitosamente!");
        }
        return "redirect:/noticias";
    }

    // --- Métodos auxiliares mejorados ---
    private boolean fechasInvalidas(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde == null || fechaHasta == null) {
            return false;
        }
        return fechaDesde.isAfter(fechaHasta);
    }

    private void validarImagen(MultipartFile imagen, BindingResult result) {
        if (imagen == null || imagen.isEmpty()) {
            return;
        }
        
        if (imagen.getSize() > MAX_FILE_SIZE) {
            result.rejectValue("imagen", "file.size.exceeded", "El tamaño máximo permitido es 2MB");
        }
        
    }

    private String manejarErrores(Noticia noticia, BindingResult result, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.noticia", result);
        redirectAttributes.addFlashAttribute("noticia", noticia);

        // Si la noticia tiene ID, es edición. Si no, es creación.
        if (noticia.getId() != null) {
            return "noticias/form";
        } else {
            return "noticias/form";
        }
    }

    // Clase Filtros mejorada
    public static class Filtros {
        private final String busqueda;
        private final String fuente;
        private final LocalDate fechaDesde;
        private final LocalDate fechaHasta;

        public Filtros(String busqueda, String fuente, LocalDate fechaDesde, LocalDate fechaHasta) {
            this.busqueda = busqueda;
            this.fuente = fuente;
            this.fechaDesde = fechaDesde;
            this.fechaHasta = fechaHasta;
        }

        // Getters
        public String getBusqueda() { return busqueda; }
        public String getFuente() { return fuente; }
        public LocalDate getFechaDesde() { return fechaDesde; }
        public LocalDate getFechaHasta() { return fechaHasta; }
        
        // Métodos útiles para la vista
        public String getFechaDesdeFormatted() {
            return fechaDesde != null ? fechaDesde.toString() : "";
        }
        
        public String getFechaHastaFormatted() {
            return fechaHasta != null ? fechaHasta.toString() : "";
        }
    }
}