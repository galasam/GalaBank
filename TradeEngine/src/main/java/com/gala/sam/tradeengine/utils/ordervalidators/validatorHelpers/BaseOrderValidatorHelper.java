package com.gala.sam.tradeengine.utils.ordervalidators.validatorHelpers;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import java.util.ArrayList;
import java.util.List;

public class BaseOrderValidatorHelper {

  public static List<String> findErrors(AbstractOrderRequest order) {
    List<String> errors = new ArrayList<>();
    errors.addAll(findErrorsInClientId(order.getClientId()));
    errors.addAll(findErrorsInQuantity(order.getQuantity()));
    return errors;
  }

  static private List<String> findErrorsInClientId(int clientId) {
    List<String> errors = new ArrayList<>();
    if (clientId < 0) {
      errors.add("clientId price is negative.");
    }
    return errors;
  }

  static private List<String> findErrorsInQuantity(int quantity) {
    List<String> errors = new ArrayList<>();
    if (quantity < 0) {
      errors.add("quantity price is negative");
    }
    return errors;
  }
}