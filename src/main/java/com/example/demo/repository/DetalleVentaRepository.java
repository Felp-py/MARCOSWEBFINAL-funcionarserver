// DetalleVentaRepository.java
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
    
    @Query("SELECT l.titulo, l.categoria.nombre, SUM(d.cantidad) as totalVendido, " +
           "SUM(d.subtotal) as ingresos " +  // Usa subtotal en lugar de calcular
           "FROM DetalleVenta d " +
           "JOIN d.libro l " +
           "GROUP BY l.idLibro, l.titulo, l.categoria.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findLibrosMasVendidos();
    
    // Modificado para tu estructura de BD
    @Query("SELECT c.nombre, SUM(d.cantidad) as totalVendido, " +
           "(SUM(d.cantidad) * 100.0 / (SELECT SUM(d2.cantidad) FROM DetalleVenta d2)) as porcentaje " +
           "FROM DetalleVenta d " +
           "JOIN d.libro l " +
           "JOIN l.categoria c " +
           "GROUP BY c.idCategoria, c.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findDistribucionPorCategoria();
}