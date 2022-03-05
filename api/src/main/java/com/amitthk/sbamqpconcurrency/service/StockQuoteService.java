package com.amitthk.sbamqpconcurrency.service;

import com.amitthk.sbamqpconcurrency.model.StockPrices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class StockQuoteService {

    @Value("${rapid.api-url}")
    String rapidApiUrl;

    @Value("${rapid.headers.api-host}")
    String rapidHeadersApiHost;

    @Value("${rapid.headers.api-key}")
    String rapidHeadersApiKey;

    public StockPrices getQuotes(String quote, LocalDateTime startDate, LocalDateTime endDate) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(rapidApiUrl))
                .header("x-rapidapi-host",rapidHeadersApiHost)
                .header("x-rapidapi-key", rapidHeadersApiKey)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
        StockPrices price = new StockPrices(quote,response.body().toString());
        return price;
    }
}
