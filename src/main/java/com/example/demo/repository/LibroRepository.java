package com.example.demo.repository;

import com.example.demo.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    // Solo métodos básicos - elimina todos los métodos personalizados
    Optional<Libro> findByTitulo(String titulo);
}