package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.Noticia;
import com.app.planetaconsciente.service.NoticiaService;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

        if (busqueda != null && busqueda.trim().isEmpty()) busqueda = null;
        if (fuente != null && fuente.trim().isEmpty()) fuente = null;

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
    public String guardarNoticia(
            @ModelAttribute Noticia noticia,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                // 1. Obtener rutas ABSOLUTAS
                String projectRoot = System.getProperty("user.dir");
                Path staticDir = Paths.get(projectRoot, "src", "main", "resources", "static");
                Path uploadPath = Paths.get(staticDir.toString(), "uploads", "noticias");
                
                // 2. Crear directorios si no existen
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 3. Generar nombre único con timestamp para cache busting
                String timestamp = String.valueOf(System.currentTimeMillis());
                String fileName = timestamp + "_" + StringUtils.cleanPath(imagenFile.getOriginalFilename());
                
                // 4. Guardar archivo
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(imagenFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                
                // 5. Guardar ruta en BD con parámetro de cache busting
                noticia.setImagenUrl("/uploads/noticias/" + fileName + "?v=" + timestamp);

                // Log de verificación
                System.out.println("Imagen guardada en: " + targetLocation.toString());
                System.out.println("URL accesible: " + noticia.getImagenUrl());

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Error al subir la imagen");
                return "redirect:/noticias/nueva";
            }
        }
        
        // 6. Guardar noticia y forzar refresco
        noticiaService.guardar(noticia);
        redirectAttributes.addFlashAttribute("forceRefresh", true);
        return "redirect:/noticias";
    }

    @PostMapping("/{id}")
    public String actualizarNoticia(
            @PathVariable Long id,
            @ModelAttribute Noticia noticia,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        Noticia noticiaExistente = noticiaService.obtenerPorId(id);

        // Actualizar campos básicos
        noticiaExistente.setTitulo(noticia.getTitulo());
        noticiaExistente.setResumen(noticia.getResumen());
        noticiaExistente.setContenido(noticia.getContenido());
        noticiaExistente.setFuente(noticia.getFuente());
        noticiaExistente.setFechaPublicacion(noticia.getFechaPublicacion());

        if (imagenFile != null && !imagenFile.isEmpty()) {
            try {
                // 1. Eliminar imagen anterior si existe
                if (noticiaExistente.getImagenUrl() != null && !noticiaExistente.getImagenUrl().isEmpty()) {
                    Path oldImagePath = Paths.get(
                        System.getProperty("user.dir"),
                        "src", "main", "resources", "static",
                        noticiaExistente.getImagenUrl()
                    );
                    Files.deleteIfExists(oldImagePath);
                }

                // 2. Definir rutas ABSOLUTAS para nueva imagen
                String projectRoot = System.getProperty("user.dir");
                Path staticDir = Paths.get(projectRoot, "src", "main", "resources", "static");
                Path uploadPath = Paths.get(staticDir.toString(), "uploads", "noticias");
                
                // 3. Crear directorios si no existen
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 4. Generar nombre único
                String fileName = System.currentTimeMillis() + "_" + 
                    StringUtils.cleanPath(imagenFile.getOriginalFilename());
                
                // 5. Guardar nueva imagen
                Path targetLocation = uploadPath.resolve(fileName);
                Files.copy(imagenFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                
                // 6. Actualizar ruta en la entidad
                noticiaExistente.setImagenUrl("/uploads/noticias/" + fileName);

                // Log para verificación
                System.out.println("Nueva imagen guardada en: " + targetLocation.toString());
                System.out.println("Accesible via URL: " + noticiaExistente.getImagenUrl());

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("error", "Error al actualizar la imagen");
                return "redirect:/noticias/" + id + "/editar";
            }
        }

        noticiaService.guardar(noticiaExistente);
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
