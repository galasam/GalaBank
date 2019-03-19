package com.gala.sam.tradeEngine;

import com.gala.sam.tradeEngine.entrypoint.FileEntryPoint;
import com.gala.sam.tradeEngine.service.MarketService;
import com.gala.sam.tradeEngine.service.OrderMatchingService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  @Bean
  public MarketService marketService() {
    return new MarketService();
  }

  @Bean
  public OrderMatchingService orderMatchingService() {
    return new OrderMatchingService(marketService());
  }

  @Bean
  public FileEntryPoint fileEntryPoint() {
    return new FileEntryPoint(orderMatchingService());
  }
}
