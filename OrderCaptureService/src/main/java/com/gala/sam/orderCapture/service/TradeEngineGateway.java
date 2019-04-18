package com.gala.sam.orderCapture.service;

import com.gala.sam.orderCapture.utils.exception.OrderNotEnteredException;
import com.gala.sam.orderRequestLibrary.OrderRequestResponse;
import com.gala.sam.orderRequestLibrary.orderrequest.AbstractOrderRequest;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class TradeEngineGateway {

  protected final String serviceHostName;
  private final RestTemplate restTemplate;
  private final EurekaClient discoveryClient;

  public OrderRequestResponse enterOrder(AbstractOrderRequest order) {
    InstanceInfo serverInstance = discoveryClient
        .getNextServerFromEureka(serviceHostName, false);
    String url = serverInstance.getHomePageUrl() + "/enter-order";

    ResponseEntity<OrderRequestResponse> restResponseObject = restTemplate
        .postForEntity(url, order, OrderRequestResponse.class);

    if (restResponseObject.getStatusCode().is2xxSuccessful()) {
      OrderRequestResponse orderRequestResponse = restResponseObject.getBody();
      log.info("Order Entered in to Trade Engine: {}", orderRequestResponse);
      return orderRequestResponse;
    } else {
      log.error("trade engine reported that order request {} was not entered in to the market",
          order.toString());
      throw new OrderNotEnteredException(order);
    }
  }

}
