package com.example.demo.service;

import com.example.demo.model.DniResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
public class DniService {
    
    private final WebClient webClient;
    
    public DniService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.apis.net.pe")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.USER_AGENT, "BibliosferaApp/1.0")
                .build();
    }
    
    public DniResponse consultarDni(String dni) {
        System.out.println("=== CONSULTANDO DNI EN api.apis.net.pe ===");
        System.out.println("DNI: " + dni);
        
        try {
            // Validar formato de DNI
            if (dni == null || !dni.matches("\\d{8}")) {
                System.err.println("❌ DNI inválido: debe tener 8 dígitos");
                return null;
            }
            
            DniResponse response = webClient.get()
                    .uri("/v1/dni?numero=" + dni)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                        System.err.println("❌ Error 4xx en la API");
                        return Mono.error(new RuntimeException("Error en la solicitud"));
                    })
                    .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                        System.err.println("❌ Error 5xx en la API");
                        return Mono.error(new RuntimeException("Error del servidor"));
                    })
                    .bodyToMono(DniResponse.class)
                    .block();
            
            if (response != null && response.getNombres() != null && !response.getNombres().isEmpty()) {
                System.out.println("✅ Consulta exitosa");
                System.out.println("   Nombre: " + response.getNombres());
                System.out.println("   Apellido Paterno: " + response.getApellidoPaterno());
                System.out.println("   Apellido Materno: " + response.getApellidoMaterno());
                return response;
            } else {
                System.err.println("⚠️ API respondió pero con datos vacíos");
                return null;
            }
            
        } catch (WebClientResponseException.NotFound e) {
            System.err.println("❌ DNI no encontrado: " + dni);
            return null;
        } catch (WebClientResponseException e) {
            System.err.println("❌ Error HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}