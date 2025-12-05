package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.model.ItemCarrito;
import com.example.demo.model.Venta;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VentaService {
    
    // Métodos CRUD básicos
    List<Venta> findAll();
    Optional<Venta> findById(Integer id);
    Venta save(Venta venta);
    void deleteById(Integer id);
    
    // Método para procesar compra - CORREGIDO
    @Transactional
    Venta procesarCompra(List<ItemCarrito> carrito,
                         Cliente cliente,
                         String metodoPago,        // CAMBIADO: De MetodoPago a String
                         String tipoEntrega,       // CAMBIADO: De TipoEntrega a String
                         BigDecimal totalCalculado);
}