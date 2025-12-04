package com.example.demo.service.impl;

import com.example.demo.model.Editorial;
import com.example.demo.repository.EditorialRepository;
import com.example.demo.service.EditorialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EditorialServiceImpl implements EditorialService {

    @Autowired
    private EditorialRepository editorialRepository;

    @Override
    public List<Editorial> findAll() {
        return editorialRepository.findAll();
    }

    @Override
    public Optional<Editorial> findById(Integer id) { // Mantener como Integer
        return editorialRepository.findById(id);
    }

    @Override
    public Editorial save(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    @Override
    public void deleteById(Integer id) { // Mantener como Integer
        editorialRepository.deleteById(id);
    }
    
    // Método adicional para buscar por nombre
    @Override
    public Optional<Editorial> findByNombre(String nombre) {
        return editorialRepository.findByNombre(nombre);
    }
}