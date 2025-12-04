package com.example.demo.service;

import com.example.demo.model.Carrito;
import java.util.List;
import java.util.Optional;

public interface CarritoService {
    List<Carrito> findAll();
    Optional<Carrito> findById(Integer id);
    Carrito save(Carrito carrito);
    void deleteById(Integer id);
}
