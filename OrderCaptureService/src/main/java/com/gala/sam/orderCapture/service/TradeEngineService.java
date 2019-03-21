package com.gala.sam.orderCapture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.gala.sam.orderCapture.utils.exception.OrderNotEnteredException;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class TradeEngineService {

  @Autowired
  RestTemplate restTemplate;

  @Autowired
  private EurekaClient discoveryClient;

  protected String serviceHostName;

  public TradeEngineService(String serviceHostName) {
    this.serviceHostName = serviceHostName;
  }

  public JsonNode enterOrder(String orderInput) {
    InstanceInfo serverInstance = discoveryClient
        .getNextServerFromEureka(serviceHostName, false);
    String url = serverInstance.getHomePageUrl() + "/enter-order";

    ResponseEntity<JsonNode> response = restTemplate.postForEntity(url, orderInput, JsonNode.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      JsonNode json = response.getBody();
      log.info("Order Entered in to Trade Engine: {}", json);
      return json;
    } else {
      throw new OrderNotEnteredException(orderInput);
    }
  }

}
