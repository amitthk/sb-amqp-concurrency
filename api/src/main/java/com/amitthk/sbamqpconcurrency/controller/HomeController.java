package com.amitthk.sbamqpconcurrency.controller;

import com.amitthk.sbamqpconcurrency.model.FetchStockDataModel;
import com.amitthk.sbamqpconcurrency.model.NotificationMessage;
import com.amitthk.sbamqpconcurrency.model.StockPrices;
import com.amitthk.sbamqpconcurrency.service.StockQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;

@Controller
public class HomeController {

//    @MessageMapping("/user-all")
//    @SendTo("/queue/sb_amqp_concurrency_topic")
//    public NotificationMessage sendToAll(@Payload NotificationMessage message) {
//        return message;
//    }

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @Autowired
    private StockQuoteService stockQuoteService;

    @PostMapping("/api/fetchall")
    public String fetchDateRange(FetchStockDataModel fetchStockDataModel) {

        List<CompletableFuture<StockPrices>> priceFutureList = fetchStockDataModel.getStockTickers().stream()
                .map(product -> CompletableFuture.supplyAsync(() -> stockQuoteService.getQuotes(product, fetchStockDataModel.getFrom(), fetchStockDataModel.getTo()), executorService))
                .collect(toList());

        CompletableFuture<List<StockPrices>> priceListFuture
                = sequence(priceFutureList);
    return "sometig";
    }

    private <T> CompletableFuture<List<T>> sequence(List<CompletableFuture<T>> futures) {
        CompletableFuture<Void> allFuturesDone =
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
        return allFuturesDone.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(toList()));
    }

}