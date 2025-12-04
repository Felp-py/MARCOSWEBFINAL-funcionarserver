package com.example.demo.controller;

import com.example.demo.service.RecomendacionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/recomendaciones")
public class RecomendacionController {

    private final RecomendacionService service;

    public RecomendacionController(RecomendacionService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("recomendaciones", service.findAll());
        return "recomendaciones/lista"; // thymeleaf
    }
}
