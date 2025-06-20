package com.app.planetaconsciente.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/retos")
public class RetoController {
    
    @GetMapping
    public String mostrarRetos(Model model) {
        model.addAttribute("mesActual", "Febrero 2024");
        model.addAttribute("retos", List.of(
            new RetoMensual("Febrero", "💧 Ahorro de Agua", List.of(
                "Reduce tiempo de ducha a 5 minutos",
                "Instala sistema de captación de agua lluvia",
                "Riega plantas con agua reutilizada"
            ), "moderado"),
            
            new RetoMensual("Marzo", "🌱 Consumo Responsable", List.of(
                "Compra productos locales",
                "Usa bolsas reutilizables",
                "Evita alimentos con exceso de empaques"
            ), "facil")
        ));
        return "retos/lista-retos";
    }

    // Clase interna para organizar los datos
    private record RetoMensual(String mes, String tema, List<String> acciones, String dificultad) {}
}