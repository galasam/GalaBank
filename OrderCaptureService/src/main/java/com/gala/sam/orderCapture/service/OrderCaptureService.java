package com.gala.sam.orderCapture.service;

import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.utils.OrderCSVParser;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class OrderCaptureService {

  final TradeEngineGateway tradeEngineGateway;

  public OrderRequestResponse enterOrder(String csvInput) {
    AbstractOrderRequest orderRequest = parseOrderRequest(csvInput);
    OrderRequestResponse response = tradeEngineGateway.enterOrder(orderRequest);
    return response;
  }

  private AbstractOrderRequest parseOrderRequest(String csvInput) {
    final Stream<String> inputRows = Pattern.compile("\n").splitAsStream(csvInput);
    final List<AbstractOrderRequest> orders = OrderCSVParser.decodeCSV(inputRows);
    if (orders.size() > 1) {
      log.warn("Multiple orders {} passed in but only one used", orders.size());
    }
    return orders.get(0);
  }

}
