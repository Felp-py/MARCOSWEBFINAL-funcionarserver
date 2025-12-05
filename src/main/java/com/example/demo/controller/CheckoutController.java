package com.example.demo.controller;

import com.example.demo.model.ItemCarrito;
import com.example.demo.service.CurrencyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
@RequestMapping("/checkout")
@SessionAttributes("carrito")
public class CheckoutController {
    
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.18");
    private final CurrencyService currencyService;
    
    public CheckoutController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }
    
    @GetMapping
    public String showCheckout(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                              @RequestParam(value = "moneda", defaultValue = "PEN") String moneda,
                              Model model) {
        
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/carrito";
        }
        
        // Calcular totales
        BigDecimal subtotal = carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal total = subtotal.add(impuestos);
        
        // Conversión de moneda si es necesario
        BigDecimal totalEnMoneda = total;
        BigDecimal tasaCambio = BigDecimal.ONE;
        
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
        
        return "checkout";
    }
    
    @PostMapping("/procesar")
    public String procesarCheckout(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                                  @RequestParam String tipoEntrega,
                                  @RequestParam String moneda,
                                  Model model) {
        
        // Aquí procesarías el checkout y redirigirías a pago
        // Por ahora, solo redirigimos
        return "redirect:/pago?tipoEntrega=" + tipoEntrega + "&moneda=" + moneda;
    }
}