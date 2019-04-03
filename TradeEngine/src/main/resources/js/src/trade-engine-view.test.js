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