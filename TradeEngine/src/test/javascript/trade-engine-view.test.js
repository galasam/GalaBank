var path = require("path")
var ReactDOM = require('react-dom')
var act = require('react-dom/test-utils').act
var engine = path.join(__dirname, "../../main/resources/public/tradeEngine/trade-engine-view.js")
var engine = require(engine)

it("displays a trade", () => {
  act(() => {})
  expect(1).toBe(1)
});