package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findByCorreo(String correo);
    
    Optional<Usuario> findByNombre(String nombre);
    
    @Query("SELECT u FROM Usuario u WHERE u.nombre = :busqueda OR u.correo = :busqueda")
    Optional<Usuario> findByNombreOrCorreo(@Param("busqueda") String busqueda);
    
    boolean existsByNombre(String nombre);
    boolean existsByCorreo(String correo);
}