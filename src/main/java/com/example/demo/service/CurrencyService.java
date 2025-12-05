package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyService {
    
    private static final BigDecimal TASA_DEFAULT = new BigDecimal("3.85");
    
    public BigDecimal getExchangeRate(String toCurrency) {
        if ("PEN".equals(toCurrency)) {
            return BigDecimal.ONE;
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