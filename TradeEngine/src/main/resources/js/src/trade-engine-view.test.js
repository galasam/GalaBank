import React from 'react';
import ReactDOM from 'react-dom';
import {act} from 'react-dom/test-utils'
import TradeEngineView from "./trade-engine-view";
import $ from 'jquery';
import get_status from './rest';

let container;

beforeEach(() => {
  container = document.createElement('div');
  document.body.appendChild(container);
});

afterEach(() => {
  document.body.removeChild(container);
  container = null;
});

jest.mock("./rest");

it("displays fetching status until rest call returns", () => {
  //Given: never returning rest call
  get_status.mockImplementation(() => $.Deferred().promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: message is shown
  const message = container.querySelector('div').textContent
  expect(message).toBe("Fetching Trade Engine Status")
});

it("displays failure message on failed request", () => {

  //Given: failing rest call
  let fail_ret = {
    responseJSON: {
      error: "you are a fail"
    }
  }

  get_status.mockImplementation(() => $.Deferred().reject(fail_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: message is shown
  const message = container.querySelector('div').textContent
  expect(message).toBe("Could not fetch Trade Engine Status: " + fail_ret.responseJSON.error)
});

it("displays empty trades message on no trades", () => {

  //Given: rest call with empty trade list
  let success_ret = {
    trades: []
  }

  get_status.mockImplementation(() => $.Deferred().resolve(success_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: trade message is shown
  const message = container.querySelector('div').querySelectorAll('div')[1].textContent;
  expect(message).toBe("No trades yet.")
});

it("displays empty order message on no orders", () => {

  //Given: rest call with empty order list
  let success_ret = {
    orders: []
  }

  get_status.mockImplementation(() => $.Deferred().resolve(success_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: order message is shown
  const message = container.querySelector('div').querySelectorAll('div')[0].textContent;
  expect(message).toBe("No Orders yet.")
});

it("displays a trade correctly", () => {
  //Given: a standard trade
  const trade_input = {
    "buyOrder": 42,
    "sellOrder": 999,
    "matchPrice": 1.0,
    "matchQuantity": 64,
    "ticker": "Greggs"
  }

  let success_ret = {
    trades: [trade_input]
  }

  get_status.mockImplementation(() => $.Deferred().resolve(success_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: trade is shown correctly
  const trade_view = container.querySelector('div').querySelector('div.trade-list');
  const trade_header = trade_view.querySelector('h2').textContent
  expect(trade_header).toBe("Trades")
  const trades = trade_view.querySelectorAll('li.trade')
  expect(trades.length).toBe(1)
  const trade = trades[0].textContent

  expect(trade).toBe(`client #${trade_input.buyOrder} sold to client #${trade_input.sellOrder}: ${trade_input.matchQuantity} shares of ${trade_input.ticker} at ${trade_input.matchPrice}`)
});

it("displays a ticker correctly", () => {
  //Given: a standard ticker

  const buy_order_input = {
    "clientId": 1,
    "direction": "BUY",
    "orderId": 1,
    "quantity": 1,
    "quantityRemaining": 1,
    "ticker": "Greggs",
    "timeInForce": "FOK",
  }

  const sell_order_input = {
    "clientId": 0,
    "direction": "SELL",
    "orderId": 0,
    "quantity": 2,
    "quantityRemaining": 2,
    "ticker": "Greggs",
    "timeInForce": "FOK",
  }

  const ticker_input = {
    name: "Greggs",
    buy: [buy_order_input],
    sell: [sell_order_input]
  }

  let success_ret = {
    orders: [ticker_input]
  }

  get_status.mockImplementation(() => $.Deferred().resolve(success_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: ticker headings are shown correctly
  const order_view = container.querySelector('div').querySelector('div.order-view');
  const order_header = order_view.querySelector('h2').textContent
  expect(order_header).toBe("Unmatched Orders")
  const tickers = order_view.querySelectorAll('div.ticker')
  expect(tickers.length).toBe(1)
  const ticker = tickers[0]
  const ticker_name = ticker.querySelector('h3').textContent
  expect(ticker_name).toBe(ticker_input.name)
  const order_lists = ticker.querySelector('div').querySelectorAll('div')
  const buys = order_lists[0]
  const sells = order_lists[1]
  const buys_title = buys.querySelector('h4').textContent
  expect(buys_title).toBe("Buy")
  const sells_title = sells.querySelector('h4').textContent
  expect(sells_title).toBe("Sell")
});

it("displays a market order correctly", () => {
  //Given: a standard ticker

  const order_input = {
    "clientId": 1,
    "direction": "BUY",
    "orderId": 1,
    "quantity": 1,
    "quantityRemaining": 1,
    "ticker": "Greggs",
    "timeInForce": "FOK",
  }

  const ticker_input = {
    name: "Greggs",
    buy: [order_input],
    sell: []
  }

  let success_ret = {
    orders: [ticker_input]
  }

  get_status.mockImplementation(() => $.Deferred().resolve(success_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: market order is shown correctly
  const order_view = container.querySelector('div').querySelector('div.order-view');
  const tickers = order_view.querySelectorAll('div.ticker')
  const ticker = tickers[0]
  const order_lists = ticker.querySelector('div').querySelectorAll('div')
  const buys = order_lists[0].querySelectorAll('li.order')
  expect(buys.length).toBe(1)
  const order = buys[0].textContent
  expect(order).toBe(`order #${order_input.orderId}: client #${order_input.clientId} made ${order_input.timeInForce} Market Order for ${order_input.quantity} shares of ${order_input.ticker} (${order_input.quantityRemaining} remaining)`)

});

it("displays a limit order correctly", () => {
  //Given: a standard ticker

  const order_input = {
    "clientId": 1,
    "direction": "BUY",
    "orderId": 1,
    "quantity": 1,
    "quantityRemaining": 1,
    "ticker": "Greggs",
    "timeInForce": "FOK",
    "limit": 1.1
  }

  const ticker_input = {
    name: "Greggs",
    buy: [order_input],
    sell: []
  }

  let success_ret = {
    orders: [ticker_input]
  }

  get_status.mockImplementation(() => $.Deferred().resolve(success_ret).promise())

  //When: page is loaded
  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  //Then: limit order is shown correctly
  const order_view = container.querySelector('div').querySelector('div.order-view');
  const tickers = order_view.querySelectorAll('div.ticker')
  const ticker = tickers[0]
  const order_lists = ticker.querySelector('div').querySelectorAll('div')
  const buys = order_lists[0].querySelectorAll('li.order')
  expect(buys.length).toBe(1)
  const order = buys[0].textContent
  expect(order).toBe(`order #${order_input.orderId}: client #${order_input.clientId} made ${order_input.timeInForce} Limit Order for ${order_input.quantity} shares of ${order_input.ticker} at ${order_input.limit} (${order_input.quantityRemaining} remaining)`)

});