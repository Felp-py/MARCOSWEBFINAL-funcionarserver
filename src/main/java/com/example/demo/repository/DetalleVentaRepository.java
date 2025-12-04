package com.example.demo.repository;

import com.example.demo.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> { // Integer aquí
    // Métodos personalizados si los necesitas:
    // List<DetalleVenta> findByVentaId(Integer ventaId);
    // List<DetalleVenta> findByLibroId(Integer libroId);
}