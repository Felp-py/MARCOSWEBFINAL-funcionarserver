package com.example.demo.controller;

import com.example.demo.model.Cliente;
import com.example.demo.model.DniResponse;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.DniService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final DniService dniService;

    public RegistroController(UsuarioRepository usuarioRepository, 
                              ClienteRepository clienteRepository,
                              PasswordEncoder passwordEncoder,
                              DniService dniService) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.dniService = dniService;
    }

    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        return "registro";
    }

    @PostMapping("/procesar")
    public String registrarCliente(
            @RequestParam("dni") String dni,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {

        try {
            System.out.println("=== INICIANDO REGISTRO ===");
            System.out.println("DNI: " + dni);
            System.out.println("Nombre: " + nombre);
            System.out.println("Apellido: " + apellido);
            
            // 1. Generar nombre completo
            String nombreCompleto = nombre.trim() + " " + apellido.trim();
            
            // 2. Generar un correo automático basado en el DNI
            String correo = generarCorreoDesdeDNI(dni, nombre, apellido);
            correo = hacerCorreoUnico(correo);
            
            System.out.println("Nombre completo: " + nombreCompleto);
            System.out.println("Correo generado: " + correo);
            
            // 3. Validar que el correo no exista
            if (usuarioRepository.findByCorreo(correo).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El DNI ya está registrado");
                return "redirect:/registro";
            }

            // 4. Hacer el nombre único si ya existe
            String nombreFinal = hacerNombreUnico(nombreCompleto);
            
            // 5. Crear el usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombreFinal);
            usuario.setCorreo(correo);  // Usar setCorreo
            usuario.setContrasena(passwordEncoder.encode(password));  // CORRECCIÓN: setContrasena
            usuario.setIdRol(2); // 2 = CLIENTE
            
            System.out.println("Guardando usuario...");
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            System.out.println("Usuario guardado con ID: " + usuarioGuardado.getIdUsuario());

            // 6. Crear el cliente asociado
            Cliente cliente = new Cliente();
            cliente.setIdUsuario(usuarioGuardado.getIdUsuario());
            cliente.setNombreCliente(nombreFinal);
            cliente.setDireccion("Por definir");
            cliente.setTelefono("No especificado");
            cliente.setCorreoCliente(correo);
            
            System.out.println("Guardando cliente...");
            clienteRepository.save(cliente);
            System.out.println("Cliente guardado con ID: " + cliente.getIdCliente());

            redirectAttributes.addFlashAttribute("success", 
                "¡Registro exitoso! Puedes iniciar sesión con tu nombre: " + nombreFinal);
            return "redirect:/login";

        } catch (Exception e) {
            System.err.println("=== ERROR EN REGISTRO ===");
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error al registrar: " + e.getMessage());
            return "redirect:/registro";
        }
    }

    private String generarCorreoDesdeDNI(String dni, String nombre, String apellido) {
        try {
            // Tomar primer nombre y primer apellido
            String primerNombre = nombre.trim().split(" ")[0].toLowerCase();
            String primerApellido = apellido.trim().split(" ")[0].toLowerCase();
            
            // Limpiar caracteres especiales
            primerNombre = primerNombre.replaceAll("[^a-z]", "");
            primerApellido = primerApellido.replaceAll("[^a-z]", "");
            
            // Si está vacío, usar valores por defecto
            if (primerNombre.isEmpty()) primerNombre = "usuario";
            if (primerApellido.isEmpty()) primerApellido = "apellido";
            
            // Crear correo: juan.perez.dni@bibliosfera.com
            return primerNombre + "." + primerApellido + "." + dni + "@bibliosfera.com";
            
        } catch (Exception e) {
            // Si hay error, generar correo simple
            return "usuario." + dni + "@bibliosfera.com";
        }
    }

    private String hacerCorreoUnico(String correoBase) {
        String correo = correoBase;
        int contador = 1;
        
        while (usuarioRepository.findByCorreo(correo).isPresent()) {
            // Cambiar el formato: usuario.perez.dni2@bibliosfera.com
            correo = correoBase.replace("@bibliosfera.com", contador + "@bibliosfera.com");
            contador++;
            
            // Por seguridad, limitar intentos
            if (contador > 100) {
                throw new RuntimeException("No se pudo generar un correo único");
            }
        }
        
        return correo;
    }

    private String hacerNombreUnico(String nombreBase) {
        String nombre = nombreBase;
        int contador = 1;
        
        while (usuarioRepository.findByNombre(nombre).isPresent()) {
            nombre = nombreBase + " " + contador;
            contador++;
            
            // Por seguridad, limitar intentos
            if (contador > 100) {
                throw new RuntimeException("No se pudo generar un nombre único");
            }
        }
        
        return nombre;
    }
    
    // Endpoint para consultar DNI
    @GetMapping("/api/reniec/dni/{dni}")
    @ResponseBody
    public Map<String, Object> consultarDNI(@PathVariable String dni) {
        System.out.println("=== CONSULTANDO DNI ===");
        System.out.println("DNI: " + dni);
        
        Map<String, Object> response = new HashMap<>();
        
        // Validar formato del DNI
        if (dni == null || !dni.matches("\\d{8}")) {
            response.put("success", false);
            response.put("message", "DNI inválido. Debe tener 8 dígitos numéricos.");
            return response;
        }
        
        try {
            // Usar el DniService
            if (dniService != null) {
                System.out.println("Usando DniService...");
                DniResponse dniResponse = dniService.consultarDni(dni);
                
                if (dniResponse != null && dniResponse.getNombres() != null && 
                    !dniResponse.getNombres().isEmpty()) {
                    
                    response.put("success", true);
                    response.put("nombres", dniResponse.getNombres());
                    response.put("apellidoPaterno", dniResponse.getApellidoPaterno());
                    response.put("apellidoMaterno", dniResponse.getApellidoMaterno());
                    response.put("message", "Consulta exitosa");
                    
                } else {
                    response.put("success", false);
                    response.put("message", "No se encontraron datos para el DNI");
                }
            } 
            // Datos de prueba
            else {
                System.out.println("DniService no disponible, usando datos de prueba");
                
                switch(dni) {
                    case "46688592":
                        response.put("success", true);
                        response.put("nombres", "JUAN CARLOS");
                        response.put("apellidoPaterno", "PEREZ");
                        response.put("apellidoMaterno", "GOMEZ");
                        response.put("message", "Consulta exitosa (modo prueba)");
                        break;
                    case "12345678":
                        response.put("success", true);
                        response.put("nombres", "MARIA LUISA");
                        response.put("apellidoPaterno", "RODRIGUEZ");
                        response.put("apellidoMaterno", "LOPEZ");
                        response.put("message", "Consulta exitosa (modo prueba)");
                        break;
                    default:
                        response.put("success", true);
                        response.put("nombres", "PRUEBA " + dni.substring(0, 3));
                        response.put("apellidoPaterno", "APELLIDO " + dni.substring(3, 6));
                        response.put("apellidoMaterno", "MATERNO");
                        response.put("message", "Consulta exitosa (modo prueba)");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error en consulta DNI: " + e.getMessage());
            e.printStackTrace();
            
            // En caso de error, devolver datos de prueba
            response.put("success", true);
            response.put("nombres", "PRUEBA POR ERROR");
            response.put("apellidoPaterno", "APELLIDO P");
            response.put("apellidoMaterno", "APELLIDO M");
            response.put("message", "Modo prueba por error: " + e.getMessage());
        }
        
        return response;
    }
}