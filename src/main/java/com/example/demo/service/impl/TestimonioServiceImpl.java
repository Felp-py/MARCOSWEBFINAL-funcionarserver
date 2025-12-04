package com.example.demo.service.impl;

import com.example.demo.model.Testimonio;
import com.example.demo.repository.TestimonioRepository;
import com.example.demo.service.TestimonioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestimonioServiceImpl implements TestimonioService {

    @Autowired
    private TestimonioRepository testimonioRepository;

    @Override
    public List<Testimonio> findAll() {
        return testimonioRepository.findAll();
    }

    @Override
    public Optional<Testimonio> findById(Integer id) {
        return testimonioRepository.findById(id);
    }

    @Override
    public Testimonio save(Testimonio testimonio) {
        return testimonioRepository.save(testimonio);
    }

    @Override
    public void deleteById(Integer id) {
        testimonioRepository.deleteById(id);
    }

    @Override
    public List<Testimonio> findByClienteContaining(String cliente) {
        return testimonioRepository.findByClienteContainingIgnoreCase(cliente);
    }

    @Override
    public double promedioCalificacion() {
        return testimonioRepository.findAll().stream()
                .mapToInt(Testimonio::getCalificacion)
                .average().orElse(0);
    }
}
