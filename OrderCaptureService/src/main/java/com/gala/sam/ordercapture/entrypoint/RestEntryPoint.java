package com.gala.sam.ordercapture.entrypoint;

import com.gala.sam.ordercapture.service.OrderCaptureService;
import com.gala.sam.orderrequestlibrary.OrderRequestResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class RestEntryPoint {

  final OrderCaptureService orderCaptureService;

  @PostMapping("/enter-order")
  public OrderRequestResponse enterOrder(@RequestBody String csvInput) {
    log.info("CSV received: {}", csvInput);
    OrderRequestResponse response = orderCaptureService.enterOrder(csvInput);
    return response;
  }

}
