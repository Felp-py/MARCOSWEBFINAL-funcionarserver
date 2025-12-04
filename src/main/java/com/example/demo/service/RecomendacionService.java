package com.example.demo.service;

import com.example.demo.model.Recomendacion;
import java.util.List;

public interface RecomendacionService {
    List<Recomendacion> findAll();
    Recomendacion save(Recomendacion recomendacion);
}
