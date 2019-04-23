package com.gala.sam.orderCapture;

import com.gala.sam.orderCapture.service.TradeEngineGateway;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

  public static final String TRADE_ENGINE_HOSTNAME = "TRADE-ENGINE";

  final EurekaClient discoveryClient;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  public TradeEngineGateway tradeEngineService() {
    return new TradeEngineGateway(TRADE_ENGINE_HOSTNAME, restTemplate(), discoveryClient);
  }

}
