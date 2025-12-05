package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "venta")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_entrega", nullable = false)
    private TipoEntrega tipoEntrega;
    
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;
    
    public Venta() {
        this.fechaVenta = LocalDateTime.now();
    }
    
    public Venta(Cliente cliente, MetodoPago metodoPago, TipoEntrega tipoEntrega, 
                BigDecimal total, LocalDateTime fechaVenta) {
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.tipoEntrega = tipoEntrega;
        this.total = total;
        this.fechaVenta = fechaVenta != null ? fechaVenta : LocalDateTime.now();
    }
    
    public Integer getIdVenta() {
        return idVenta;
    }
    
    public void setIdVenta(Integer idVenta) {
        this.idVenta = idVenta;
    }
    
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getMetodoPagoNombre() {
        return metodoPago != null ? metodoPago.getNombre() : "";
    }
    
    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }
    
    public void setTipoEntrega(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
    }
    
    public String getTipoEntregaNombre() {
        return tipoEntrega != null ? tipoEntrega.getNombre() : "";
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
        public String getFechaVentaFormateada() {
        if (fechaVenta != null) {
            return fechaVenta.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "";
    }
    
    @Override
    public String toString() {
        return "Venta{" +
                "idVenta=" + idVenta +
                ", cliente=" + (cliente != null ? cliente.getNombreCliente() : "null") +
                ", total=" + total +
                ", fechaVenta=" + fechaVenta +
                '}';
    }
}