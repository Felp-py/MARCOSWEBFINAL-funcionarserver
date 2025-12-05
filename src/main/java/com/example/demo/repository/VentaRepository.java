package com.example.demo.repository;

import com.example.demo.model.Venta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v")
    BigDecimal sumTotalVentas();
    
    @Query("SELECT COALESCE(AVG(v.total), 0) FROM Venta v")
    BigDecimal promedioVentas();
    
    @Query("SELECT FUNCTION('MONTH', v.fechaVenta) as mes, COUNT(v.idVenta) as totalVentas, " +
           "COALESCE(SUM(v.total), 0) as ingresos " +
           "FROM Venta v " +
           "GROUP BY FUNCTION('MONTH', v.fechaVenta) " +
           "ORDER BY mes")
    List<Object[]> findVentasPorMes();

    @Query("SELECT v FROM Venta v ORDER BY v.fechaVenta DESC") 
    List<Venta> findUltimasVentas(Pageable pageable);
    
    @Query(value = "SELECT MONTH(v.fecha_venta) as mes, " +
           "YEAR(v.fecha_venta) as anio, " +
           "COALESCE(SUM(v.total), 0) as ingresos " +
           "FROM venta v " +
           "WHERE v.fecha_venta >= DATE_SUB(CURRENT_DATE, INTERVAL 6 MONTH) " +
           "GROUP BY YEAR(v.fecha_venta), MONTH(v.fecha_venta) " +
           "ORDER BY anio, mes", nativeQuery = true)
    List<Object[]> findVentasUltimos6Meses();
}