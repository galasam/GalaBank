import React from 'react';
import ReactDOM from 'react-dom';
import {act} from 'react-dom/test-utils'
import TradeEngineView from "./trade-engine-view";

let container;

beforeEach(() => {
  container = document.createElement('div');
  document.body.appendChild(container);
});

afterEach(() => {
  document.body.removeChild(container);
  container = null;
});

it("displays a trade", () => {

  act(() => {
    ReactDOM.render(<TradeEngineView />, container);
  })

  const message = container.querySelector('div').textContent
  expect(message).toBe("Fetching Trade Engine Status")
});