package com.gala.sam.orderCapture.service;

import com.gala.sam.orderCapture.utils.OrderCSVParser;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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
    return orders.get(0);
  }

}
