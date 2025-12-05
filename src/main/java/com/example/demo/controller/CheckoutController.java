package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/checkout")
@SessionAttributes({"carrito", "datosCompra"})
public class CheckoutController {
    
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.18");
    private static final BigDecimal TASA_CAMBIO_FIJA = new BigDecimal("3.80");
    
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final TipoEntregaRepository tipoEntregaRepository;
    
    public CheckoutController(VentaRepository ventaRepository,
                            DetalleVentaRepository detalleVentaRepository,
                            LibroRepository libroRepository,
                            UsuarioRepository usuarioRepository,
                            ClienteRepository clienteRepository,
                            MetodoPagoRepository metodoPagoRepository,
                            TipoEntregaRepository tipoEntregaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.libroRepository = libroRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.tipoEntregaRepository = tipoEntregaRepository;
    }
    
    @ModelAttribute("metodosPago")
    public List<MetodoPago> cargarMetodosPago() {
        return metodoPagoRepository.findAll();
    }
    
    @ModelAttribute("tiposEntrega")
    public List<TipoEntrega> cargarTiposEntrega() {
        return tipoEntregaRepository.findAll();
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

        BigDecimal subtotal = calcularSubtotal(carrito);
        BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(impuestos);
        BigDecimal tasaCambio = TASA_CAMBIO_FIJA;
        BigDecimal totalEnMoneda = total;
        
        if ("USD".equals(moneda)) {
            totalEnMoneda = total.divide(tasaCambio, 2, RoundingMode.HALF_UP);
        }
        
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
    @Transactional
    public String procesarPago(@ModelAttribute("carrito") List<ItemCarrito> carrito,
                            @ModelAttribute("datosCompra") DatosCompra datosCompra,
                            @RequestParam Integer idMetodoPago,
                            @RequestParam Integer idTipoEntrega,
                            @RequestParam(defaultValue = "PEN") String moneda,
                            @RequestParam(required = false) String direccion,
                            @RequestParam(required = false) String distrito,
                            @RequestParam(required = false) String ciudad,
                            @RequestParam(required = false) String referencia,
                            RedirectAttributes redirectAttributes) {
        
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== INICIANDO PROCESO DE PAGO ===");
            System.out.println("=".repeat(50));
            System.out.println("Carrito tamaño: " + carrito.size());
            
            TipoEntrega tipoEntrega = tipoEntregaRepository.findById(idTipoEntrega)
                    .orElseThrow(() -> new RuntimeException("Tipo de entrega no válido"));
            
            if ("delivery".equals(tipoEntrega.getNombre().toLowerCase())) {
                if (direccion == null || direccion.trim().isEmpty() ||
                    distrito == null || distrito.trim().isEmpty()) {
                    System.out.println("Error: Dirección incompleta para delivery");
                    redirectAttributes.addFlashAttribute("error", 
                        "Por favor completa la dirección para delivery");
                    return "redirect:/checkout";
                }
            }

            MetodoPago metodoPago = metodoPagoRepository.findById(idMetodoPago)
                    .orElseThrow(() -> new RuntimeException("Método de pago no válido"));
            
            BigDecimal subtotal = calcularSubtotal(carrito);
            BigDecimal envio = tipoEntrega.getCosto() != null ? tipoEntrega.getCosto() : BigDecimal.ZERO;
            BigDecimal impuestos = subtotal.multiply(TASA_IMPUESTOS)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(envio).add(impuestos);
            
            System.out.println("\n" + "-".repeat(30));
            System.out.println("CÁLCULOS DE TOTALES");
            System.out.println("-".repeat(30));
            System.out.println("Subtotal: S/ " + subtotal);
            System.out.println("Envío: S/ " + envio);
            System.out.println("Impuestos (18%): S/ " + impuestos);
            System.out.println("TOTAL FINAL: S/ " + total);
            System.out.println("Moneda: " + moneda);
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            System.out.println("\n👤 Usuario autenticado: " + username);
            
            Usuario usuario = usuarioRepository.findByCorreo(username)
                    .orElseThrow(() -> {
                        System.err.println("Usuario no encontrado: " + username);
                        return new RuntimeException("Usuario no encontrado: " + username);
                    });
            System.out.println("Usuario encontrado - ID: " + usuario.getIdUsuario() + 
                            ", Nombre: " + usuario.getNombre());
            

            Optional<Cliente> clienteOpt = clienteRepository.findByIdUsuario(usuario.getIdUsuario());
            if (clienteOpt.isEmpty()) {
                System.out.println("Cliente no encontrado, creando nuevo...");

                Cliente nuevoCliente = new Cliente();
                nuevoCliente.setIdUsuario(usuario.getIdUsuario());
                nuevoCliente.setNombreCliente(usuario.getNombre());
                nuevoCliente.setCorreoCliente(usuario.getCorreo());
                nuevoCliente.setDireccion(direccion);
                nuevoCliente.setTelefono("999999999");
                nuevoCliente = clienteRepository.save(nuevoCliente);
                clienteOpt = Optional.of(nuevoCliente);
                System.out.println("Nuevo cliente creado - ID: " + nuevoCliente.getIdCliente());
            }
            
            Cliente cliente = clienteOpt.get();
            System.out.println("Cliente asociado - ID: " + cliente.getIdCliente() +
                            ", Nombre: " + cliente.getNombreCliente());
            
            Venta venta = new Venta();
            venta.setCliente(cliente);
            venta.setMetodoPago(metodoPago);
            venta.setTipoEntrega(tipoEntrega);
            venta.setTotal(total);
            venta.setFechaVenta(LocalDateTime.now());
            
            System.out.println("\n" + "-".repeat(30));
            System.out.println("GUARDANDO VENTA EN BD");
            System.out.println("-".repeat(30));
            System.out.println("Datos venta:");
            System.out.println("- Cliente ID: " + cliente.getIdCliente());
            System.out.println("- Método pago: " + metodoPago.getNombre());
            System.out.println("- Tipo entrega: " + tipoEntrega.getNombre());
            System.out.println("- Total: S/ " + total);
            
            long ventasAntes = ventaRepository.count();
            System.out.println("Ventas en BD antes: " + ventasAntes);
            
            venta = ventaRepository.save(venta);
            System.out.println("Venta guardada exitosamente!");
            System.out.println("Venta ID generado: " + venta.getIdVenta());

            long ventasDespues = ventaRepository.count();
            System.out.println("Ventas en BD después: " + ventasDespues);
            System.out.println("Diferencia: " + (ventasDespues - ventasAntes));
            
            String numeroPedido = "PED-" + venta.getIdVenta();
            System.out.println("Número de pedido generado: " + numeroPedido);

            List<DetalleVenta> detalles = new ArrayList<>();
            System.out.println("\n" + "-".repeat(40));
            System.out.println("PROCESANDO " + carrito.size() + " ITEMS DEL CARRITO");
            System.out.println("-".repeat(40));
            
            for (int i = 0; i < carrito.size(); i++) {
                ItemCarrito item = carrito.get(i);
                System.out.println("\n--- ÍTEM " + (i + 1) + " DE " + carrito.size() + " ---");
                System.out.println("Libro ID: " + item.getIdLibro());
                System.out.println("Título: " + item.getTitulo());
                System.out.println("Cantidad: " + item.getCantidad());
                System.out.println("Precio unitario: S/ " + item.getPrecio());
                System.out.println("Subtotal: S/ " + item.getSubtotal());

                Optional<Libro> libroOpt = libroRepository.findById(item.getIdLibro());
                if (libroOpt.isEmpty()) {
                    System.err.println("ERROR: Libro no encontrado con ID: " + item.getIdLibro());
                    throw new RuntimeException("Libro no encontrado: " + item.getTitulo());
                }
                
                Libro libro = libroOpt.get();
                System.out.println("Libro encontrado en BD:");
                System.out.println("   - Título BD: " + libro.getTitulo());
                System.out.println("   - Stock actual: " + libro.getStock());
                System.out.println("   - Precio BD: S/ " + libro.getPrecio());
                

                if (libro.getStock() < item.getCantidad()) {
                    System.err.println("ERROR: Stock insuficiente!");
                    System.err.println("   - Stock disponible: " + libro.getStock());
                    System.err.println("   - Cantidad solicitada: " + item.getCantidad());
                    throw new RuntimeException("Stock insuficiente para: " + libro.getTitulo() + 
                                            ". Stock disponible: " + libro.getStock() + 
                                            ", solicitado: " + item.getCantidad());
                }
                
                DetalleVenta detalle = new DetalleVenta();
                detalle.setVenta(venta);
                detalle.setLibro(libro);
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getPrecio());
                detalle.setSubtotal(item.getSubtotal());
                
                detalles.add(detalle);
                System.out.println("Detalle creado:");
                System.out.println("   - Cantidad: " + item.getCantidad());
                System.out.println("   - Precio: S/ " + item.getPrecio());
                System.out.println("   - Subtotal: S/ " + item.getSubtotal());
                

                int stockAnterior = libro.getStock();
                int nuevoStock = stockAnterior - item.getCantidad();
                System.out.println("Actualizando stock:");
                System.out.println("   - Stock anterior: " + stockAnterior);
                System.out.println("   - Nuevo stock: " + nuevoStock);
                
                libro.setStock(nuevoStock);
                Libro libroActualizado = libroRepository.save(libro);
                System.out.println("Stock actualizado exitosamente!");
                System.out.println("Stock verificado: " + libroActualizado.getStock());
            }
            
            System.out.println("\n" + "-".repeat(30));
            System.out.println("GUARDANDO " + detalles.size() + " DETALLES EN BD");
            System.out.println("-".repeat(30));
            
            long detallesAntes = detalleVentaRepository.count();
            System.out.println("Detalles en BD antes: " + detallesAntes);
            
            detalleVentaRepository.saveAll(detalles);
            
            long detallesDespues = detalleVentaRepository.count();
            System.out.println("Detalles en BD después: " + detallesDespues);
            System.out.println("Detalles guardados exitosamente!");
            System.out.println("Diferencia: " + (detallesDespues - detallesAntes) + " detalles añadidos");
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("VERIFICACIÓN COMPLETA DE DATOS GUARDADOS");
            System.out.println("=".repeat(50));
            System.out.println("Venta ID: " + venta.getIdVenta());
            System.out.println("Número de pedido: " + numeroPedido);
            System.out.println("Total de la venta: S/ " + total);
            System.out.println("Fecha de venta: " + venta.getFechaVenta());
            System.out.println("Método de pago: " + venta.getMetodoPago().getNombre());
            System.out.println("Tipo de entrega: " + venta.getTipoEntrega().getNombre());
            
            System.out.println("\n" + "-".repeat(30));
            System.out.println("DETALLES DE LA VENTA");
            System.out.println("-".repeat(30));
            for (int i = 0; i < detalles.size(); i++) {
                DetalleVenta detalle = detalles.get(i);
                System.out.println("Detalle " + (i + 1) + ":");
                System.out.println("   - Libro: " + detalle.getLibro().getTitulo());
                System.out.println("   - Cantidad: " + detalle.getCantidad());
                System.out.println("   - Precio unitario: S/ " + detalle.getPrecioUnitario());
                System.out.println("   - Subtotal: S/ " + detalle.getSubtotal());
                System.out.println("   - Stock restante: " + detalle.getLibro().getStock());
            }
            
            datosCompra.setTipoEntrega(tipoEntrega.getNombre());
            datosCompra.setMetodoPago(metodoPago.getNombre());
            datosCompra.setDireccion(direccion);
            datosCompra.setDistrito(distrito);
            datosCompra.setCiudad(ciudad != null ? ciudad : "Lima");
            datosCompra.setReferencia(referencia);
            datosCompra.setMoneda(moneda);
            datosCompra.setSubtotal(subtotal);
            datosCompra.setEnvio(envio);
            datosCompra.setImpuestos(impuestos);
            datosCompra.setTotal(total);
            datosCompra.setNumeroPedido(numeroPedido);
            datosCompra.setFecha(LocalDateTime.now());
            
            System.out.println("\n" + "-".repeat(30));
            System.out.println("LIMPIANDO CARRITO");
            System.out.println("-".repeat(30));
            int itemsEliminados = carrito.size();
            carrito.clear();
            System.out.println("Carrito limpiado exitosamente!");
            System.out.println("" + itemsEliminados + " items eliminados del carrito.");
            
            redirectAttributes.addFlashAttribute("pagoAprobado", true);
            redirectAttributes.addFlashAttribute("numeroPedido", numeroPedido);
            redirectAttributes.addFlashAttribute("mensajeExito", 
                "¡Pago procesado exitosamente! El stock ha sido actualizado y la venta registrada.");
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("PROCESO DE PAGO COMPLETADO EXITOSAMENTE");
            System.out.println("=".repeat(50));
            System.out.println("Redirigiendo a confirmación...");
            
            return "redirect:/checkout/confirmacion";
            
        } catch (Exception e) {
            System.err.println("\n" + "=".repeat(50));
            System.err.println("ERROR EN PROCESO DE PAGO");
            System.err.println("=".repeat(50));
            System.err.println("Error tipo: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "Ninguna"));
            System.err.println("Stack trace:");
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
        
        if (datosCompra.getNumeroPedido() == null) {
            return "redirect:/checkout";
        }
        
        return "checkout/confirmacion";
    }
    
    @GetMapping("/estadisticas")
    public String verEstadisticas() {
        return "redirect:/compras/estadisticas";
    }
    
    private BigDecimal calcularSubtotal(List<ItemCarrito> carrito) {
        if (carrito == null) return BigDecimal.ZERO;
        
        return carrito.stream()
                .map(ItemCarrito::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @GetMapping("/verificarDatos")
    @ResponseBody
    public String verificarDatos() {
        StringBuilder resultado = new StringBuilder();
        resultado.append("<pre>"); 
        resultado.append("=== VERIFICACIÓN DE DATOS EN TIEMPO REAL ===\n\n");
        
        try {
            long totalVentas = ventaRepository.count();
            resultado.append("Total ventas en BD: ").append(totalVentas).append("\n");
            
            BigDecimal totalIngresos = ventaRepository.sumTotalVentas();
            resultado.append("Total ingresos en BD: S/ ")
                    .append(totalIngresos != null ? totalIngresos.setScale(2, RoundingMode.HALF_UP) : "0.00")
                    .append("\n");
            
            Long totalDetalles = detalleVentaRepository.sumTotalLibrosVendidos();
            resultado.append("Total libros vendidos en BD: ").append(totalDetalles != null ? totalDetalles : 0).append("\n");
            
            resultado.append("\n=== ÚLTIMAS 5 VENTAS ===\n");
            try {
                List<Venta> todasVentas = ventaRepository.findAll();
                todasVentas.sort((v1, v2) -> v2.getIdVenta().compareTo(v1.getIdVenta()));
                
                List<Venta> ultimasVentas = todasVentas.size() > 5 ? 
                    todasVentas.subList(0, 5) : todasVentas;
                
                if (ultimasVentas.isEmpty()) {
                    resultado.append("No hay ventas registradas\n");
                } else {
                    for (Venta v : ultimasVentas) {
                        resultado.append("ID: ").append(v.getIdVenta())
                                .append(" | Total: S/ ").append(v.getTotal())
                                .append(" | Fecha: ").append(v.getFechaVenta())
                                .append(" | Método: ").append(v.getMetodoPago() != null ? v.getMetodoPago().getNombre() : "N/A")
                                .append(" | Entrega: ").append(v.getTipoEntrega() != null ? v.getTipoEntrega().getNombre() : "N/A")
                                .append(" | Cliente: ").append(v.getCliente() != null ? v.getCliente().getNombreCliente() : "N/A")
                                .append("\n");
                    }
                }
            } catch (Exception e) {
                resultado.append("Error obteniendo ventas: ").append(e.getMessage()).append("\n");
            }
            
            resultado.append("\n=== STOCK DE LIBROS (primeros 10) ===\n");
            try {
                List<Libro> libros = libroRepository.findAll();
                List<Libro> primerosLibros = libros.size() > 10 ? 
                    libros.subList(0, 10) : libros;
                
                for (Libro libro : primerosLibros) {
                    resultado.append("ID: ").append(libro.getIdLibro())
                            .append(" | Título: ").append(libro.getTitulo().length() > 20 ? 
                                libro.getTitulo().substring(0, 20) + "..." : libro.getTitulo())
                            .append(" | Stock: ").append(libro.getStock())
                            .append(" | Precio: S/ ").append(libro.getPrecio())
                            .append("\n");
                }
            } catch (Exception e) {
                resultado.append("Error obteniendo libros: ").append(e.getMessage()).append("\n");
            }
            
            resultado.append("\n=== ESTADÍSTICAS POR MES ===\n");
            try {
                List<Object[]> ventasPorMes = ventaRepository.findVentasPorMes();
                if (ventasPorMes.isEmpty()) {
                    resultado.append("No hay datos de ventas por mes\n");
                } else {
                    for (Object[] obj : ventasPorMes) {
                        resultado.append("Mes ").append(obj[0])
                                .append(": ").append(obj[1]).append(" ventas\n");
                    }
                }
            } catch (Exception e) {
                resultado.append("Error obteniendo ventas por mes: ").append(e.getMessage()).append("\n");
            }
            
            resultado.append("\n=== LIBROS MÁS VENDIDOS ===\n");
            try {
                List<Object[]> librosMasVendidos = detalleVentaRepository.findLibrosMasVendidos();
                if (librosMasVendidos.isEmpty()) {
                    resultado.append("No hay datos de libros vendidos\n");
                } else {
                    for (int i = 0; i < Math.min(5, librosMasVendidos.size()); i++) {
                        Object[] obj = librosMasVendidos.get(i);
                        resultado.append(" ").append(obj[0] != null ? ((String) obj[0]).substring(0, Math.min(30, ((String) obj[0]).length())) : "Sin título")
                                .append(": ").append(obj[1]).append(" unidades\n");
                    }
                }
            } catch (Exception e) {
                resultado.append("Error obteniendo libros más vendidos: ").append(e.getMessage()).append("\n");
            }
            
        } catch (Exception e) {
            resultado.append("ERROR GENERAL: ").append(e.getMessage()).append("\n");
        }
        
        resultado.append("</pre>");
        return resultado.toString();
    }

    @GetMapping("/estadoSistema")
    @ResponseBody
    public String estadoSistema() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><title>Estado del Sistema</title>");
        sb.append("<style>");
        sb.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        sb.append(".success { color: green; }");
        sb.append(".error { color: red; }");
        sb.append(".warning { color: orange; }");
        sb.append("pre { background: #f4f4f4; padding: 10px; border-radius: 5px; }");
        sb.append("</style></head><body>");
        sb.append("<h2>Estado del Sistema de Ventas</h2>");
        
        try {
            long totalVentas = ventaRepository.count();
            sb.append("<p class='success'>Conexión a BD establecida</p>");
            sb.append("<p>Total de ventas: <strong>").append(totalVentas).append("</strong></p>");
            
            List<Venta> ventas = ventaRepository.findAll();
            if (!ventas.isEmpty()) {
                Venta ultimaVenta = ventas.get(0);
                for (Venta v : ventas) {
                    if (v.getIdVenta() > ultimaVenta.getIdVenta()) {
                        ultimaVenta = v;
                    }
                }
                
                sb.append("<p>Última venta registrada:</p>");
                sb.append("<pre>");
                sb.append("ID: ").append(ultimaVenta.getIdVenta()).append("\n");
                sb.append("Total: S/ ").append(ultimaVenta.getTotal()).append("\n");
                sb.append("Fecha: ").append(ultimaVenta.getFechaVenta()).append("\n");
                sb.append("Método: ").append(ultimaVenta.getMetodoPago() != null ? ultimaVenta.getMetodoPago().getNombre() : "N/A").append("\n");
                sb.append("Cliente ID: ").append(ultimaVenta.getCliente() != null ? ultimaVenta.getCliente().getIdCliente() : "N/A").append("\n");
                sb.append("</pre>");
            } else {
                sb.append("<p class='warning'>No hay ventas registradas</p>");
            }
            
            BigDecimal ingresosTotales = ventaRepository.sumTotalVentas();
            Long librosVendidos = detalleVentaRepository.sumTotalLibrosVendidos();
            
            sb.append("<p>Ingresos totales: <strong>S/ ")
            .append(ingresosTotales != null ? ingresosTotales.setScale(2) : "0.00")
            .append("</strong></p>");
            
            sb.append("<p>Libros vendidos: <strong>")
            .append(librosVendidos != null ? librosVendidos : 0)
            .append("</strong></p>");
            
            sb.append("<p> Muestra de stock de libros:</p>");
            sb.append("<pre>");
            List<Libro> libros = libroRepository.findAll();
            int count = 0;
            for (Libro libro : libros) {
                if (count++ >= 5) break;
                sb.append(String.format("%-30s | Stock: %3d | S/ %6.2f%n",
                    libro.getTitulo().length() > 30 ? libro.getTitulo().substring(0, 27) + "..." : libro.getTitulo(),
                    libro.getStock(),
                    libro.getPrecio()));
            }
            sb.append("</pre>");
            
            sb.append("<p class='success'>Sistema funcionando correctamente</p>");
            
        } catch (Exception e) {
            sb.append("<p class='error'>Error: ").append(e.getMessage()).append("</p>");
        }
        
        sb.append("</body></html>");
        return sb.toString();
    }
    
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