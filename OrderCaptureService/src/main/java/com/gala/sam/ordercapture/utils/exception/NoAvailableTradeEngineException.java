package com.gala.sam.ordercapture.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoAvailableTradeEngineException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NoAvailableTradeEngineException(String hostname) {
    super("hostname: " + hostname);
  }
}
