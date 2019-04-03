package com.gala.sam.orderCapture.service;

import com.gala.sam.orderCapture.utils.exception.OrderNotEnteredException;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class TradeEngineGateway {

  protected String serviceHostName;
  @Autowired
  RestTemplate restTemplate;
  @Autowired
  private EurekaClient discoveryClient;

  public TradeEngineGateway(String serviceHostName) {
    this.serviceHostName = serviceHostName;
  }

  public OrderRequestResponse enterOrder(AbstractOrderRequest order) {
    InstanceInfo serverInstance = discoveryClient
        .getNextServerFromEureka(serviceHostName, false);
    String url = serverInstance.getHomePageUrl() + "/enter-order";

    ResponseEntity<OrderRequestResponse> response = restTemplate
        .postForEntity(url, order, OrderRequestResponse.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      OrderRequestResponse resp = response.getBody();
      log.info("Order Entered in to Trade Engine: {}", resp);
      return resp;
    } else {
      throw new OrderNotEnteredException(order);
    }
  }

}
