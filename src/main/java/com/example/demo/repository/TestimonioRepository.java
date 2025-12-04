package com.example.demo.repository;

import com.example.demo.model.Testimonio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestimonioRepository extends JpaRepository<Testimonio, Integer> {

    // Método para buscar testimonios por nombre de cliente, ignorando mayúsculas/minúsculas
    List<Testimonio> findByClienteContainingIgnoreCase(String cliente);
}
