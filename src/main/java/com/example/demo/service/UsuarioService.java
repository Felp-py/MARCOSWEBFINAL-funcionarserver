package com.example.demo.service;

import com.example.demo.model.Usuario;
import java.util.List;

public interface UsuarioService {

    Usuario registrar(Usuario usuario);
    Usuario obtenerPorId(Integer id);
    Usuario obtenerPorCorreo(String correo);  
    Usuario obtenerPorNombre(String nombre);
    List<Usuario> listarTodos();
}