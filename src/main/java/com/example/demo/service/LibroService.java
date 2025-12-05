package com.example.demo.service;

import com.example.demo.model.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    List<Libro> findAll();
    Optional<Libro> findById(Long id);
    Optional<Libro> findByTitulo(String titulo);
    Optional<Libro> findByTituloContainingIgnoreCase(String titulo);
    Libro save(Libro libro);
    void deleteById(Long id);
    
    // Nuevos métodos para manejar stock
    boolean verificarStock(Long libroId, int cantidadRequerida);
    boolean actualizarStock(Long libroId, int cantidad);
    boolean verificarYActualizarStock(List<CarritoItem> itemsCarrito);
    
    // Clase auxiliar para items del carrito
    class CarritoItem {
        private Long libroId;
        private String titulo;
        private int cantidad;
        
        public CarritoItem() {}
        
        public CarritoItem(Long libroId, String titulo, int cantidad) {
            this.libroId = libroId;
            this.titulo = titulo;
            this.cantidad = cantidad;
        }
        
        public Long getLibroId() { return libroId; }
        public void setLibroId(Long libroId) { this.libroId = libroId; }
        
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }
        
        public int getCantidad() { return cantidad; }
        public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    }
}