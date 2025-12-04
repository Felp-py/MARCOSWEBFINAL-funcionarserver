package com.example.demo.service;

import com.example.demo.model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    List<Libro> findAll();
    Optional<Libro> findById(Long id); // Cambiado a Long
    Libro save(Libro libro);
    void deleteById(Long id); // Cambiado a Long
}