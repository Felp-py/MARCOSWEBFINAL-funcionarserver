package com.example.demo.repository;

import com.example.demo.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    // Método para buscar por título exacto
    Optional<Libro> findByTitulo(String titulo);
    
    // Método para buscar por título (case insensitive)
    Optional<Libro> findByTituloContainingIgnoreCase(String titulo);
    
    // Método para actualizar stock de forma segura
    @Modifying
    @Query("UPDATE Libro l SET l.stock = l.stock + :cantidad WHERE l.id = :id AND l.stock + :cantidad >= 0")
    int actualizarStock(@Param("id") Long id, @Param("cantidad") int cantidad);
}