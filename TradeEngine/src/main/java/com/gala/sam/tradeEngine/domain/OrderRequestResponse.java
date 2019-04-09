package com.gala.sam.tradeEngine.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderRequestResponse {

  ResponseType responseType;

  ;

  public static OrderRequestResponse Success() {
    return new OrderRequestResponse(ResponseType.SUCCESS);
  }

  public static OrderRequestResponse Error() {
    return new OrderRequestResponse(ResponseType.ERROR);
  }

  enum ResponseType {SUCCESS, ERROR}

}
