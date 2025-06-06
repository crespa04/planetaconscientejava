package com.app.planetaconsciente.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String showDashboard() {
        return "dashboard";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }
}