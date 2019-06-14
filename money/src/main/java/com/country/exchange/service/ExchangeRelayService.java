package com.country.exchange.service;

import com.country.exchange.dto.ExchangeCurrencyApiResult;
import com.country.exchange.dto.ExchangeCurrencyRequest;
import com.country.exchange.dto.ExchangeCurrencyResponse;
import com.country.exchange.dto.ExchangeRedisDto;
import com.country.exchange.exception.BusinessError;
import com.country.exchange.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.country.exchange.service.ExchangeService.ExchangeSource;

@Service
@RequiredArgsConstructor
public class ExchangeRelayService {

    private final ExchangeMoneyRateApiService exchangeMoneyRateApiService;
    private final ExchangeRedisService exchangeRedisService;
    private final ExchangeService exchangeService;

    public ExchangeCurrencyResponse getCurrency(@Valid ExchangeCurrencyRequest request) {

        ExchangeSource source = getCurrencyData(request);

        return ExchangeCurrencyResponse.builder()
                .source(request.getSource().getCode())
                .currency(exchangeService.calculate(source, request.getTransferMoney()))
                .beforeCalculate(exchangeService.beforeCalculate(source)).build();
    }

    private ExchangeSource getCurrencyData(ExchangeCurrencyRequest request) {
        Optional<ExchangeRedisDto> currencyData = exchangeRedisService.getCurrencyData(request.getSource().getCode());

        if (currencyData.isPresent()) return currencyData.get();

        return getLiveDataFromApi(request);
    }

    private ExchangeSource getLiveDataFromApi(ExchangeCurrencyRequest request) {
        String countries = request.getCurrencies().stream().map(currency -> currency.getCode()).collect(Collectors.joining(","));
        ExchangeCurrencyApiResult liveRate = exchangeMoneyRateApiService.getLiveRate(request.getSource().getCode(), countries);

        if (liveRate.isValidData()) return liveRate;

//        ExchangeAlternativeApiResult alternative = exchangeMoneyRateApiService.getAlternativeLiveRate(request.getSource().getCode(), countries);

//        if (alternative.getQuotes().size() > 0) return alternative;

        throw new BusinessException(BusinessError.NO_DATA);
    }

}
