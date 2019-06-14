package com.country.exchange.dto;

import com.country.exchange.service.CurrencyCalculator;
import com.country.exchange.service.ExchangeService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@Data
public class ExchangeAlternativeApiResult implements ExchangeService.ExchangeSource {

    private String date;
    private String source;
    @JsonProperty("rates")
    private Map<String, Double> quotes = new HashMap<>();
    private String error;


    @Override
    public Map<CurrencyCountry, String> mapExchangeCurrency(CurrencyCalculator calculator, double transferMoney) {
        return quotes.entrySet()
                .stream()
                .collect(Collectors
                        .toMap(e -> CurrencyCountry.of(e.getKey()),
                                e -> calculator.calculateTotalWithFormat(e.getValue(), transferMoney)));
    }

    @Override
    public Map<CurrencyCountry, String> mapBeforeCurrency(CurrencyCalculator calculator) {
        return null;
    }

}
