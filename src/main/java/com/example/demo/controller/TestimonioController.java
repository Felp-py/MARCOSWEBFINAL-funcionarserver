package com.example.demo.controller;

import com.example.demo.model.Testimonio;
import com.example.demo.service.TestimonioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nosotros/testimonio")
public class TestimonioController {

    private final TestimonioService testimonioService;

    public TestimonioController(TestimonioService testimonioService) {
        this.testimonioService = testimonioService;
    }

@GetMapping
public String verTestimonios(Model model) {
    model.addAttribute("testimonios", testimonioService.findAll());
    model.addAttribute("promedio", testimonioService.promedioCalificacion());
    return "nosotros";
}

@PostMapping("/agregar")
public String agregarTestimonio(@RequestParam String cliente,
                                @RequestParam String comentario,
                                @RequestParam int calificacion) {
    Testimonio t = new Testimonio();
    t.setCliente(cliente);
    t.setComentario(comentario);
    t.setCalificacion(calificacion);
    testimonioService.save(t);
    return "redirect:/nosotros/testimonio";
}

@GetMapping("/eliminar/{id}")
public String eliminarTestimonio(@PathVariable int id) {
    testimonioService.deleteById(id);
    return "redirect:/nosotros/testimonio";
}

@GetMapping("/buscar")
public String buscar(@RequestParam String cliente, Model model) {
    model.addAttribute("testimonios", testimonioService.findByClienteContaining(cliente));
    model.addAttribute("promedio", testimonioService.promedioCalificacion());
    return "nosotros";
}

}
