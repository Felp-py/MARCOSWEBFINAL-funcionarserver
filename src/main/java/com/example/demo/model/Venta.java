package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "venta")
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private Integer idVenta;
    
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    @ManyToOne
    @JoinColumn(name = "id_metodo_pago")  // Asegúrate que la columna existe en la BD
    private MetodoPago metodoPago;
    
    @ManyToOne
    @JoinColumn(name = "id_tipo_entrega")  // Asegúrate que la columna existe en la BD
    private TipoEntrega tipoEntrega;
    
    @Column(name = "total", nullable = false)
    private BigDecimal total;
    
    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;
    
    // Constructor
    public Venta() {
        this.fechaVenta = LocalDateTime.now();
    }
    
    // Getters y Setters
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
    
    // ESTOS SON LOS MÉTODOS QUE TE FALTAN
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }
    
    public void setTipoEntrega(TipoEntrega tipoEntrega) {
        this.tipoEntrega = tipoEntrega;
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
}