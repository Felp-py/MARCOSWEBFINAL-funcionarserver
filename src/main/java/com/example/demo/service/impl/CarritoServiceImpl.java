package com.example.demo.service.impl;

import com.example.demo.model.Carrito;
import com.example.demo.repository.CarritoRepository;
import com.example.demo.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Optional<Carrito> findById(Integer id) {
        return carritoRepository.findById(id);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Integer id) {
        carritoRepository.deleteById(id);
    }
}
