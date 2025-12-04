package com.example.demo.controller;

import com.example.demo.model.Editorial;
import com.example.demo.service.EditorialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/editoriales")
public class EditorialController {

    private final EditorialService editorialService;

    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("editoriales", editorialService.findAll());
        model.addAttribute("nuevaEditorial", new Editorial());
        return "editoriales";
    }

    @PostMapping("/agregar")
    public String agregar(@ModelAttribute Editorial editorial) {
        editorialService.save(editorial);
        return "redirect:/editoriales";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        editorialService.deleteById(id);
        return "redirect:/editoriales";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Editorial editorial = editorialService.findById(id).orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada"));
        model.addAttribute("editorial", editorial);
        return "editarEditorial";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id, @ModelAttribute Editorial editorial) {
        editorial.setIdEditorial(id);
        editorialService.save(editorial);
        return "redirect:/editoriales";
    }
}
