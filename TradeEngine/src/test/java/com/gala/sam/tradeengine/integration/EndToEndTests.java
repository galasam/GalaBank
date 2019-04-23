package com.gala.sam.tradeengine.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse.ResponseType;
import com.gala.sam.tradeengine.IntegrationTest;
import com.gala.sam.tradeengine.domain.PublicMarketStatus;
import com.gala.sam.tradeengine.domain.PublicMarketStatus.Ticker;
import com.gala.sam.tradeengine.domain.Trade;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Category(IntegrationTest.class)
public class EndToEndTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  ObjectMapper jsonMapper;

  @Before
  public void reset() throws Exception {
    mvc.perform(post("/reset")
        .contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void enterOrderWithUnmatchedActiveOrder() throws Exception {
    //Given: a order request that won't match with anything (because market is empty)
    LimitOrderRequest limitOrderRequest = getLimitOrderRequest(Direction.BUY);
    LimitOrder referenceLimitOrder = getLimitOrder();
    String orderRequestJsonString = jsonMapper.writeValueAsString(limitOrderRequest);

    //When: the order is posted to the service
    MvcResult postResponse = performEnterOrderPostRequest(orderRequestJsonString);

    OrderRequestResponse response = jsonMapper
        .readValue(postResponse.getResponse().getContentAsString(), OrderRequestResponse.class);
    Assert.assertEquals("Response should be successful", ResponseType.SUCCESS,
        response.getResponseType());

    MvcResult getResponse = mvc.perform(get("/status")
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    PublicMarketStatus status = jsonMapper
        .readValue(getResponse.getResponse().getContentAsString(), PublicMarketStatus.class);

    //Then: the order is entered correctly in to the market
    List<Ticker> tickers = status.getOrders();
    Assert.assertEquals("There is a ticker", 1, tickers.size());
    Ticker ticker = tickers.get(0);
    Assert.assertEquals("Ticker name is correct", limitOrderRequest.getTicker(), ticker.getName());
    Assert.assertTrue("Order is not in wrong queue", ticker.getSell().isEmpty());
    Assert.assertEquals("Order is in right queue", 1, ticker.getBuy().size());
    AbstractActiveOrder order = ticker.getBuy().get(0);
    Assert.assertEquals("Order is correct one", referenceLimitOrder, order);

  }

  private LimitOrderRequest getLimitOrderRequest(Direction direction) {
    return LimitOrderRequest.builder()
        .clientId(1)
        .direction(direction)
        .ticker("Greggs")
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .limit(1.2f)
        .build();
  }

  private LimitOrder getLimitOrder() {
    return LimitOrder.builder()
        .orderId(1)
        .clientId(1)
        .direction(Direction.BUY)
        .ticker("Greggs")
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .limit(1.2f)
        .build();
  }

  @Test
  public void enterOrderWithMatchedActiveOrderToCreateTrade() throws Exception {
    //Given: two order requests that will fully match each other
    List<LimitOrderRequest> orderRequests = new ArrayList<>();
    orderRequests.add(getLimitOrderRequest(Direction.BUY));
    orderRequests.add(getLimitOrderRequest(Direction.SELL));

    Trade tradeReference = Trade.builder()
        .matchPrice(orderRequests.get(0).getLimit())
        .matchQuantity(orderRequests.get(0).getQuantity())
        .ticker(orderRequests.get(0).getTicker())
        .buyOrder(1)
        .sellOrder(2)
        .build();

    //When: the orders are posted to the service
    for (LimitOrderRequest orderRequest : orderRequests) {
      String orderRequestJsonString = jsonMapper.writeValueAsString(orderRequest);
      performEnterOrderPostRequest(orderRequestJsonString);
    }

    MvcResult getResponse = mvc.perform(get("/status")
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    PublicMarketStatus status = jsonMapper
        .readValue(getResponse.getResponse().getContentAsString(), PublicMarketStatus.class);

    //Then: the trade is shown correctly in the market and the orders disappear
    List<Ticker> tickers = status.getOrders();
    Assert.assertEquals("There is no ticker", 0, tickers.size());

    List<Trade> trades = status.getTrades();
    Assert.assertEquals("There is a trade", 1, trades.size());
    Trade trade = trades.get(0);
    Assert.assertEquals("Trade is correct one", tradeReference, trade);

  }

  private MvcResult performEnterOrderPostRequest(String orderRequestJsonString) throws Exception {
    return mvc.perform(post("/enter-order")
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderRequestJsonString))
        .andReturn();
  }

}
