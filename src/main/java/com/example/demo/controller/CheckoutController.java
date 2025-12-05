package com.example.demo.controller;

import com.example.demo.model.ItemCarrito;
import com.example.demo.service.CurrencyService;
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
    
    private final CurrencyService currencyService;
    
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.18");
    private static final BigDecimal COSTO_ENVIO = new BigDecimal("10.00");
    
    public CheckoutController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
    
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
        
        // Conversión de moneda
        BigDecimal tasaCambio = BigDecimal.ONE;
        BigDecimal totalEnMoneda = total;
        
        if ("USD".equals(moneda)) {
            tasaCambio = currencyService.getExchangeRate("USD");
            totalEnMoneda = currencyService.convertToUSD(total);
        }
        
        // Agregar al modelo
        model.addAttribute("carrito", carrito);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("impuestos", impuestos);
        model.addAttribute("total", total);
        model.addAttribute("totalEnMoneda", totalEnMoneda);
        model.addAttribute("moneda", moneda);
        model.addAttribute("tasaCambio", tasaCambio);
        
        // IMPORTANTE: Cambia esto según tu estructura de carpetas
        return "checkout"; // Si está en templates/checkout.html
        // O return "checkout/checkout"; // Si está en templates/checkout/checkout.html
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
            System.out.println("DEBUG: Procesando pago SIMULADO - método: " + metodoPago);
            
            // 1. VALIDACIONES (igual que antes)
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
            
            // 2. SIMULAR PROCESO DE PAGO
            System.out.println("=== SIMULANDO PAGO ===");
            System.out.println("1. Conectando con pasarela de pago...");
            System.out.println("2. Validando tarjeta...");
            System.out.println("3. Autorizando monto...");
            
            // Simular delay de red
            Thread.sleep(1000);
            
            // Generar número de transacción simulado
            String numeroTransaccion = "TRX-" + System.currentTimeMillis();
            System.out.println("4. Transacción aprobada: " + numeroTransaccion);
            
            // 3. CALCULAR TOTALES
            BigDecimal subtotal = calcularSubtotal(carrito);
            BigDecimal envio = "delivery".equals(tipoEntrega) ? COSTO_ENVIO : BigDecimal.ZERO;
            BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(envio).add(impuestos);
            
            // Conversión si es USD
            BigDecimal totalFinal = total;
            if ("USD".equals(moneda)) {
                totalFinal = currencyService.convertToUSD(total);
            }
            
            // 4. GUARDAR DATOS CON NÚMERO DE TRANSACCIÓN
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
            datosCompra.setNumeroPedido("PED-" + System.currentTimeMillis());
            datosCompra.setFecha(LocalDateTime.now());
            
            // Agregar datos de transacción simulada
            redirectAttributes.addFlashAttribute("transaccionId", numeroTransaccion);
            redirectAttributes.addFlashAttribute("pagoAprobado", true);
            redirectAttributes.addFlashAttribute("mensajePago", "Pago simulado exitoso");
            
            // 5. REDIRIGIR A CONFIRMACIÓN
            redirectAttributes.addFlashAttribute("carrito", carrito);
            redirectAttributes.addFlashAttribute("datosCompra", datosCompra);
            redirectAttributes.addFlashAttribute("subtotal", subtotal);
            redirectAttributes.addFlashAttribute("envio", envio);
            redirectAttributes.addFlashAttribute("impuestos", impuestos);
            redirectAttributes.addFlashAttribute("total", totalFinal);
            
            return "redirect:/checkout/confirmacion";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error en pago simulado: " + e.getMessage());
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
        
        // Si llegamos aquí con flash attributes, ya están en el modelo
        return "checkout/confirmacion";
    }
    
    @PostMapping("/finalizar")
    public String finalizarCompra(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                                 @ModelAttribute("datosCompra") DatosCompra datosCompra,
                                 RedirectAttributes redirectAttributes) {
        
        try {
            // Simular guardado en BD
            System.out.println("Guardando compra: " + datosCompra.getNumeroPedido());
            
            // Limpiar carrito después de la compra
            carrito.clear();
            
            redirectAttributes.addFlashAttribute("success", 
                "¡Compra finalizada exitosamente! Número de pedido: " + datosCompra.getNumeroPedido());
            redirectAttributes.addFlashAttribute("numeroPedido", datosCompra.getNumeroPedido());
            
            return "redirect:/compras/comprobante?pedido=" + datosCompra.getNumeroPedido();
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error al finalizar la compra: " + e.getMessage());
            return "redirect:/checkout/confirmacion";
        }
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
        
        // Constructor vacío IMPORTANTE
        public DatosCompra() {}
        
        // Getters y setters
        public String getTipoEntrega() { return tipoEntrega; }
        public void setTipoEntrega(String tipoEntrega) { this.tipoEntrega = tipoEntrega; }
        
        public boolean isDelivery() {
            return tipoEntrega != null && "delivery".equalsIgnoreCase(tipoEntrega);
        }
        
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