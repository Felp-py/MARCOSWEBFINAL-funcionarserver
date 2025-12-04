package com.example.demo.controller;

import com.example.demo.model.Libro;
import com.example.demo.repository.LibroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final LibroRepository libroRepository;

    public AdminController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/lista-libros")
    public String mostrarListaLibrosAdmin(Model model) {
        List<Libro> libros = libroRepository.findAll(); // Solo usa findAll()
        
        System.out.println("=== DEBUG: Mostrando lista de libros ===");
        System.out.println("Total libros encontrados: " + libros.size());
        
        model.addAttribute("libros", libros);
        return "lista-libros";
    }
}