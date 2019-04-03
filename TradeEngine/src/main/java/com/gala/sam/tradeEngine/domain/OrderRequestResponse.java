package com.gala.sam.tradeEngine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderRequestResponse {

  enum ResponseType {SUCCESS, ERROR};

  ResponseType responseType;

  public static OrderRequestResponse Success() {
    return new OrderRequestResponse(ResponseType.SUCCESS);
  }

  public static OrderRequestResponse Error() {
    return new OrderRequestResponse(ResponseType.ERROR);
  }

}
