package com.example.demo.service;

import com.example.demo.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    List<Cliente> findAll();
    Optional<Cliente> findById(Long id);  // Usa Long aquí también
    Cliente save(Cliente cliente);
    void deleteById(Long id);  // Usa Long aquí también
    Optional<Cliente> findByIdUsuario(Integer idUsuario);
}