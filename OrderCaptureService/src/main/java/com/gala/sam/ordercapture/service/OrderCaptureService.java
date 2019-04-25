package com.gala.sam.ordercapture.service;

import com.gala.sam.orderrequestlibrary.OrderCSVParser;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderCaptureService {

  final OrderCSVParser orderCSVParser;
  final TradeEngineGateway tradeEngineGateway;

  public OrderRequestResponse enterOrder(String csvInput) {
    AbstractOrderRequest orderRequest = parseOrderRequest(csvInput);
    log.info("Order Request Created: {}", orderRequest);
    return tradeEngineGateway.enterOrder(orderRequest);
  }

  private AbstractOrderRequest parseOrderRequest(String csvInput) {
    final List<AbstractOrderRequest> orders = orderCSVParser.decodeCSV(csvInput);
    if (orders.size() > 1) {
      log.warn("Multiple orders {} passed in but only the first is used", orders.size());
    }
    return orders.get(0);
  }

}
