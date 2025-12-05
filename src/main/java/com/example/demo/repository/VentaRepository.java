package com.example.demo.repository;

import com.example.demo.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    // Contar total de ventas
    long count();
    
    // Sumar total de ventas
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v")
    BigDecimal sumTotalVentas();
    
    // Obtener promedio de ventas
    @Query("SELECT COALESCE(AVG(v.total), 0) FROM Venta v")
    BigDecimal promedioVentas();
    
    // Obtener ventas por mes
    @Query("SELECT FUNCTION('MONTH', v.fechaVenta) as mes, COUNT(v.idVenta) as totalVentas " +
           "FROM Venta v " +
           "GROUP BY FUNCTION('MONTH', v.fechaVenta) " +
           "ORDER BY mes")
    List<Object[]> findVentasPorMes();
}