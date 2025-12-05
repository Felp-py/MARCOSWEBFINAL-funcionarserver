package com.example.demo.repository;

import com.example.demo.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM DetalleVenta d")
    Long sumTotalLibrosVendidos();
    
    @Query("SELECT l.titulo, SUM(d.cantidad) as totalVendido " +
           "FROM DetalleVenta d " +
           "JOIN d.libro l " +
           "GROUP BY l.idLibro, l.titulo " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findLibrosMasVendidos();
    
    @Query("SELECT l.categoria.nombre, SUM(d.cantidad) as totalVendido " +
           "FROM DetalleVenta d " +
           "JOIN d.libro l " +
           "JOIN l.categoria c " +
           "GROUP BY c.idCategoria, c.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findDistribucionPorCategoria();
}