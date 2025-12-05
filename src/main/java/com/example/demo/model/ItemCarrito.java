package com.example.demo.model;

import java.math.BigDecimal;

public class ItemCarrito {
    private Long idLibro;
    private String titulo;
    private BigDecimal precio;
    private Integer cantidad;
    private String imagenUrl; // Asegúrate de tener este campo

    // Constructor
    public ItemCarrito(Long idLibro, String titulo, BigDecimal precio, Integer cantidad, String imagenUrl) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagenUrl = imagenUrl;
    }

    // Getter para subtotal
    public BigDecimal getSubtotal() {
        return precio.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y Setters
    public Long getIdLibro() { return idLibro; }
    public void setIdLibro(Long idLibro) { this.idLibro = idLibro; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public String getImagenUrl() { return imagenUrl; } // Getter para imagen
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; } // Setter para imagen
}