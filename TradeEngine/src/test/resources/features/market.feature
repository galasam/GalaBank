Feature: Enter Order
  In order to match orders
  A user must be able to submit an order request

  Scenario: limit order with negative client id is rejected
    Given limit order with negative client id
    When order is entered in to market
    Then order is rejected

  Scenario: entering a valid order is successfully
    Given a valid order
    When order is entered in to market
    Then order is successfully entered


