package com.example.demo.service;

import com.example.demo.model.Testimonio;
import java.util.List;
import java.util.Optional;

public interface TestimonioService {
    List<Testimonio> findAll();
    Optional<Testimonio> findById(Integer id);
    Testimonio save(Testimonio testimonio);
    void deleteById(Integer id);

    // Métodos extra
    List<Testimonio> findByClienteContaining(String cliente);
    double promedioCalificacion();
}
