package com.gala.sam.tradeEngine.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PublicMarketStatus {

    List<Trade> trades;
    List<Ticker> orders;

    @Value
    @Builder
    public static class Ticker {
        String name;
        List<ReadyOrder> buy;
        List<ReadyOrder> sell;
    }

}
