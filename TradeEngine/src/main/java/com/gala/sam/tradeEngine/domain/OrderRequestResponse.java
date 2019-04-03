package com.gala.sam.tradeEngine.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class OrderRequestResponse {

  enum ResponseType {SUCCESS, ERROR};

  final ResponseType responseType;

  public static Success Success() {
    return new Success();
  }

  public static Error Error() {
    return new Error();
  }

  static class Success extends OrderRequestResponse {

    private final static ResponseType RESPONSE_TYPE = ResponseType.SUCCESS;

    public Success() {
      super(RESPONSE_TYPE);
    }
  }

  static class Error extends OrderRequestResponse {

    private final static ResponseType RESPONSE_TYPE = ResponseType.SUCCESS;

    public Error() {
      super(RESPONSE_TYPE);
    }
  }
}
