package com.example.demo.service;

import com.example.demo.model.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VentaService {
    List<Venta> findAll();
    Optional<Venta> findById(Integer id);  // Cambiado a Integer
    Venta save(Venta venta);
    void deleteById(Integer id);  // Cambiado a Integer
    
    Venta procesarCompra(List<ItemCarrito> carrito,
                        Cliente cliente,
                        MetodoPago metodoPago,
                        TipoEntrega tipoEntrega,
                        BigDecimal totalCalculado);
}