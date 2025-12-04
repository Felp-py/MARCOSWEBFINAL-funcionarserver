package com.example.demo.controller;

import com.example.demo.model.OpenLibraryBook;
import com.example.demo.service.OpenLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/openlibrary")
public class OpenLibraryController {

    @Autowired
    private OpenLibraryService service;

    @GetMapping("/{isbn}")
    public OpenLibraryBook buscar(@PathVariable String isbn) {
        return service.buscarPorIsbn(isbn);
    }

    @GetMapping("/portada/{isbn}")
    public String portada(@PathVariable String isbn) {
        return service.obtenerPortada(isbn);
    }
}
