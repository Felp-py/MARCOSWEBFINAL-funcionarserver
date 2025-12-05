package com.example.demo.controller;

import com.example.demo.model.ItemCarrito;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@SessionAttributes({"carrito", "datosCompra"})
public class CheckoutController {
    
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.18");
    private static final BigDecimal COSTO_ENVIO = new BigDecimal("10.00");
    private static final BigDecimal TASA_CAMBIO_FIJA = new BigDecimal("3.80"); // CAMBIO FIJO
    
    @ModelAttribute("datosCompra")
    public DatosCompra inicializarDatosCompra() {
        return new DatosCompra();
    }
    
    @GetMapping
    public String mostrarCheckout(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                                 @RequestParam(value = "moneda", defaultValue = "PEN") String moneda,
                                 Model model) {
        
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Calcular totales iniciales (sin envío)
        BigDecimal subtotal = calcularSubtotal(carrito);
        BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuestos);
        
        // Conversión de moneda con tasa fija 3.80
        BigDecimal tasaCambio = TASA_CAMBIO_FIJA;
        BigDecimal totalEnMoneda = total;
        
        if ("USD".equals(moneda)) {
            totalEnMoneda = total.divide(tasaCambio, 2, RoundingMode.HALF_UP);
        }
        
        // Agregar al modelo
        model.addAttribute("carrito", carrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("total", total);
        model.addAttribute("totalEnMoneda", totalEnMoneda);
        model.addAttribute("moneda", moneda);
        model.addAttribute("tasaCambio", tasaCambio);
        
        return "checkout";
    }
    
    @PostMapping("/procesar")
    public String procesarPago(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                              @ModelAttribute("datosCompra") DatosCompra datosCompra,
                              @RequestParam String tipoEntrega,
                              @RequestParam String metodoPago,
                              @RequestParam(defaultValue = "PEN") String moneda,
                              @RequestParam(required = false) String direccion,
                              @RequestParam(required = false) String distrito,
                              @RequestParam(required = false) String ciudad,
                              @RequestParam(required = false) String referencia,
                              RedirectAttributes redirectAttributes) {
        
        try {
            // 1. VALIDACIONES
            if ("delivery".equals(tipoEntrega)) {
                if (direccion == null || direccion.trim().isEmpty() ||
                    distrito == null || distrito.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Por favor completa la dirección para delivery");
                    return "redirect:/checkout";
                }
            }
            
            if (metodoPago == null || metodoPago.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    "Por favor selecciona un método de pago");
                return "redirect:/checkout";
            }
            
            // 2. CALCULAR TOTALES
            BigDecimal subtotal = calcularSubtotal(carrito);
            BigDecimal envio = "delivery".equals(tipoEntrega) ? COSTO_ENVIO : BigDecimal.ZERO;
            BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(envio).add(impuestos);
            
            // Conversión si es USD
            BigDecimal totalFinal = total;
            if ("USD".equals(moneda)) {
                totalFinal = total.divide(TASA_CAMBIO_FIJA, 2, RoundingMode.HALF_UP);
            }
            
            // 3. GUARDAR DATOS DE LA COMPRA
            String numeroPedido = "PED-" + System.currentTimeMillis();
            
            datosCompra.setTipoEntrega(tipoEntrega);
            datosCompra.setMetodoPago(metodoPago);
            datosCompra.setDireccion(direccion);
            datosCompra.setDistrito(distrito);
            datosCompra.setCiudad(ciudad != null ? ciudad : "Lima");
            datosCompra.setReferencia(referencia);
            datosCompra.setMoneda(moneda);
            datosCompra.setSubtotal(subtotal);
            datosCompra.setEnvio(envio);
            datosCompra.setImpuestos(impuestos);
            datosCompra.setTotal(totalFinal);
            datosCompra.setNumeroPedido(numeroPedido);
            datosCompra.setFecha(LocalDateTime.now());
            
            // 4. PREPARAR DATOS PARA REDIRECCIÓN
            redirectAttributes.addFlashAttribute("pagoAprobado", true);
            redirectAttributes.addFlashAttribute("numeroPedido", numeroPedido);
            redirectAttributes.addFlashAttribute("carrito", carrito);
            redirectAttributes.addFlashAttribute("datosCompra", datosCompra);
            
            return "redirect:/checkout/confirmacion";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar el pago: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
    
    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                                     @ModelAttribute("datosCompra") DatosCompra datosCompra,
                                     Model model) {
        
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }
        
        if (datosCompra.getNumeroPedido() == null) {
            return "redirect:/checkout";
        }
        
        return "checkout/confirmacion";
    }
    
    private BigDecimal calcularSubtotal(List<ItemCarrito> carrito) {
        if (carrito == null) return BigDecimal.ZERO;
        
        return carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }
    
    // Clase DTO para datos de la compra
    public static class DatosCompra {
        private String tipoEntrega;
        private String metodoPago;
        private String direccion;
        private String distrito;
        private String ciudad;
        private String referencia;
        private String moneda;
        private BigDecimal subtotal;
        private BigDecimal envio;
        private BigDecimal impuestos;
        private BigDecimal total;
        private String numeroPedido;
        private LocalDateTime fecha;
        
        public DatosCompra() {}
        
        public String getTipoEntrega() { return tipoEntrega; }
        public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }
        
        public String getMetodoPago() { return metodoPago; }
        public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
        
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
        
        public String getDistrito() { return distrito; }
        public void setDistrito(String distrito) { this.distrito = distrito; }
        
        public String getCiudad() { return ciudad; }
        public void setCiudad(String ciudad) { this.ciudad = ciudad; }
        
        public String getReferencia() { return referencia; }
        public void setReferencia(String referencia) { this.referencia = referencia; }
        
        public String getMoneda() { return moneda; }
        public void setMoneda(String moneda) { this.moneda = moneda; }
        
        public BigDecimal getSubtotal() { return subtotal; }
        public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
        
        public BigDecimal getEnvio() { return envio; }
        public void setEnvio(BigDecimal envio) { this.envio = envio; }
        
        public BigDecimal getImpuestos() { return impuestos; }
        public void setImpuestos(BigDecimal impuestos) { this.impuestos = impuestos; }
        
        public BigDecimal getTotal() { return total; }
        public void setTotal(BigDecimal total) { this.total = total; }
        
        public String getNumeroPedido() { return numeroPedido; }
        public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }
        
        public LocalDateTime getFecha() { return fecha; }
        public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
        
        public String getFechaFormateada() {
            if (fecha != null) {
                return fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            return "";
        }
        
        public String getSimboloMoneda() {
            return "PEN".equals(moneda) ? "S/" : "US$";
        }
    }
}