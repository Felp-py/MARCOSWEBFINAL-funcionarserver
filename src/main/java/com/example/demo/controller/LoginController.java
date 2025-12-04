package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    
    @GetMapping("/login")  // Solo /login, sin prefijo /auth
    public String login() {
        return "login";
    }
    
}