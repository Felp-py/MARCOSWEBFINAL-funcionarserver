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
import java.util.stream.Collectors;

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
        System.out.println("=== CARGANDO ESTADÍSTICAS ===");
        
        try {
            // 1. Estadísticas básicas
            long totalCompras = ventaRepository.count();
            System.out.println("Total compras: " + totalCompras);
            
            Long totalLibrosComprados = detalleVentaRepository.sumTotalLibrosVendidos();
            if (totalLibrosComprados == null) {
                totalLibrosComprados = 0L;
            }
            System.out.println("Total libros vendidos: " + totalLibrosComprados);
            
            BigDecimal gastoTotal = ventaRepository.sumTotalVentas();
            if (gastoTotal == null) {
                gastoTotal = BigDecimal.ZERO;
            }
            gastoTotal = gastoTotal.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Gasto total: " + gastoTotal);
            
            BigDecimal promedioCompra = ventaRepository.promedioVentas();
            if (promedioCompra == null) {
                promedioCompra = BigDecimal.ZERO;
            }
            promedioCompra = promedioCompra.setScale(2, RoundingMode.HALF_UP);
            System.out.println("Promedio compra: " + promedioCompra);
            
            // 2. Datos para gráficos
            List<Object[]> librosMasVendidos = detalleVentaRepository.findLibrosMasVendidos();
            System.out.println("Libros más vendidos encontrados: " + librosMasVendidos.size());
            
            List<Object[]> distribucionCategorias = detalleVentaRepository.findDistribucionPorCategoria();
            System.out.println("Categorías encontradas: " + distribucionCategorias.size());
            
            List<Object[]> ventasPorMes = ventaRepository.findVentasPorMes();
            System.out.println("Ventas por mes encontradas: " + ventasPorMes.size());
            
            // 3. Preparar datos para gráficos
            // Gráfico de barras: Libros más vendidos
            List<String> labelsLibros = librosMasVendidos.stream()
                    .map(obj -> {
                        String titulo = (String) obj[0];
                        // Acortar títulos largos
                        return titulo.length() > 20 ? titulo.substring(0, 20) + "..." : titulo;
                    })
                    .collect(Collectors.toList());
            
            List<Long> dataLibros = librosMasVendidos.stream()
                    .map(obj -> {
                        Object valor = obj[1];
                        if (valor instanceof Long) {
                            return (Long) valor;
                        } else if (valor instanceof Integer) {
                            return ((Integer) valor).longValue();
                        } else if (valor instanceof BigDecimal) {
                            return ((BigDecimal) valor).longValue();
                        }
                        return 0L;
                    })
                    .collect(Collectors.toList());
            
            // Gráfico de pastel: Distribución por categorías
            List<String> labelsCategorias = distribucionCategorias.stream()
                    .map(obj -> (String) obj[0])
                    .collect(Collectors.toList());
            
            List<Long> dataCategorias = distribucionCategorias.stream()
                    .map(obj -> {
                        Object valor = obj[1];
                        if (valor instanceof Long) {
                            return (Long) valor;
                        } else if (valor instanceof Integer) {
                            return ((Integer) valor).longValue();
                        } else if (valor instanceof BigDecimal) {
                            return ((BigDecimal) valor).longValue();
                        }
                        return 0L;
                    })
                    .collect(Collectors.toList());
            
            // Gráfico de líneas: Ventas por mes
            List<String> labelsMeses = ventasPorMes.stream()
                    .map(obj -> {
                        Object mesObj = obj[0];
                        int mes;
                        if (mesObj instanceof Integer) {
                            mes = (Integer) mesObj;
                        } else if (mesObj instanceof Long) {
                            mes = ((Long) mesObj).intValue();
                        } else {
                            mes = 1;
                        }
                        return getNombreMes(mes);
                    })
                    .collect(Collectors.toList());
            
            List<Long> dataVentasPorMes = ventasPorMes.stream()
                    .map(obj -> {
                        Object valor = obj[1];
                        if (valor instanceof Long) {
                            return (Long) valor;
                        } else if (valor instanceof Integer) {
                            return ((Integer) valor).longValue();
                        }
                        return 0L;
                    })
                    .collect(Collectors.toList());
            
            // 4. Agregar datos al modelo
            model.addAttribute("totalCompras", totalCompras);
            model.addAttribute("totalLibrosComprados", totalLibrosComprados);
            model.addAttribute("gastoTotal", gastoTotal);
            model.addAttribute("promedioCompra", promedioCompra);
            
            // Datos completos para tablas
            model.addAttribute("librosMasVendidos", librosMasVendidos);
            model.addAttribute("distribucionCategorias", distribucionCategorias);
            model.addAttribute("ventasPorMes", ventasPorMes);
            
            // Datos formateados para JavaScript
            model.addAttribute("labelsLibros", labelsLibros);
            model.addAttribute("dataLibros", dataLibros);
            model.addAttribute("labelsCategorias", labelsCategorias);
            model.addAttribute("dataCategorias", dataCategorias);
            model.addAttribute("labelsMeses", labelsMeses);
            model.addAttribute("dataVentasPorMes", dataVentasPorMes);
            
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
            model.addAttribute("ventasPorMes", List.of());
        }
        
        return "estadisticas";
    }
    
    private String getNombreMes(int mes) {
        String[] meses = {
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };
        return mes >= 1 && mes <= 12 ? meses[mes - 1] : "Mes " + mes;
    }
}