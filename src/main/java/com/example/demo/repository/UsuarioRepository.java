package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Buscar por correo (columna 'correo' en BD)
    Optional<Usuario> findByCorreo(String correo);
    
    // Buscar por nombre (columna 'nombre' en BD)
    Optional<Usuario> findByNombre(String nombre);
    
    // Buscar por nombre O correo
    @Query("SELECT u FROM Usuario u WHERE u.nombre = :busqueda OR u.correo = :busqueda")
    Optional<Usuario> findByNombreOrCorreo(@Param("busqueda") String busqueda);
    
    // Verificar si existe
    boolean existsByNombre(String nombre);
    boolean existsByCorreo(String correo);
}