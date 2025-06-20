package com.app.planetaconsciente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        // Obtener información del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("userName", auth.getName());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        // Agregar datos para las secciones (en un caso real, estos vendrían de servicios)
        model.addAttribute("stats", Map.of(
            "peopleInvolved", 12500,
            "eventsHeld", 320,
            "communitiesImpacted", 45,
            "co2Reduced", 1200
        ));
        
        // Datos de noticias (en una aplicación real, esto vendría de un servicio)
        model.addAttribute("news", List.of(
            Map.of(
                "id", 1,
                "title", "Nueva iniciativa de reforestación urbana",
                "summary", "Lanzamos nuestro programa para plantar 10,000 árboles en áreas urbanas durante este año.",
                "date", "15 Mar 2025",
                "image", "/images/amb5.jpg"
            ),
            Map.of(
                "id", 2,
                "title", "Talleres de reciclaje creativo",
                "summary", "Aprende a transformar materiales reciclables en objetos útiles en nuestros talleres mensuales.",
                "date", "28 Feb 2025",
                "image", "/images/amb3.jpg"
            ),
            Map.of(
                "id", 3,
                "title", "Resultados de nuestra campaña de limpieza",
                "summary", "Gracias a la participación de 500 voluntarios, recolectamos 2 toneladas de residuos en playas.",
                "date", "10 Feb 2025",
                "image", "/images/amb2.jpg"
            )
        ));
        
        // Testimonios
        model.addAttribute("testimonials", List.of(
            Map.of(
                "quote", "Gracias a los talleres de Planeta Consciente, mi familia ha reducido su basura semanal en un 60%. ¡Es increíble ver el impacto que podemos tener!",
                "name", "María González",
                "role", "Participante desde 2024",
                "image", "/images/1.jpg"
            ),
            Map.of(
                "quote", "La calculadora ecológica me abrió los ojos sobre mi consumo de agua. Ahora he implementado sistemas de recolección de lluvia en mi casa.",
                "name", "Carlos Mendoza",
                "role", "Voluntario activo",
                "image", "/images/usu3.jpg"
            ),
            Map.of(
                "quote", "Los eventos de limpieza no solo ayudan al medio ambiente, sino que crean una comunidad increíble de personas con valores similares.",
                "name", "Ana Rodríguez",
                "role", "Líder comunitaria",
                "image", "/images/amb1.jpg"
            )
        ));
        
        return "home";
    }
}