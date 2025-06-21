package com.app.planetaconsciente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Inicio - Planeta Consciente");
        return "home";
    }
    
    @GetMapping("/calculadora")
    public String calculadora(Model model) {
        model.addAttribute("pageTitle", "Calculadora Ecológica");
        return "calculadora";
    }
    
    @GetMapping("/medio_ambiente")
    public String medioAmbiente(Model model) {
        model.addAttribute("pageTitle", "Medio Ambiente");
        return "medio-ambiente";
    }
    
    @GetMapping("/eventos")
    public String eventos(Model model) {
        model.addAttribute("pageTitle", "Eventos Ambientales");
        return "eventos";
    }
    
    @GetMapping("/noticias")
    public String noticias(Model model) {
        model.addAttribute("pageTitle", "Noticias Ambientales");
        return "noticias";
    }
}