package com.example.demo.controller;

import com.example.demo.model.DniResponse;
import com.example.demo.service.DniService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dni")
public class DniController {
    
    @Autowired
    private DniService dniService;
    
    @GetMapping("/consultar")
    public String mostrarFormulario(Model model) {
        // Inicializar valores por defecto
        if (!model.containsAttribute("dni")) {
            model.addAttribute("dni", "");
        }
        return "consultar-dni"; // Nombre del archivo HTML
    }
    
    @PostMapping("/consultar")
    public String consultarDni(
            @RequestParam("dni") String dni,
            RedirectAttributes redirectAttributes) {
        
        dni = dni.trim();
        
        // Validación simple
        if (dni.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Por favor ingrese un DNI");
            redirectAttributes.addFlashAttribute("dni", dni);
            return "redirect:/dni/consultar";
        }
        
        if (!dni.matches("\\d{8}")) {
            redirectAttributes.addFlashAttribute("error", "El DNI debe tener 8 dígitos numéricos");
            redirectAttributes.addFlashAttribute("dni", dni);
            return "redirect:/dni/consultar";
        }
        
        try {
            DniResponse response = dniService.consultarDni(dni);
            
            if (response != null && response.getNombres() != null) {
                redirectAttributes.addFlashAttribute("success", "Consulta exitosa");
                redirectAttributes.addFlashAttribute("persona", response);
                redirectAttributes.addFlashAttribute("dni", dni);
            } else {
                redirectAttributes.addFlashAttribute("error", "No se encontró información para el DNI: " + dni);
                redirectAttributes.addFlashAttribute("dni", dni);
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al consultar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("dni", dni);
        }
        
        return "redirect:/dni/consultar";
    }
    
    // Endpoint para API (AJAX/JavaScript)
    @GetMapping("/api")
    @ResponseBody
    public DniResponse consultarDniApi(@RequestParam String numero) {
        return dniService.consultarDni(numero);
    }
}