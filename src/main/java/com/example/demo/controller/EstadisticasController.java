package com.example.demo.controller;

import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequestMapping("/compras")
@PreAuthorize("hasRole('ADMIN')")
public class EstadisticasController {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    
    @Autowired
    private LibroRepository libroRepository;
    
    @GetMapping("/estadisticas")
    public String verEstadisticas(Model model) {
        System.out.println("=== CARGANDO ESTADÍSTICAS ACTUALIZADAS ===");
        
        try {
            // 1. Estadísticas básicas - CONSULTA DIRECTA A BD
            long totalCompras = ventaRepository.count();
            System.out.println("Total compras en BD: " + totalCompras);
            
            Long totalLibrosComprados = detalleVentaRepository.sumTotalLibrosVendidos();
            if (totalLibrosComprados == null) {
                totalLibrosComprados = 0L;
            }
            System.out.println("Total libros vendidos en BD: " + totalLibrosComprados);
            
            BigDecimal gastoTotal = ventaRepository.sumTotalVentas();
            if (gastoTotal == null) {
                gastoTotal = BigDecimal.ZERO;
            }
            gastoTotal = gastoTotal.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Gasto total en BD: " + gastoTotal);
            
            BigDecimal promedioCompra = ventaRepository.promedioVentas();
            if (promedioCompra == null) {
                promedioCompra = BigDecimal.ZERO;
            }
            promedioCompra = promedioCompra.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Promedio compra en BD: " + promedioCompra);
            
            // 2. Datos para gráficos
            List<Object[]> librosMasVendidos = detalleVentaRepository.findLibrosMasVendidos();
            System.out.println("Libros más vendidos encontrados: " + librosMasVendidos.size());
            
            List<Object[]> distribucionCategorias = detalleVentaRepository.findDistribucionPorCategoria();
            System.out.println("Categorías encontradas: " + distribucionCategorias.size());
            
            // 3. Agregar datos al modelo
            model.addAttribute("totalCompras", totalCompras);
            model.addAttribute("totalLibrosComprados", totalLibrosComprados);
            model.addAttribute("gastoTotal", gastoTotal);
            model.addAttribute("promedioCompra", promedioCompra);
            model.addAttribute("librosMasVendidos", librosMasVendidos);
            model.addAttribute("distribucionCategorias", distribucionCategorias);
            
            System.out.println("=== ESTADÍSTICAS CARGADAS EXITOSAMENTE ===");
            
        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
            e.printStackTrace();
            
            // Datos por defecto en caso de error
            model.addAttribute("totalCompras", 0);
            model.addAttribute("totalLibrosComprados", 0);
            model.addAttribute("gastoTotal", BigDecimal.ZERO);
            model.addAttribute("promedioCompra", BigDecimal.ZERO);
            model.addAttribute("librosMasVendidos", List.of());
            model.addAttribute("distribucionCategorias", List.of());
        }
        
        return "estadisticas";
    }
}