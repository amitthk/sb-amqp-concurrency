package com.amitthk.sbamqpconcurrency.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class FetchStockDataModel {
    private List<String> stockTickers;
    private LocalDateTime from;
    private  LocalDateTime to;
}
