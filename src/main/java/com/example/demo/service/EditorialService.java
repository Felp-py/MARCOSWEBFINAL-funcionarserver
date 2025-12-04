package com.example.demo.service;

import com.example.demo.model.Editorial;
import java.util.List;
import java.util.Optional;

public interface EditorialService {
    List<Editorial> findAll();
    Optional<Editorial> findById(Integer id); // Mantener como Integer
    Editorial save(Editorial editorial);
    void deleteById(Integer id); // Mantener como Integer
    Optional<Editorial> findByNombre(String nombre); // Añadir este método
}