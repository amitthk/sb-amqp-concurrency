package com.amitthk.sbamqpconcurrency.model;

import lombok.Data;

@Data
public class StockPrices {
    private String quote;
    private String response;

    public StockPrices(String quote, String response) {
        this.quote=quote;
        this.response=response;
    }
}
