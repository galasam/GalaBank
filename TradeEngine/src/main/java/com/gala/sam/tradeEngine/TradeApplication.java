package com.gala.sam.tradeEngine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@Slf4j
@SpringBootApplication
@EnableEurekaClient
public class TradeApplication {

  public static void main(String[] args) {
    System.setProperty("spring.config.name", "trade-engine");
    SpringApplication.run(TradeApplication.class, args);
    log.info("Trade Engine Started");
  }
}
