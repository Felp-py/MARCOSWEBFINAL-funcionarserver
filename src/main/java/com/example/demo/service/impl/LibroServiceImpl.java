package com.example.demo.service.impl;

import com.example.demo.model.Libro;
import com.example.demo.repository.LibroRepository;
import com.example.demo.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> findAll() {
        return libroRepository.findAll();
    }

    @Override
    public Optional<Libro> findById(Long id) {
        return libroRepository.findById(id);
    }
    
    @Override
    public Optional<Libro> findByTitulo(String titulo) {
        return libroRepository.findByTitulo(titulo);
    }
    
    @Override
    public Optional<Libro> findByTituloContainingIgnoreCase(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }

    @Override
    public Libro save(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public void deleteById(Long id) {
        libroRepository.deleteById(id);
    }
    
    @Override
    public boolean verificarStock(Long libroId, int cantidadRequerida) {
        Optional<Libro> libroOpt = findById(libroId);
        if (libroOpt.isPresent()) {
            Libro libro = libroOpt.get();
            return libro.getStock() >= cantidadRequerida;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean actualizarStock(Long libroId, int cantidad) {
        try {
            // Usar la consulta personalizada para actualización atómica
            int filasActualizadas = libroRepository.actualizarStock(libroId, cantidad);
            return filasActualizadas > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean verificarYActualizarStock(List<CarritoItem> itemsCarrito) {
        try {
            // Primero verificar stock para todos los items
            for (CarritoItem item : itemsCarrito) {
                if (!verificarStock(item.getLibroId(), item.getCantidad())) {
                    throw new IllegalArgumentException("Stock insuficiente para el libro ID: " + item.getLibroId());
                }
            }
            
            // Si todo está OK, actualizar stock
            for (CarritoItem item : itemsCarrito) {
                boolean actualizado = actualizarStock(item.getLibroId(), -item.getCantidad());
                if (!actualizado) {
                    throw new IllegalArgumentException("Error al actualizar stock para libro ID: " + item.getLibroId());
                }
                System.out.println("Stock actualizado - Libro ID: " + item.getLibroId() + 
                                 ", Cantidad: -" + item.getCantidad());
            }
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}