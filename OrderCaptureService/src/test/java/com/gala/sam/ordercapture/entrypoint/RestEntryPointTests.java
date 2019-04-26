package com.gala.sam.ordercapture.entrypoint;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gala.sam.ordercapture.service.OrderCaptureService;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse.ResponseType;
import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import com.gala.sam.orderrequestlibrary.orderrequest.LimitOrderRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@WebMvcTest(RestEntryPoint.class)
public class RestEntryPointTests {

  @Autowired
  ObjectMapper jsonMapper;
  @Autowired
  private MockMvc mvc;
  @MockBean
  private OrderCaptureService orderCaptureService;

  @Test
  public void testEntryPointCallsService() throws Exception {
    //Given: an order request and orderCaptureService that returns successfully
    AbstractOrderRequest orderRequest = LimitOrderRequest.builder().build();
    String orderRequestJsonString = jsonMapper.writeValueAsString(orderRequest);
    given(orderCaptureService.enterOrder(orderRequestJsonString))
        .willReturn(OrderRequestResponse.success());

    //When: Endpoint is hit
    MvcResult result = mvc.perform(post("/enter-order")
        .contentType(MediaType.APPLICATION_JSON)
        .content(orderRequestJsonString))
        .andReturn();

    //Then: Service is called once with correct args and result returned to users
    then(orderCaptureService).should(times(1)).enterOrder(any());
    then(orderCaptureService).should().enterOrder(orderRequestJsonString);
    OrderRequestResponse response = jsonMapper
        .readValue(result.getResponse().getContentAsString(), OrderRequestResponse.class);
    Assert.assertEquals("Returned result is what service provided", ResponseType.SUCCESS,
        response.getResponseType());
  }

}
