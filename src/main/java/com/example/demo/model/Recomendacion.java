package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recomendacion")
public class Recomendacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recomendacion")
    private Integer idRecomendacion;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    private LocalDateTime fecha;

    public Recomendacion() {}

    public Recomendacion(Cliente cliente, String comentario) {
        this.cliente = cliente;
        this.comentario = comentario;
        this.fecha = LocalDateTime.now();
    }

    public Integer getIdRecomendacion() { return idRecomendacion; }
    public void setIdRecomendacion(Integer idRecomendacion) { this.idRecomendacion = idRecomendacion; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
