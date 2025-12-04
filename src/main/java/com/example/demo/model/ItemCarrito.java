package com.example.demo.model;

import java.math.BigDecimal;

// NO usar @Entity, @Table, etc. - NO es una entidad JPA
public class ItemCarrito {
    private Long idLibro;  // Long, igual que en Libro
    private String titulo;
    private BigDecimal precio;
    private Integer cantidad;
    
    // Constructor con Long
    public ItemCarrito(Long idLibro, String titulo, BigDecimal precio, Integer cantidad) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.precio = precio;
        this.cantidad = cantidad;
    }
    
    // Constructor vacío
    public ItemCarrito() {}
    
    // Getters y Setters
    public Long getIdLibro() {
        return idLibro;
    }
    
    public void setIdLibro(Long idLibro) {
        this.idLibro = idLibro;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getSubtotal() {
        if (precio != null && cantidad != null) {
            return precio.multiply(BigDecimal.valueOf(cantidad));
        }
        return BigDecimal.ZERO;
    }
}