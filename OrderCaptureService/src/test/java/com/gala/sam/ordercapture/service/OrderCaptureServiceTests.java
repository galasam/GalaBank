package com.gala.sam.ordercapture.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.gala.sam.orderrequestlibrary.OrderCSVParser;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse.ResponseType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class OrderCaptureServiceTests {

  @Test
  public void testSuccessfulOrderEntered() {
    //Given: order request as csv that contains one successful order
    final String csv = "example";

    final AbstractOrderRequest orderRequest = LimitOrderRequest.builder().build();
    OrderCSVParser orderCSVParser = mock(OrderCSVParser.class);
    List<AbstractOrderRequest> orderRequests = new LinkedList<>();
    orderRequests.add(orderRequest);
    given(orderCSVParser.decodeCSV(csv)).willReturn(orderRequests);

    TradeEngineGateway tradeEngineGateway = mock(TradeEngineGateway.class);
    given(tradeEngineGateway.enterOrder(orderRequest)).willReturn(OrderRequestResponse.success());

    //When: entered
    OrderCaptureService orderCaptureService = new OrderCaptureService(orderCSVParser,
        tradeEngineGateway);
    OrderRequestResponse orderRequestResponse = orderCaptureService.enterOrder(csv);

    //Then: it is successful
    Assert.assertEquals("Request is successful", ResponseType.SUCCESS,
        orderRequestResponse.getResponseType());
  }

  @Test
  public void testOnlySubmitsFirstOrder() {
    //Given: order request as csv that contains multiple successful order
    final String csv = "example";

    OrderCSVParser orderCSVParser = mock(OrderCSVParser.class);
    List<AbstractOrderRequest> orderRequests = new LinkedList<>();
    AbstractOrderRequest firstOrderRequest = LimitOrderRequest.builder().build();
    orderRequests.add(firstOrderRequest);
    orderRequests.add(LimitOrderRequest.builder().build());
    orderRequests.add(LimitOrderRequest.builder().build());
    given(orderCSVParser.decodeCSV(csv)).willReturn(orderRequests);

    TradeEngineGateway tradeEngineGateway = mock(TradeEngineGateway.class);
    given(tradeEngineGateway.enterOrder(firstOrderRequest))
        .willReturn(OrderRequestResponse.success());

    //When: entered
    OrderCaptureService orderCaptureService = new OrderCaptureService(orderCSVParser,
        tradeEngineGateway);
    OrderRequestResponse orderRequestResponse = orderCaptureService.enterOrder(csv);

    //Then: it only calls submit on the first
    Assert.assertEquals("Request is successful", ResponseType.SUCCESS,
        orderRequestResponse.getResponseType());
    then(tradeEngineGateway).should(times(1)).enterOrder(any());
    then(tradeEngineGateway).should().enterOrder(firstOrderRequest);
  }


}
