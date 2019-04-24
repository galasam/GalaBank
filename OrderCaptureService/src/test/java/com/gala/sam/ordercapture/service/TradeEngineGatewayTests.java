package com.gala.sam.ordercapture.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.gala.sam.ordercapture.utils.exception.NoAvailableTradeEngineException;
import com.gala.sam.ordercapture.utils.exception.OrderNotEnteredException;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse.ResponseType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.StopLimitOrderRequest;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TradeEngineGatewayTests {

  @Test
  public void canEnterOrderSuccessfully() {
    //Given: an order that will succeed
    final AbstractOrderRequest orderRequest = StopLimitOrderRequest.builder().build();
    final String homepageURL = "http://example.com";

    InstanceInfo serverInstance = mock(InstanceInfo.class);
    given(serverInstance.getHomePageUrl()).willReturn(homepageURL);

    DiscoveryClient discoveryClient = mock(DiscoveryClient.class);
    given(discoveryClient.getNextServerFromEureka(any(), eq(false))).willReturn(serverInstance);

    RestTemplate restTemplate = mock(RestTemplate.class);

    @SuppressWarnings("unchecked")
    ResponseEntity<OrderRequestResponse> postResponse = mock(ResponseEntity.class);
    given(postResponse.getStatusCode()).willReturn(HttpStatus.OK);
    given(postResponse.getBody()).willReturn(OrderRequestResponse.success());

    given(restTemplate
        .postForEntity(homepageURL + "/enter-order", orderRequest, OrderRequestResponse.class))
        .willReturn(postResponse);

    TradeEngineGateway tradeEngineGateway = new TradeEngineGateway("example",
        restTemplate, discoveryClient);

    //When: it is entered
    OrderRequestResponse methodResponse = tradeEngineGateway.enterOrder(orderRequest);

    //Then: the response it successful
    Assert.assertEquals("Response is successful", ResponseType.SUCCESS,
        methodResponse.getResponseType());
  }

  @Test
  public void testFailingOrderRequest() {
    //Given: an order that will succeed
    final AbstractOrderRequest orderRequest = StopLimitOrderRequest.builder().build();
    final String homepageURL = "http://example.com";

    InstanceInfo serverInstance = mock(InstanceInfo.class);
    given(serverInstance.getHomePageUrl()).willReturn(homepageURL);

    DiscoveryClient discoveryClient = mock(DiscoveryClient.class);
    given(discoveryClient.getNextServerFromEureka(any(), eq(false))).willReturn(serverInstance);

    RestTemplate restTemplate = mock(RestTemplate.class);

    @SuppressWarnings("unchecked")
    ResponseEntity<OrderRequestResponse> postResponse = mock(ResponseEntity.class);
    given(postResponse.getStatusCode()).willReturn(HttpStatus.OK);
    given(postResponse.getBody()).willReturn(OrderRequestResponse.error());

    given(restTemplate
        .postForEntity(homepageURL + "/enter-order", orderRequest, OrderRequestResponse.class))
        .willReturn(postResponse);

    TradeEngineGateway tradeEngineGateway = new TradeEngineGateway("example",
        restTemplate, discoveryClient);

    //When: it is entered
    OrderRequestResponse methodResponse = tradeEngineGateway.enterOrder(orderRequest);

    //Then: the response it successful
    Assert.assertEquals("Response is successful", ResponseType.ERROR,
        methodResponse.getResponseType());
  }

  @Test
  public void testFailingPostRequest() {
    //Given: an order that will succeed
    final AbstractOrderRequest orderRequest = StopLimitOrderRequest.builder().build();
    final String homepageURL = "http://example.com";

    InstanceInfo serverInstance = mock(InstanceInfo.class);
    given(serverInstance.getHomePageUrl()).willReturn(homepageURL);

    DiscoveryClient discoveryClient = mock(DiscoveryClient.class);
    given(discoveryClient.getNextServerFromEureka(any(), eq(false))).willReturn(serverInstance);

    RestTemplate restTemplate = mock(RestTemplate.class);

    @SuppressWarnings("unchecked")
    ResponseEntity<OrderRequestResponse> postResponse = mock(ResponseEntity.class);
    given(postResponse.getStatusCode()).willReturn(HttpStatus.NOT_FOUND);

    given(restTemplate
        .postForEntity(homepageURL + "/enter-order", orderRequest, OrderRequestResponse.class))
        .willReturn(postResponse);

    TradeEngineGateway tradeEngineGateway = new TradeEngineGateway("example",
        restTemplate, discoveryClient);

    try {
      //When: it is entered
      OrderRequestResponse methodResponse = tradeEngineGateway.enterOrder(orderRequest);

      //Then: an exception is thrown
    } catch (OrderNotEnteredException e) {
      Assert.assertEquals("", new OrderNotEnteredException(orderRequest).getMessage(),
          e.getMessage());
      return;
    }
    Assert.fail("Should throw OrderNotEnteredException");
  }

  @Test
  public void testFailingDiscoveryClient() {
    //Given: an order that will succeed
    final AbstractOrderRequest orderRequest = StopLimitOrderRequest.builder().build();
    final String homepageURL = "http://example.com";
    final String hostname = "EXAMPLE";

    DiscoveryClient discoveryClient = mock(DiscoveryClient.class);
    given(discoveryClient.getNextServerFromEureka(any(), eq(false))).willReturn(null);

    RestTemplate restTemplate = mock(RestTemplate.class);

    @SuppressWarnings("unchecked")
    ResponseEntity<OrderRequestResponse> postResponse = mock(ResponseEntity.class);
    given(postResponse.getStatusCode()).willReturn(HttpStatus.NOT_FOUND);

    given(restTemplate
        .postForEntity(homepageURL + "/enter-order", orderRequest, OrderRequestResponse.class))
        .willReturn(postResponse);

    TradeEngineGateway tradeEngineGateway = new TradeEngineGateway(hostname,
        restTemplate, discoveryClient);

    try {
      //When: it is entered
      OrderRequestResponse methodResponse = tradeEngineGateway.enterOrder(orderRequest);

      //Then: an exception is thrown
    } catch (NoAvailableTradeEngineException e) {
      Assert.assertEquals("", new NoAvailableTradeEngineException(hostname).getMessage(),
          e.getMessage());
      return;
    }
    Assert.fail("Should throw NoAvailableTradeEngineException");
  }


}
