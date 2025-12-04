package com.example.demo.service.impl;

import com.example.demo.model.Recomendacion;
import com.example.demo.repository.RecomendacionRepository;
import com.example.demo.service.RecomendacionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecomendacionServiceImpl implements RecomendacionService {

    private final RecomendacionRepository repo;

    public RecomendacionServiceImpl(RecomendacionRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Recomendacion> findAll() {
        return repo.findAll();
    }

    @Override
    public Recomendacion save(Recomendacion recomendacion) {
        return repo.save(recomendacion);
    }
}
