package com.example.demo.controller;

import com.example.demo.model.ItemCarrito;
import com.example.demo.model.Libro;
import com.example.demo.repository.LibroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes("carrito")
@RequestMapping("/carrito")
public class CarritoController {

    private final LibroRepository libroRepository;
    
    // Constantes para cálculos
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.18"); // 18% IGV
    private static final BigDecimal COSTO_ENVIO = new BigDecimal("10.00");
    private static final BigDecimal MINIMO_ENVIO_GRATIS = new BigDecimal("50.00");

    public CarritoController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @ModelAttribute("carrito")
    public List<ItemCarrito> inicializarCarrito() {
        return new ArrayList<>();
    }

    // Página principal del carrito
    @GetMapping
    public String verCarrito(@ModelAttribute("carrito") List<ItemCarrito> carrito, 
                            Model model) {
        calcularTotales(carrito, model);
        return "carrito"; // Esta será tu nueva plantilla
    }

    // Agregar al carrito (desde catálogo o modal)
    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam Long idLibro,
                                   @RequestParam(defaultValue = "1") int cantidad,
                                   @ModelAttribute("carrito") List<ItemCarrito> carrito) {

        Optional<Libro> libroOpt = libroRepository.findById(idLibro);

        if (libroOpt.isPresent() && cantidad > 0) {
            Libro libro = libroOpt.get();
            
            // Verificar stock
            if (libro.getStock() != null && libro.getStock() < cantidad) {
                return "redirect:/carrito?error=stock_insuficiente";
            }
            
            // Buscar si ya existe en el carrito
            Optional<ItemCarrito> itemExistente = carrito.stream()
                    .filter(item -> item.getIdLibro().equals(idLibro))
                    .findFirst();

            if (itemExistente.isPresent()) {
                // Actualizar cantidad
                ItemCarrito item = itemExistente.get();
                int nuevaCantidad = item.getCantidad() + cantidad;
                
                // Verificar stock total
                if (libro.getStock() == null || nuevaCantidad <= libro.getStock()) {
                    item.setCantidad(nuevaCantidad);
                }
            } else {
                // Crear nuevo item
                ItemCarrito nuevoItem = new ItemCarrito(
                    libro.getIdLibro(),
                    libro.getTitulo(),
                    libro.getPrecio(),
                    cantidad
                );
                carrito.add(nuevoItem);
            }
        }

        return "redirect:/carrito";
    }

@PostMapping("/actualizar")
public String actualizarCantidad(@RequestParam Long idLibro,
                                 @RequestParam Integer cantidad,
                                 @ModelAttribute("carrito") List<ItemCarrito> carrito) {
    
    // Validar parámetro
    if (cantidad == null) {
        return "redirect:/carrito";
    }
    
    // Buscar el item usando Optional
    Optional<ItemCarrito> itemOptional = carrito.stream()
        .filter(item -> item.getIdLibro().equals(idLibro))
        .findFirst();
    
    if (itemOptional.isEmpty()) {
        return "redirect:/carrito"; // Item no encontrado
    }
    
    ItemCarrito item = itemOptional.get();
    
    if (cantidad < 1) {
        // Eliminar del carrito
        carrito.remove(item);
        return "redirect:/carrito";
    }
    
    // Verificar stock y actualizar
    libroRepository.findById(idLibro).ifPresent(libro -> {
        int cantidadFinal = cantidad;
        if (libro.getStock() != null && cantidad > libro.getStock()) {
            cantidadFinal = libro.getStock();
        }
        item.setCantidad(cantidadFinal);
    });
    
    return "redirect:/carrito";
}

    // Eliminar un item específico
    @PostMapping("/eliminar")
    public String eliminarItem(@RequestParam Long idLibro,
                               @ModelAttribute("carrito") List<ItemCarrito> carrito) {
        carrito.removeIf(item -> item.getIdLibro().equals(idLibro));
        return "redirect:/carrito";
    }

    // Vaciar todo el carrito
    @PostMapping("/vaciar")
    public String vaciarCarrito(@ModelAttribute("carrito") List<ItemCarrito> carrito) {
        carrito.clear();
        return "redirect:/carrito";
    }

    // Método auxiliar para calcular totales
    private void calcularTotales(List<ItemCarrito> carrito, Model model) {
        // Subtotal
        BigDecimal subtotal = carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Envío (gratis si > 50)
        BigDecimal envio = subtotal.compareTo(MINIMO_ENVIO_GRATIS) >= 0 
                ? BigDecimal.ZERO 
                : COSTO_ENVIO;
        
        // Impuestos
        BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Total
        BigDecimal total = subtotal.add(envio).add(impuestos)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Agregar al modelo
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", envio);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("total", total);
        model.addAttribute("minimoEnvioGratis", MINIMO_ENVIO_GRATIS);
        
        // Para compatibilidad con tu plantilla actual
        model.addAttribute("carrito", carrito);
    }
}