package com.example.demo.controller;

import com.example.demo.model.ItemCarrito;
import com.example.demo.repository.VentaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
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
    @ModelAttribute("carrito")
    public List<ItemCarrito> inicializarCarrito() {
        return new ArrayList<>();
    }

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