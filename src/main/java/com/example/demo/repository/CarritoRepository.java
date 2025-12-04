package com.example.demo.repository;

import com.example.demo.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Integer> {
    // Aquí puedes agregar consultas personalizadas si las necesitas, por ejemplo:
    // List<Carrito> findByClienteId(Integer clienteId);
}
