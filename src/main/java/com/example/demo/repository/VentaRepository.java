package com.example.demo.repository;

import com.example.demo.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> { // Integer aquí
    // Si necesitas métodos personalizados:
    // List<Venta> findByClienteId(Integer clienteId);
    // List<Venta> findByFechaBetween(Date inicio, Date fin);
}