package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TestToken implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        testTokenValido();
    }
    
    private void testTokenValido() {
        System.out.println("=== TESTEANDO TOKEN APIS PERÚ ===");
        
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InNvbC5jbDg3NTBAZ21haWwuY29tIn0.cnFcUliqqBq_VH7fLViVIhnWhU_mgW4f9WDLgJTDHAU";
        String url = "https://dniruc.apisperu.com/api/v1/dni/46688592?token=" + token;
        
        RestTemplate restTemplate = new RestTemplate();
        
        try {
            String resultado = restTemplate.getForObject(url, String.class);
            System.out.println("Respuesta cruda: " + resultado);
            
            if (resultado.contains("\"nombres\":\"JOSE\"")) {
                System.out.println("✅ Token VÁLIDO - Datos reales");
            } else if (resultado.contains("\"success\":false")) {
                System.out.println("❌ Token INVÁLIDO o sin créditos");
                System.out.println("Respuesta completa: " + resultado);
            } else {
                System.out.println("⚠️ Respuesta inesperada");
            }
        } catch (Exception e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
    }
}