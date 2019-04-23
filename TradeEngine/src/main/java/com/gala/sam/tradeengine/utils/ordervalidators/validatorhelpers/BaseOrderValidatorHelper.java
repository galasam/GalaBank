package com.gala.sam.tradeengine.utils.ordervalidators.validatorhelpers;

import com.gala.sam.orderrequestlibrary.orderrequest.AbstractOrderRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BaseOrderValidatorHelper {

  public static List<String> findErrors(AbstractOrderRequest order) {
    List<String> errors = new ArrayList<>();
    errors.addAll(findErrorsInClientId(order.getClientId()));
    errors.addAll(findErrorsInQuantity(order.getQuantity()));
    return errors;
  }

  private static List<String> findErrorsInClientId(int clientId) {
    List<String> errors = new ArrayList<>();
    if (clientId < 0) {
      errors.add("clientId price is negative.");
    }
    return errors;
  }

  private static List<String> findErrorsInQuantity(int quantity) {
    List<String> errors = new ArrayList<>();
    if (quantity < 0) {
      errors.add("quantity price is negative");
    }
    return errors;
  }
}
