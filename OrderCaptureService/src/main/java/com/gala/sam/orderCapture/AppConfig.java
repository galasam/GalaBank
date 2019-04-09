package com.gala.sam.orderCapture;

import com.gala.sam.orderCapture.service.TradeEngineGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

  public static final String TRADE_ENGINE_HOSTNAME = "TRADE-ENGINE";

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public TradeEngineGateway tradeEngineService() {
    return new TradeEngineGateway(TRADE_ENGINE_HOSTNAME);
  }

}
