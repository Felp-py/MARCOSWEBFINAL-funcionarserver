package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Value;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class CurrencyService {
    
    @Value("${currency.api.key:}")
    private String apiKey;
    
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/%s/latest/PEN";
    private static final BigDecimal TASA_DEFAULT = new BigDecimal("3.85");
    
    @Cacheable(value = "exchangeRates", unless = "#result == null")
    public BigDecimal getExchangeRate(String toCurrency) {
        if ("PEN".equals(toCurrency)) {
            return BigDecimal.ONE;
        }
        
        try {
            String url = String.format(API_URL, apiKey);
            RestTemplate restTemplate = new RestTemplate();
            
            // Si no tienes API key, usa una alternativa gratuita
            if (apiKey == null || apiKey.isEmpty()) {
                url = "https://api.exchangerate-api.com/v4/latest/PEN";
            }
            
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("conversion_rates")) {
                Map<String, Object> rates = (Map<String, Object>) response.get("conversion_rates");
                if (rates.containsKey(toCurrency)) {
                    return new BigDecimal(rates.get(toCurrency).toString())
                            .setScale(4, RoundingMode.HALF_UP);
                }
            }
        } catch (Exception e) {
            // Log error
            System.err.println("Error obteniendo tasa de cambio: " + e.getMessage());
        }
        
        return TASA_DEFAULT;
    }
    
    public BigDecimal convertToUSD(BigDecimal amountInPEN) {
        BigDecimal rate = getExchangeRate("USD");
        return amountInPEN.divide(rate, 2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal convertToPEN(BigDecimal amountInUSD) {
        BigDecimal rate = getExchangeRate("USD");
        return amountInUSD.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }
}