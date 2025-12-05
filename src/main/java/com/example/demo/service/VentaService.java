package com.example.demo.service;

import com.example.demo.model.Cliente;
import com.example.demo.model.ItemCarrito;
import com.example.demo.model.MetodoPago;
import com.example.demo.model.TipoEntrega;
import com.example.demo.model.Venta;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface VentaService {
    
    List<Venta> findAll();
    Optional<Venta> findById(Integer id);
    Venta save(Venta venta);
    void deleteById(Integer id);
    
    @Transactional
    Venta procesarCompra(List<ItemCarrito> carrito,
                         Cliente cliente,
                         MetodoPago metodoPago,        
                         TipoEntrega tipoEntrega,      
                         BigDecimal totalCalculado);
}