package com.example.demo.service;

import com.example.demo.model.Autor;
import java.util.List;
import java.util.Optional;

public interface AutorService {
    List<Autor> findAll();
    Optional<Autor> findById(Integer id); // Mantener como Integer
    Autor save(Autor autor);
    void deleteById(Integer id); // Mantener como Integer
    Optional<Autor> findByNombre(String nombre); // Añadir este método
}