package com.example.demo.controller;

import com.example.demo.repository.*;
import com.example.demo.model.Venta; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
            
            List<Object[]> librosMasVendidos = detalleVentaRepository.findLibrosMasVendidos();
            System.out.println("Libros más vendidos encontrados: " + librosMasVendidos.size());
            
            List<Object[]> distribucionCategorias = detalleVentaRepository.findDistribucionPorCategoria();
            System.out.println("Categorías encontradas: " + distribucionCategorias.size());
            
            List<Object[]> ventasPorMes = ventaRepository.findVentasPorMes();
            System.out.println("Ventas por mes encontradas: " + ventasPorMes.size());
            
            List<Object[]> ventasUltimos6Meses = ventaRepository.findVentasUltimos6Meses();
            System.out.println("Ventas últimos 6 meses: " + ventasUltimos6Meses.size());
            
            List<Venta> ultimasVentas = ventaRepository.findUltimasVentas(
                PageRequest.of(0, 5, Sort.by("fechaVenta").descending())
            );
            System.out.println("Últimas ventas encontradas: " + ultimasVentas.size());
            
            String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                                     "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
            
            Map<Integer, BigDecimal> ventasMensualesMap = new HashMap<>();
            for (Object[] venta : ventasUltimos6Meses) {
                Integer mes = (Integer) venta[0];
                Integer anio = (Integer) venta[1];
                BigDecimal ingresos = (BigDecimal) venta[2];
                ventasMensualesMap.put(mes + anio * 100, ingresos);
            }
            
            model.addAttribute("totalCompras", totalCompras);
            model.addAttribute("totalLibrosComprados", totalLibrosComprados);
            model.addAttribute("gastoTotal", gastoTotal);
            model.addAttribute("promedioCompra", promedioCompra);
            model.addAttribute("librosMasVendidos", librosMasVendidos);
            model.addAttribute("distribucionCategorias", distribucionCategorias);
            model.addAttribute("ventasPorMes", ventasPorMes);
            model.addAttribute("ventasUltimos6Meses", ventasUltimos6Meses);
            model.addAttribute("ultimasVentas", ultimasVentas);
            model.addAttribute("nombresMeses", nombresMeses);
            model.addAttribute("ventasMensualesMap", ventasMensualesMap);
            
            System.out.println("=== ESTADÍSTICAS CARGADAS EXITOSAMENTE ===");
            
        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("totalCompras", 0);
            model.addAttribute("totalLibrosComprados", 0);
            model.addAttribute("gastoTotal", BigDecimal.ZERO);
            model.addAttribute("promedioCompra", BigDecimal.ZERO);
            model.addAttribute("librosMasVendidos", List.of());
            model.addAttribute("distribucionCategorias", List.of());
            model.addAttribute("ventasPorMes", List.of());
            model.addAttribute("ventasUltimos6Meses", List.of());
            model.addAttribute("ultimasVentas", List.of());
            model.addAttribute("nombresMeses", new String[]{});
            model.addAttribute("ventasMensualesMap", new HashMap<>());
            model.addAttribute("formatearFecha", new java.util.function.Function<LocalDateTime, String>() {
        @Override
        public String apply(LocalDateTime fecha) {
            if (fecha == null) return "";
            return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    });
    
    System.out.println("=== ESTADÍSTICAS CARGADAS EXITOSAMENTE ===");
        }
        
        return "estadisticas";
    }


@GetMapping("/estadisticas-simple")
public String verEstadisticasSimple(Model model) {
     System.out.println("=== CARGANDO ESTADÍSTICAS ACTUALIZADAS ===");
        
        try {
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
            
            List<Object[]> librosMasVendidos = detalleVentaRepository.findLibrosMasVendidos();
            System.out.println("Libros más vendidos encontrados: " + librosMasVendidos.size());
            
            List<Object[]> distribucionCategorias = detalleVentaRepository.findDistribucionPorCategoria();
            System.out.println("Categorías encontradas: " + distribucionCategorias.size());
            
            List<Object[]> ventasPorMes = ventaRepository.findVentasPorMes();
            System.out.println("Ventas por mes encontradas: " + ventasPorMes.size());
            
            List<Object[]> ventasUltimos6Meses = ventaRepository.findVentasUltimos6Meses();
            System.out.println("Ventas últimos 6 meses: " + ventasUltimos6Meses.size());
            
            List<Venta> ultimasVentas = ventaRepository.findUltimasVentas(
                PageRequest.of(0, 5, Sort.by("fechaVenta").descending())
            );
            System.out.println("Últimas ventas encontradas: " + ultimasVentas.size());
            
            String[] nombresMeses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", 
                                     "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
            
            Map<Integer, BigDecimal> ventasMensualesMap = new HashMap<>();
            for (Object[] venta : ventasUltimos6Meses) {
                Integer mes = (Integer) venta[0];
                Integer anio = (Integer) venta[1];
                BigDecimal ingresos = (BigDecimal) venta[2];
                ventasMensualesMap.put(mes + anio * 100, ingresos);
            }
            
            model.addAttribute("totalCompras", totalCompras);
            model.addAttribute("totalLibrosComprados", totalLibrosComprados);
            model.addAttribute("gastoTotal", gastoTotal);
            model.addAttribute("promedioCompra", promedioCompra);
            model.addAttribute("librosMasVendidos", librosMasVendidos);
            model.addAttribute("distribucionCategorias", distribucionCategorias);
            model.addAttribute("ventasPorMes", ventasPorMes);
            model.addAttribute("ventasUltimos6Meses", ventasUltimos6Meses);
            model.addAttribute("ultimasVentas", ultimasVentas);
            model.addAttribute("nombresMeses", nombresMeses);
            model.addAttribute("ventasMensualesMap", ventasMensualesMap);
            
            System.out.println("=== ESTADÍSTICAS CARGADAS EXITOSAMENTE ===");
            
        } catch (Exception e) {
            System.err.println("Error cargando estadísticas: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("totalCompras", 0);
            model.addAttribute("totalLibrosComprados", 0);
            model.addAttribute("gastoTotal", BigDecimal.ZERO);
            model.addAttribute("promedioCompra", BigDecimal.ZERO);
            model.addAttribute("librosMasVendidos", List.of());
            model.addAttribute("distribucionCategorias", List.of());
            model.addAttribute("ventasPorMes", List.of());
            model.addAttribute("ventasUltimos6Meses", List.of());
            model.addAttribute("ultimasVentas", List.of());
            model.addAttribute("nombresMeses", new String[]{});
            model.addAttribute("ventasMensualesMap", new HashMap<>());
            model.addAttribute("formatearFecha", new java.util.function.Function<LocalDateTime, String>() {
        @Override
        public String apply(LocalDateTime fecha) {
            if (fecha == null) return "";
            return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    });
    
    System.out.println("=== ESTADÍSTICAS CARGADAS EXITOSAMENTE ===");
        }
        
    return "estadisticas-simple";
}


}
