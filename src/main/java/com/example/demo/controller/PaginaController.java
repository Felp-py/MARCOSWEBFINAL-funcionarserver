package com.example.demo.controller;

import com.example.demo.model.Libro;
import com.example.demo.repository.LibroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PaginaController {

    private final LibroRepository libroRepository;

    public PaginaController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/inicio")
    public String inicio() {
        return "inicio";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }


    @GetMapping("/catalogo")
    public String verCatalogo(Model model) {
        List<Libro> libros = libroRepository.findAll();

        System.out.println("=== DEBUG CATÁLOGO ===");
        System.out.println("Total libros: " + libros.size());

        for(int i = 0; i < Math.min(3, libros.size()); i++) {
            Libro libro = libros.get(i);
            System.out.println("Libro " + i + ": " + libro.getTitulo());
            System.out.println("  Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "NULL"));
            System.out.println("  Imagen URL: " + libro.getImagenUrl());
        }

        model.addAttribute("libros", libros);
        return "catalogo";
    }

    @GetMapping("/compras")
    public String compras(Model model) {
        model.addAttribute("carrito", new ArrayList<Libro>());
        model.addAttribute("total", 0.0);
        return "compras";
    }
     @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}