package com.gala.sam.tradeEngine.utils.orderValidators;

import com.gala.sam.tradeEngine.domain.orderrequest.AbstractOrderRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseOrderValidator {
   static List<String> findErrors(AbstractOrderRequest order) {
      List<String> errors = new ArrayList<>();
      errors.addAll(findErrorsInClientId(order.getClientId()));
      errors.addAll(findErrorsInQuantity(order.getQuantity()));
      return errors;
   }

   static private Collection<String> findErrorsInClientId(int clientId) {
      List<String> errors = new ArrayList<>();
      if(clientId < 0) {
         errors.add("clientId price cannot be negative");
      }
      return errors;
   }

   static private List<String> findErrorsInQuantity(int quantity) {
      List<String> errors = new ArrayList<>();
      if(quantity < 0) {
         errors.add("quantity price cannot be negative");
      }
      return errors;
   }
}
