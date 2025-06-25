package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.Foro;
import com.app.planetaconsciente.model.User;
import com.app.planetaconsciente.service.ForoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/foro")
public class ForoController {

    @Autowired
    private ForoService foroService;

    @GetMapping
    public String listarForo(Model model) {
        model.addAttribute("publicaciones", foroService.obtenerTodos());
        model.addAttribute("publicacion", new Foro()); // ✅ Esto es obligatorio para Thymeleaf
        return "medioambiente/foro";
    }

    @PostMapping
    public String crearPublicacion(@ModelAttribute Foro foro,
                                   @AuthenticationPrincipal User user) {
        foro.setUsuario(user);
        foro.setCreatedAt(LocalDateTime.now());
        foroService.guardar(foro);
        return "redirect:/foro";
    }

    @GetMapping("/edit/{id}")
    public String editarPublicacion(@PathVariable Long id, Model model) {
        Foro foro = foroService.obtenerPorId(id);
        model.addAttribute("publicacion", foro);
        return "medioambiente/foro_edit"; // Asegúrate de tener esta vista creada
    }

    @PostMapping("/update/{id}")
    public String actualizarPublicacion(@PathVariable Long id,
                                        @ModelAttribute Foro foroEditado) {
        Foro foro = foroService.obtenerPorId(id);
        if (foro != null) {
            foro.setTitulo(foroEditado.getTitulo());
            foro.setContenido(foroEditado.getContenido());
            foroService.guardar(foro);
        }
        return "redirect:/foro";
    }

    @PostMapping("/delete/{id}")
    public String eliminarPublicacion(@PathVariable Long id) {
        foroService.eliminar(id);
        return "redirect:/foro";
    }
}
