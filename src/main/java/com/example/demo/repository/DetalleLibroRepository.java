package com.example.demo.repository;

import com.example.demo.model.DetalleLibro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleLibroRepository extends JpaRepository<DetalleLibro, Integer> {
}
