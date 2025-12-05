package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_venta")  // IMPORTANTE: Coincide con BD
    private Integer idDetalleVenta;
    
    @ManyToOne
    @JoinColumn(name = "id_venta")
    private Venta venta;
    
    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;
    
    @Column(name = "cantidad")
    private Integer cantidad;
    
    @Column(name = "precio_unitario")
    private BigDecimal precioUnitario;
    
    @Column(name = "subtotal")  // ¡AGREGA ESTE CAMPO!
    private BigDecimal subtotal;
    
    // Constructor vacío
    public DetalleVenta() {}
    
    // Constructor con parámetros
    public DetalleVenta(Venta venta, Libro libro, Integer cantidad, 
                       BigDecimal precioUnitario, BigDecimal subtotal) {
        this.venta = venta;
        this.libro = libro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
    
    // Getters y Setters COMPLETOS
    public Integer getIdDetalleVenta() { 
        return idDetalleVenta; 
    }
    
    public void setIdDetalleVenta(Integer idDetalleVenta) { 
        this.idDetalleVenta = idDetalleVenta; 
    }
    
    public Venta getVenta() { 
        return venta; 
    }
    
    public void setVenta(Venta venta) { 
        this.venta = venta; 
    }
    
    public Libro getLibro() { 
        return libro; 
    }
    
    public void setLibro(Libro libro) { 
        this.libro = libro; 
    }
    
    public Integer getCantidad() { 
        return cantidad; 
    }
    
    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad; 
    }
    
    public BigDecimal getPrecioUnitario() { 
        return precioUnitario; 
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) { 
        this.precioUnitario = precioUnitario; 
    }
    
    public BigDecimal getSubtotal() {  // ¡AGREGA ESTE GETTER!
        return subtotal; 
    }
    
    public void setSubtotal(BigDecimal subtotal) {  // ¡AGREGA ESTE SETTER!
        this.subtotal = subtotal; 
    }
    
    // Método para calcular subtotal automáticamente
    @Transient
    public BigDecimal calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
}