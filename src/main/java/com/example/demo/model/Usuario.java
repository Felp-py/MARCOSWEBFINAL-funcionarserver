package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "correo", nullable = false, unique = true, length = 100)
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "fecha_registro", insertable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "id_rol")
    private Integer idRol;

    // IMPORTANTE: Temporalmente comenta esta relación
    // @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    // private Cliente cliente;

    // Constructor vacío - NO inicialices fechaRegistro aquí
    public Usuario() {
        // Deja que la BD asigne el valor por defecto
    }

    // Constructor para registro
    public Usuario(String nombre, String correo, String contrasena, Integer idRol) {
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.idRol = idRol;
        // No asignes fechaRegistro, la BD lo hará
    }

    // Getters y Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }


    //public Cliente getCliente() { return cliente; }
    //public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    @Transient
    public String getPassword() {
        return this.contrasena;
    }
    
    @Transient
    public void setPassword(String password) {
        this.contrasena = password;
    }
    
    @Transient
    public String getEmail() {
        return this.correo;
    }
    
    @Transient
    public void setEmail(String email) {
        this.correo = email;
    }
}