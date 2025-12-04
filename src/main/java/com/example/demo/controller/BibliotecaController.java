package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BibliotecaController {

    @GetMapping("/graficos")
    public String mostrarGraficos(Model model) {
        model.addAttribute("librosPorGenero", new int[]{10, 5, 8, 3});
        model.addAttribute("generos", new String[]{"Ficción", "No Ficción", "Ciencia", "Historia"});
        model.addAttribute("prestamosPorMes", new int[]{2, 4, 6, 8, 5, 7});
        model.addAttribute("meses", new String[]{"Ene", "Feb", "Mar", "Abr", "May", "Jun"});
        return "recomendaciones";
    }
}
