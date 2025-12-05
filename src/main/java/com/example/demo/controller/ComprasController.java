package com.example.demo.controller;

import com.example.demo.model.ItemCarrito;
import com.example.demo.repository.VentaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.servlet.http.HttpSession; // CAMBIADO: de javax.servlet a jakarta.servlet
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

@Controller
@SessionAttributes("carrito")
public class ComprasController {

    private final VentaRepository ventaRepository;

    public ComprasController(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    // Inicializar carrito si no existe
    @ModelAttribute("carrito")
    public List<ItemCarrito> inicializarCarrito() {
        return new ArrayList<>();
    }

    @GetMapping("/compras/estadisticas")
    public String verEstadisticas(@ModelAttribute("carrito") List<ItemCarrito> carrito, 
                                  Model model,
                                  HttpSession session) { // Ahora usa jakarta.servlet
        
        System.out.println("=== DEBUG ESTADÍSTICAS ===");
        System.out.println("Carrito tamaño: " + carrito.size());
        
        // 1. Datos del carrito actual
        model.addAttribute("carrito", carrito);
        
        // 2. Estadísticas generales (simuladas)
        int totalCompras = 5;
        int totalLibrosComprados = 12;
        BigDecimal gastoTotal = new BigDecimal("345.75");
        BigDecimal promedioCompra = totalCompras > 0 
            ? gastoTotal.divide(BigDecimal.valueOf(totalCompras), 2, java.math.RoundingMode.HALF_UP) 
            : BigDecimal.ZERO;
        
        model.addAttribute("totalCompras", totalCompras);
        model.addAttribute("totalLibrosComprados", totalLibrosComprados);
        model.addAttribute("gastoTotal", gastoTotal);
        model.addAttribute("promedioCompra", promedioCompra);
        
        // 3. Últimas compras (simuladas)
        List<CompraDTO> ultimasCompras = new ArrayList<>();
        ultimasCompras.add(new CompraDTO("2024-01-15 10:30", "Cien años de soledad, Fundación", 2, new BigDecimal("54.49"), true));
        ultimasCompras.add(new CompraDTO("2024-01-10 14:20", "Harry Potter y la piedra filosofal", 1, new BigDecimal("22.99"), true));
        ultimasCompras.add(new CompraDTO("2024-01-05 09:15", "1984, El principito", 2, new BigDecimal("34.50"), true));
        
        model.addAttribute("ultimasCompras", ultimasCompras);
        
        // 4. Calcular subtotal del carrito para gráficos
        BigDecimal subtotal = BigDecimal.ZERO;
        if (carrito != null && !carrito.isEmpty()) {
            subtotal = carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        model.addAttribute("subtotal", subtotal);
        
        System.out.println("Estadísticas cargadas - Carrito: " + (carrito != null ? carrito.size() : 0) + " items");
        System.out.println("Subtotal: " + subtotal);
        
        return "estadisticas"; // Asegúrate que el template esté en templates/estadisticas.html
    }

    // Clase DTO interna para las compras
    public static class CompraDTO {
        private String fecha;
        private String libros;
        private int cantidadTotal;
        private BigDecimal total;
        private boolean completada;
        
        public CompraDTO(String fecha, String libros, int cantidadTotal, BigDecimal total, boolean completada) {
            this.fecha = fecha;
            this.libros = libros;
            this.cantidadTotal = cantidadTotal;
            this.total = total;
            this.completada = completada;
        }
        
        // Getters y setters
        public String getFecha() { return fecha; }
        public void setFecha(String fecha) { this.fecha = fecha; }
        
        public String getLibros() { return libros; }
        public void setLibros(String libros) { this.libros = libros; }
        
        public int getCantidadTotal() { return cantidadTotal; }
        public void setCantidadTotal(int cantidadTotal) { this.cantidadTotal = cantidadTotal; }
        
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        
        public boolean isCompletada() { return completada; }
        public void setCompletada(boolean completada) { this.completada = completada; }
    }
}