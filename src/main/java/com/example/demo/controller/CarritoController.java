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
@SessionAttributes({"carrito", "tipoCambio"})
@RequestMapping("/carrito")
public class CarritoController {

    private final LibroRepository libroRepository;
    
    // Constantes para cálculos
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.18"); // 18% IGV
    private static final BigDecimal COSTO_ENVIO_DOMICILIO = new BigDecimal("10.00");
    private static final BigDecimal ENVIO_GRATIS = BigDecimal.ZERO;
    
    // Tasa de cambio predeterminada
    private static final BigDecimal TASA_CAMBIO_DEFAULT = new BigDecimal("3.850");

    public CarritoController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @ModelAttribute("carrito")
    public List<ItemCarrito> inicializarCarrito() {
        return new ArrayList<>();
    }
    
    @ModelAttribute("tipoCambio")
    public BigDecimal inicializarTipoCambio() {
        return TASA_CAMBIO_DEFAULT;
    }

    // Página principal del carrito
    @GetMapping
    public String verCarrito(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                            @ModelAttribute("tipoCambio") BigDecimal tipoCambio,
                            Model model) {
        
        calcularTotales(carrito, tipoCambio, model);
        
        return "carrito";
    }

    // Agregar al carrito
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
            
            // Obtener imagen del libro
            String imagenUrl = libro.getImagenUrl();
            if (imagenUrl == null || imagenUrl.trim().isEmpty()) {
                imagenUrl = "/img/libros/default.jpg";
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
                // Crear nuevo item CON IMAGEN
                ItemCarrito nuevoItem = new ItemCarrito(
                    libro.getIdLibro(),
                    libro.getTitulo(),
                    libro.getPrecio(),
                    cantidad,
                    imagenUrl  // Pasar la imagen del libro
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
        
        if (cantidad == null) {
            return "redirect:/carrito";
        }
        
        Optional<ItemCarrito> itemOptional = carrito.stream()
            .filter(item -> item.getIdLibro().equals(idLibro))
            .findFirst();
        
        if (itemOptional.isEmpty()) {
            return "redirect:/carrito";
        }
        
        ItemCarrito item = itemOptional.get();
        
        if (cantidad < 1) {
            carrito.remove(item);
            return "redirect:/carrito";
        }
        
        libroRepository.findById(idLibro).ifPresent(libro -> {
            int cantidadFinal = cantidad;
            if (libro.getStock() != null && cantidad > libro.getStock()) {
                cantidadFinal = libro.getStock();
            }
            item.setCantidad(cantidadFinal);
        });
        
        return "redirect:/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarItem(@RequestParam Long idLibro,
                               @ModelAttribute("carrito") List<ItemCarrito> carrito) {
        carrito.removeIf(item -> item.getIdLibro().equals(idLibro));
        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito(@ModelAttribute("carrito") List<ItemCarrito> carrito) {
        carrito.clear();
        return "redirect:/carrito";
    }

    private void calcularTotales(List<ItemCarrito> carrito, 
                                BigDecimal tipoCambio,
                                Model model) {
        // Subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        if (carrito != null && !carrito.isEmpty()) {
            subtotal = carrito.stream()
                    .map(item -> item.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        // Envío - EN CARRITO ES SIEMPRE GRATIS
        BigDecimal envio = ENVIO_GRATIS;
        
        // Impuestos (18% sobre el subtotal)
        BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Total
        BigDecimal total = subtotal.add(envio).add(impuestos)
                .setScale(2, RoundingMode.HALF_UP);
        
        // Calcular total en USD (para conversión)
        BigDecimal totalEnUsd = total.divide(tipoCambio, 2, RoundingMode.HALF_UP);
        
        // Agregar todos los valores al modelo
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("envio", envio);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("total", total);
        model.addAttribute("tipoCambio", tipoCambio);
        model.addAttribute("totalEnUsd", totalEnUsd);
        model.addAttribute("carrito", carrito);
    }
    
    // Continuar al checkout
    @GetMapping("/checkout")
    public String continuarAlCheckout(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                                      Model model) {
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito?error=carrito_vacio";
        }
        
        // Redirigir al checkout controller
        return "redirect:/checkout";
    }
}