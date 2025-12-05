package com.example.demo.service.impl;

import com.example.demo.model.Autor;
import com.example.demo.repository.AutorRepository;
import com.example.demo.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AutorServiceImpl implements AutorService {

    @Autowired
    private AutorRepository autorRepository;

    @Override
    public List<Autor> findAll() {
        return autorRepository.findAll();
    }

    @Override
    public Optional<Autor> findById(Integer id) { 
        return autorRepository.findById(id);
    }

    @Override
    public Autor save(Autor autor) {
        return autorRepository.save(autor);
    }

    @Override
    public void deleteById(Integer id) { 
        autorRepository.deleteById(id);
    }
    
    @Override
    public Optional<Autor> findByNombre(String nombre) {
        return autorRepository.findByNombre(nombre);
    }
}