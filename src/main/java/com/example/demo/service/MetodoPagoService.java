package com.example.demo.service;

import com.example.demo.model.MetodoPago;
import java.util.List;
import java.util.Optional;

public interface MetodoPagoService {
    List<MetodoPago> findAll();
    Optional<MetodoPago> findById(Integer id);
    MetodoPago save(MetodoPago metodoPago);
    void deleteById(Integer id);
}
