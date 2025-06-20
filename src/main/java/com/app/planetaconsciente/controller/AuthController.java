package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.User;
import com.app.planetaconsciente.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // Muestra formulario de login
    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "success", required = false) String success,
            Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "Credenciales inválidas. Por favor intenta nuevamente.");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "Has cerrado sesión correctamente.");
        }
        
        if (success != null) {
            model.addAttribute("successMessage", "¡Registro exitoso! Por favor inicia sesión.");
        }
        
        return "login"; // login.html
    }

    // Muestra formulario de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html
    }

    // Procesa el registro de nuevos usuarios
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        try {
            userService.registerNewUser(user);
            return "redirect:/login?success";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error en el registro: " + e.getMessage());
            return "register";
        }
    }

    // Maneja el mensaje de bienvenida en la página principal
    @GetMapping("/")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            model.addAttribute("userName", auth.getName());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "home";
    }
}