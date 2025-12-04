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
                                MetodoPago metodoPago,
                                TipoEntrega tipoEntrega,
                                BigDecimal totalCalculado) {

        Venta nuevaVenta = new Venta();
        nuevaVenta.setCliente(cliente);
        nuevaVenta.setMetodoPago(metodoPago);
        nuevaVenta.setTipoEntrega(tipoEntrega);
        nuevaVenta.setTotal(totalCalculado);

        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        for (ItemCarrito item : carrito) {
            // Esto ya debería funcionar si libroRepository usa Integer
            Libro libro = libroRepository.findById(item.getIdLibro())
                    .orElseThrow(() ->
                            new RuntimeException("Libro no encontrado con ID: " + item.getIdLibro())
                    );

            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(ventaGuardada);
            detalle.setLibro(libro);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecio());

            detalleVentaRepository.save(detalle);
        }

        return ventaGuardada;
    }
}