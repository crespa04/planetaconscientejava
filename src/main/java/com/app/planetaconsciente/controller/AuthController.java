package com.app.planetaconsciente.controller;

import com.app.planetaconsciente.model.User;
import com.app.planetaconsciente.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        
        if (error != null) {
            model.addAttribute("error", "Email o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("success", "Has cerrado sesión correctamente");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute User user,
            BindingResult result,
            Model model,
            @RequestParam(required = false) String passwordConfirmation) {
        
        // Validación básica
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Validar confirmación de contraseña
        if (!user.getPassword().equals(passwordConfirmation)) {
            model.addAttribute("error", "Las contraseñas no coinciden");
            return "auth/register";
        }
        
        try {
            userService.registerNewUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}