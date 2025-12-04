package com.example.demo.repository;

import com.example.demo.model.Recomendacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecomendacionRepository extends JpaRepository<Recomendacion, Integer> {
}
