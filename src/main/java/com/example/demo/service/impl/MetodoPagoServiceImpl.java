package com.example.demo.service.impl;

import com.example.demo.model.MetodoPago;
import com.example.demo.repository.MetodoPagoRepository;
import com.example.demo.service.MetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoServiceImpl implements MetodoPagoService {

    @Autowired
    private MetodoPagoRepository metodoPagoRepository;

    @Override
    public List<MetodoPago> findAll() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public Optional<MetodoPago> findById(Integer id) {
        return metodoPagoRepository.findById(id);
    }

    @Override
    public MetodoPago save(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public void deleteById(Integer id) {
        metodoPagoRepository.deleteById(id);
    }
}
