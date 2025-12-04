package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class TestController {

    @GetMapping("/test-dni")
    public String testDni() {
        String token = "eyJ0eXAiOLjKV1OiLCJhbGciOlJIUz11NJ9.eyJlbWFpbcf6inhzdW5saWdodHM4QGdrYWIsLmNvbSJ9.PyLKhj7mFQzY3DX7LAbeSW5Cf0ljfeluH-llHc_1Azd4";
        String url = "https://dniruc.apisperu.com/api/v1/dni/46688592?token=" + token;
        
        RestTemplate restTemplate = new RestTemplate();
        try {
            String result = restTemplate.getForObject(url, String.class);
            return "✅ Token funciona! Respuesta: " + result;
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}