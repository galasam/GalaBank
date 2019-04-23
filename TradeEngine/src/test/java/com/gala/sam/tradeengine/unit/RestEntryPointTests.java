package com.gala.sam.tradeengine.unit;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse.ResponseType;
import com.gala.sam.tradeengine.domain.enteredorder.AbstractOrder;
import com.gala.sam.tradeengine.domain.enteredorder.LimitOrder;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.Direction;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest.TimeInForce;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import com.gala.sam.tradeengine.entrypoint.RestEntryPoint;
import com.gala.sam.tradeengine.service.MarketService;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(RestEntryPoint.class)
public class RestEntryPointTests {

  @Autowired
  private MockMvc mvc;

  @MockBean
  private MarketService marketService;

  @Autowired
  ObjectMapper jsonMapper;

  @Test
  public void testEnterOrderSuccess() throws Exception {

    //Given: an order request and market service will return successfully.
    AbstractOrderRequest orderRequest = LimitOrderRequest.builder()
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .limit(1.1f)
        .build();

    String orderRequestJsonString = jsonMapper.writeValueAsString(orderRequest);

    AbstractOrder order = LimitOrder.builder()
        .orderId(99)
        .clientId(1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .limit(1.1f)
        .build();

    given(marketService.enterOrder(orderRequest)).willReturn(Optional.of(order));

    //When: enter order api is called
    mvc.perform(post("/enter-order")
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderRequestJsonString))
        .andExpect(mvcResult -> {
          OrderRequestResponse response = jsonMapper
              .readValue(mvcResult.getResponse().getContentAsString(), OrderRequestResponse.class);
          if (response.getResponseType() != ResponseType.SUCCESS) {
            throw new Exception("Response should be successful");
          }
        });
  }

  @Test
  public void testEnterOrderError() throws Exception {

    //Given: an order request and market service will return successfully.
    AbstractOrderRequest orderRequest = LimitOrderRequest.builder()
        .clientId(-1)
        .direction(Direction.BUY)
        .quantity(1)
        .timeInForce(TimeInForce.GTC)
        .ticker("Greggs")
        .limit(1.1f)
        .build();

    String orderRequestJsonString = jsonMapper.writeValueAsString(orderRequest);

    given(marketService.enterOrder(orderRequest)).willReturn(Optional.empty());

    //When: enter order api is called
    mvc.perform(post("/enter-order")
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderRequestJsonString))
        .andExpect(mvcResult -> {
          OrderRequestResponse response = jsonMapper
              .readValue(mvcResult.getResponse().getContentAsString(), OrderRequestResponse.class);
          if (response.getResponseType() != ResponseType.ERROR) {
            throw new Exception("Response should fail");
          }
        });
  }
}
