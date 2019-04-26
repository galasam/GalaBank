package com.gala.sam.ordercapture.service;

import com.gala.sam.ordercapture.utils.exception.NoAvailableTradeEngineException;
import com.gala.sam.ordercapture.utils.exception.OrderNotEnteredException;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
public class TradeEngineGateway {

  private final String serviceHostName;
  private final RestTemplate restTemplate;
  private final EurekaClient discoveryClient;

  OrderRequestResponse enterOrder(AbstractOrderRequest order) {
    String url = getURL();

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

  private String getURL() {
    InstanceInfo serverInstance = discoveryClient
        .getNextServerFromEureka(serviceHostName, false);
    if (serverInstance == null) {
      throw new NoAvailableTradeEngineException(serviceHostName);
    }
    return serverInstance.getHomePageUrl() + "/enter-order";
  }

}
