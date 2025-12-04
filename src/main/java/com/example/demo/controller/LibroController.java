package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import com.example.demo.repository.LibroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/libros")
public class LibroController {

    private final LibroRepository libroRepository;
    private final AutorService autorService;
    private final CategoriaService categoriaService;
    private final EditorialService editorialService;

    public LibroController(LibroRepository libroRepository,
                          AutorService autorService,
                          CategoriaService categoriaService,
                          EditorialService editorialService) {
        this.libroRepository = libroRepository;
        this.autorService = autorService;
        this.categoriaService = categoriaService;
        this.editorialService = editorialService;
    }

    // Mostrar formulario para nuevo libro (solo admin)
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevoLibro() {
        return "nuevo-libro";
    }

    // Guardar nuevo libro
    @PostMapping("/guardar")
    @Transactional
    public String guardarLibro(
            @RequestParam String titulo,
            @RequestParam String autor,
            @RequestParam String categoria,
            @RequestParam String editorial,
            @RequestParam BigDecimal precio,
            @RequestParam Integer stock) {
        
        try {
            // Buscar o crear autor
            Autor autorObj = autorService.findByNombre(autor)
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(autor);
                        return autorService.save(nuevoAutor);
                    });
            
            // Buscar o crear categoría
            Categoria categoriaObj = categoriaService.findByNombre(categoria)
                    .orElseGet(() -> {
                        Categoria nuevaCategoria = new Categoria();
                        nuevaCategoria.setNombre(categoria);
                        return categoriaService.save(nuevaCategoria);
                    });
            
            // Buscar o crear editorial
            Editorial editorialObj = editorialService.findByNombre(editorial)
                    .orElseGet(() -> {
                        Editorial nuevaEditorial = new Editorial();
                        nuevaEditorial.setNombre(editorial);
                        return editorialService.save(nuevaEditorial);
                    });
            
            // Crear y guardar libro
            Libro libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autorObj);
            libro.setCategoria(categoriaObj);
            libro.setEditorial(editorialObj);
            libro.setPrecio(precio);
            libro.setStock(stock != null ? stock : 0);
            
            libroRepository.save(libro);
            
            return "redirect:/admin/lista-libros?success=true";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/libros/nuevo?error=true";
        }
    }

    // Mostrar formulario para editar libro
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarLibro(@PathVariable Long id, Model model) { // Cambiado a Long
        Optional<Libro> libroOpt = libroRepository.findById(id);
        
        if (libroOpt.isPresent()) {
            model.addAttribute("libro", libroOpt.get());
            return "editar-libro";
        }
        
        return "redirect:/admin/lista-libros?error=notfound";
    }

    // Actualizar libro
    @PostMapping("/actualizar/{id}")
    @Transactional
    public String actualizarLibro(
            @PathVariable Long id, // Cambiado a Long
            @RequestParam String titulo,
            @RequestParam String autor,
            @RequestParam String categoria,
            @RequestParam String editorial,
            @RequestParam BigDecimal precio,
            @RequestParam Integer stock) {
        
        Optional<Libro> libroOpt = libroRepository.findById(id);
        
        if (libroOpt.isPresent()) {
            Libro libro = libroOpt.get();
            
            // Actualizar campos básicos
            libro.setTitulo(titulo);
            libro.setPrecio(precio);
            libro.setStock(stock != null ? stock : 0);
            
            // Actualizar o crear autor
            Autor autorObj = autorService.findByNombre(autor)
                    .orElseGet(() -> {
                        Autor nuevoAutor = new Autor();
                        nuevoAutor.setNombre(autor);
                        return autorService.save(nuevoAutor);
                    });
            libro.setAutor(autorObj);
            
            // Actualizar o crear categoría
            Categoria categoriaObj = categoriaService.findByNombre(categoria)
                    .orElseGet(() -> {
                        Categoria nuevaCategoria = new Categoria();
                        nuevaCategoria.setNombre(categoria);
                        return categoriaService.save(nuevaCategoria);
                    });
            libro.setCategoria(categoriaObj);
            
            // Actualizar o crear editorial
            Editorial editorialObj = editorialService.findByNombre(editorial)
                    .orElseGet(() -> {
                        Editorial nuevaEditorial = new Editorial();
                        nuevaEditorial.setNombre(editorial);
                        return editorialService.save(nuevaEditorial);
                    });
            libro.setEditorial(editorialObj);
            
            libroRepository.save(libro);
            
            return "redirect:/admin/lista-libros?success=updated";
        }
        
        return "redirect:/admin/lista-libros?error=notfound";
    }

    // Eliminar libro
    @GetMapping("/eliminar/{id}")
    public String eliminarLibro(@PathVariable Long id) { // Cambiado a Long
        libroRepository.deleteById(id);
        return "redirect:/admin/lista-libros?success=deleted";
    }

    // Lista simple de libros (alternativa)
    @GetMapping("/lista")
    public String listaLibros(Model model) {
        List<Libro> libros = libroRepository.findAll();
        model.addAttribute("libros", libros);
        return "lista-simple-libros";
    }
}