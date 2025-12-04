package com.example.demo.controller;

import com.example.demo.model.Autor;
import com.example.demo.service.AutorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("autores", autorService.findAll());
        model.addAttribute("nuevoAutor", new Autor());
        return "autores"; // nombre del template Thymeleaf
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute Autor autor) {
        autorService.save(autor);
        return "redirect:/autores";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        autorService.deleteById(id);
        return "redirect:/autores";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Autor autor = autorService.findById(id).orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
        model.addAttribute("autor", autor);
        return "editarAutor"; // plantilla para editar
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Autor autor) {
        autor.setIdAutor(id);
        autorService.save(autor);
        return "redirect:/autores";
    }
}
