package com.example.demo.repository;

import com.example.demo.model.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EditorialRepository extends JpaRepository<Editorial, Integer> {
    Optional<Editorial> findByNombre(String nombre);
}