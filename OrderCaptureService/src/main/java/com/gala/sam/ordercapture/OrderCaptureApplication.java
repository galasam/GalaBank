package com.gala.sam.ordercapture;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@Slf4j
@SpringBootApplication
@EnableEurekaClient
public class OrderCaptureApplication {

  public static void main(String[] args) {
    System.setProperty("spring.config.name", "order-capture");
    SpringApplication.run(OrderCaptureApplication.class, args);
    log.info("Order Capture Started");
  }
}
