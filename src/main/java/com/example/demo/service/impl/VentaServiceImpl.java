package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.DetalleVentaRepository;
import com.example.demo.repository.LibroRepository;
import com.example.demo.repository.VentaRepository;
import com.example.demo.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Optional<Venta> findById(Integer id) {
        return ventaRepository.findById(id);
    }

    @Override
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public void deleteById(Integer id) {
        ventaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Venta procesarCompra(List<ItemCarrito> carrito,
                                Cliente cliente,
                                String metodoPago,        // CORREGIDO: Ahora es String
                                String tipoEntrega,       // CORREGIDO: Ahora es String
                                BigDecimal totalCalculado) {

        // 1. Crear y guardar la venta
        Venta nuevaVenta = new Venta();
        nuevaVenta.setCliente(cliente);
        nuevaVenta.setMetodoPago(metodoPago);        // CORREGIDO: Ahora recibe String
        nuevaVenta.setTipoEntrega(tipoEntrega);      // CORREGIDO: Ahora recibe String
        nuevaVenta.setTotal(totalCalculado);
        nuevaVenta.setFechaVenta(LocalDateTime.now());

        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        // 2. Crear detalles de venta y actualizar stock
        for (ItemCarrito item : carrito) {
            // Buscar libro (nota: tu LibroRepository usa Long como ID)
            Optional<Libro> libroOpt = libroRepository.findById(item.getIdLibro());
            
            if (libroOpt.isEmpty()) {
                throw new RuntimeException("Libro no encontrado con ID: " + item.getIdLibro());
            }
            
            Libro libro = libroOpt.get();
            
            // Verificar stock
            if (libro.getStock() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para: " + libro.getTitulo() + 
                                           ". Stock disponible: " + libro.getStock());
            }
            
            // Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(ventaGuardada);
            detalle.setLibro(libro);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());
            
            // Calcular subtotal (esto es importante para las estadísticas)
            BigDecimal subtotal = item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            detalle.setSubtotal(subtotal);
            
            detalleVentaRepository.save(detalle);
            
            // Actualizar stock del libro
            libro.setStock(libro.getStock() - item.getCantidad());
            libroRepository.save(libro);
        }

        return ventaGuardada;
    }
}