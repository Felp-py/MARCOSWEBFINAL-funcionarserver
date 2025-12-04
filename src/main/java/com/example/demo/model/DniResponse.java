package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DniResponse {
    
    @JsonProperty("nombres")
    private String nombres;
    
    @JsonProperty("apellidoPaterno")
    private String apellidoPaterno;
    
    @JsonProperty("apellidoMaterno")
    private String apellidoMaterno;
    
    @JsonProperty("tipoDocumento")
    private String tipoDocumento;
    
    @JsonProperty("numeroDocumento")
    private String numeroDocumento;
    
    // Constructor vacío necesario para Jackson
    public DniResponse() {}
    
    // Getters (Lombok los genera con @Data, pero por si acaso)
    public String getNombres() {
        return nombres;
    }
    
    public String getApellidoPaterno() {
        return apellidoPaterno;
    }
    
    public String getApellidoMaterno() {
        return apellidoMaterno;
    }
}