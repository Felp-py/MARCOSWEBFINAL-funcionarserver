package com.example.demo.service;

import com.example.demo.model.OpenLibraryBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenLibraryService {

    @Autowired
    private RestTemplate restTemplate;

    public OpenLibraryBook buscarPorIsbn(String isbn) {
        String url = "https://openlibrary.org/isbn/" + isbn + ".json";
        return restTemplate.getForObject(url, OpenLibraryBook.class);
    }

    public String obtenerPortada(String isbn) {
        return "https://covers.openlibrary.org/b/isbn/" + isbn + "-L.jpg";
    }
}
