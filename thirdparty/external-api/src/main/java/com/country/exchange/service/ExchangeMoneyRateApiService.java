package com.country.exchange.service;


import com.country.exchange.dto.ExchangeAlternativeApiResult;
import com.country.exchange.dto.ExchangeCurrencyApiResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeMoneyRateApiService {

    @Value("${exchange.url}")
    private String apiUrl;

    @Value("${exchange.access-key}")
    private String accessKey;

//    @Value("${rate.exchange.url}")
//    private String alternativeUrl;

    private final RestTemplate exchangeRestTemplate;

    private final RestTemplate rateTemplate;

    @Retryable(maxAttempts = 2,
            backoff = @Backoff(delay = 200))
    public ExchangeCurrencyApiResult getLiveRate(String code, String countries) {
        String requestUrl = getRequest(code, countries);

        ExchangeCurrencyApiResult result = exchangeRestTemplate.getForObject(requestUrl, ExchangeCurrencyApiResult.class);

        log.info("getLiveRate {}", result);
        return result;
    }

    @Retryable(maxAttempts = 2,
            backoff = @Backoff(delay = 200))
    public ExchangeAlternativeApiResult getAlternativeLiveRate(String code, String countries) {
        String requestUrl = getAlternativeRequest(code, countries);

        ExchangeAlternativeApiResult result = rateTemplate.getForObject(requestUrl, ExchangeAlternativeApiResult.class);

        log.info("getLiveRate {}", result);
        return result;
    }

    private String getAlternativeRequest(String source, String currenciesString) {
        return UriComponentsBuilder.fromHttpUrl("test")
                .path("/latest")
                .queryParam("base", source)
                .queryParam("symbols", currenciesString)
                .build()
                .toUriString();
    }

    private String getRequest(String source, String currenciesString) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl)
                .path("/api/live")
                .queryParam("access_key", accessKey)
                .queryParam("source", source)
                .queryParam("currencies", currenciesString)
                .build()
                .toUriString();
    }

}
