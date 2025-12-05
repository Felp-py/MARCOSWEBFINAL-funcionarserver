package com.example.demo.service;

import com.example.demo.model.Autor;
import java.util.List;
import java.util.Optional;

public interface AutorService {
    List<Autor> findAll();
    Optional<Autor> findById(Integer id); 
    Autor save(Autor autor);
    void deleteById(Integer id); 
    Optional<Autor> findByNombre(String nombre); 
}