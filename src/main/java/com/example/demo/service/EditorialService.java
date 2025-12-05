package com.example.demo.service;

import com.example.demo.model.Editorial;
import java.util.List;
import java.util.Optional;

public interface EditorialService {
    List<Editorial> findAll();
    Optional<Editorial> findById(Integer id); 
    Editorial save(Editorial editorial);
    void deleteById(Integer id); 
    Optional<Editorial> findByNombre(String nombre); 
}