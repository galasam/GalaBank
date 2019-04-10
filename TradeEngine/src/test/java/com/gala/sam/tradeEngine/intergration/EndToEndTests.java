package com.gala.sam.tradeEngine.intergration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gala.sam.tradeEngine.domain.OrderRequestResponse;
import com.gala.sam.tradeEngine.domain.OrderRequestResponse.ResponseType;
import com.gala.sam.tradeEngine.domain.PublicMarketStatus;
import com.gala.sam.tradeEngine.domain.PublicMarketStatus.Ticker;
import com.gala.sam.tradeEngine.domain.enteredorder.AbstractActiveOrder;
import com.gala.sam.tradeEngine.domain.enteredorder.LimitOrder;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.tradeEngine.domain.orderrequest.LimitOrderRequest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
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
public class EndToEndTests {

  @Autowired
  private MockMvc mvc;

  @Autowired
  ObjectMapper jsonMapper;

  @Test
  public void enterOrderWithUnmatchedActiveOrder() throws Exception {
    //Given: a order request that won't match with anything (because market is empty)
    LimitOrderRequest limitOrderRequest = getLimitOrderRequest();
    LimitOrder referenceLimitOrder = getLimitOrder();
    String orderRequestJsonString = jsonMapper.writeValueAsString(limitOrderRequest);

    //When: the order is posted to the service
    MvcResult postResponse = mvc.perform(post("/enter-order")
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderRequestJsonString))
        .andReturn();

    OrderRequestResponse response = jsonMapper
        .readValue(postResponse.getResponse().getContentAsString(), OrderRequestResponse.class);
    Assert.assertEquals("Response should be successful", ResponseType.SUCCESS,
        response.getResponseType());

    MvcResult getResponse = mvc.perform(get("/status")
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    PublicMarketStatus status = jsonMapper
        .readValue(getResponse.getResponse().getContentAsString(), PublicMarketStatus.class);

    List<Ticker> tickers = status.getOrders();
    Assert.assertEquals("There is a ticker", 1, tickers.size());
    Ticker ticker = tickers.get(0);
    Assert.assertEquals("Ticker name is correct", limitOrderRequest.getTicker(), ticker.getName());
    Assert.assertTrue("Order is not in wrong queue", ticker.getSell().isEmpty());
    Assert.assertEquals("Order is in right queue", 1, ticker.getBuy().size());
    AbstractActiveOrder order = ticker.getBuy().get(0);
    Assert.assertEquals("Order is correct one", referenceLimitOrder, order);

  }

  private LimitOrderRequest getLimitOrderRequest() {
    return LimitOrderRequest.builder()
        .clientId(1)
        .direction(Direction.BUY)
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

}
