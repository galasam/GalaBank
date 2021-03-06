
import React from 'react';
import $ from 'jquery';
import get_status from './rest';

class Order extends React.Component {
  render() {
    let order=this.props.order
    if (order.limit == null) {
      return (
        <li className="order market" >order #{order.orderId}: client #{order.clientId} made {order.timeInForce} Market Order for {order.quantity} shares of {order.ticker} ({order.quantityRemaining} remaining)</li>
      );
    } else {
      return (
        <li className="order limit" >order #{order.orderId}: client #{order.clientId} made {order.timeInForce} Limit Order for {order.quantity} shares of {order.ticker} at {order.limit} ({order.quantityRemaining} remaining)</li>
      );
    }
  }
}

class OrderList extends React.Component {
  render() {
    if (this.props.orders.length == 0) {
      return null
    } else {
      return (
        <div className="order-list">
          <h4>{this.props.direction}</h4>
          <ul>
            {this.props.orders.map((order) => <Order key={order.orderId} order={order} />)}
          </ul>
        </div>
        );
    }
  }
}

class OrderLists extends React.Component {
  render() {
    return (
      <div className="order-lists">
        <OrderList direction={"Buy"} orders={this.props.buy} />
        <OrderList direction={"Sell"} orders={this.props.sell} />
      </div>
      );
  }
}

class Ticker extends React.Component {
  render() {
    return (
      <div className="ticker">
        <h3>{this.props.ticker.name}</h3>
        <OrderLists buy={this.props.ticker.buy} sell={this.props.ticker.sell} />
      </div>
      );
  }
}

class OrderView extends React.Component {
  render() {
    if (this.props.tickers == null || this.props.tickers.length == 0) {
      return (<div className="positive-feedback">No Orders yet.</div>)
    } else {
      return (
        <div className="order-view">
          <h2>Unmatched Orders</h2>
          {this.props.tickers.map((ticker) =>
            <Ticker key={ticker.name} ticker={ticker} />)}
        </div>
      )
    }
  }
}

class Trade extends React.Component {
  render() {
    let trade=this.props.trade
    return (
      <li className="trade">client #{trade.buyOrder} sold to client #{trade.sellOrder}: {trade.matchQuantity} shares of {trade.ticker} at {trade.matchPrice}</li>
      );
  }
}

class TradeList extends React.Component {
  render() {
    if (this.props.trades == null || this.props.trades.length == 0) {
      return (<div className="positive-feedback">No trades yet.</div>)
    } else {
      return (
        <div className="trade-list">
          <h2>Trades</h2>
          {this.props.trades.map((trade) => <Trade key={trade.buyOrder} trade={trade} />)}
        </div>
        );
    }
  }
}

class TradeEngineView extends React.Component {

  constructor(props) {
    super(props)
    this.state = {
      message: {
        type: "info",
        text: "Fetching Trade Engine Status"
      }
    }
  }

  componentDidMount() {
    get_status()
      .done( (ret) => {
        this.setState({
          status: ret,
          message: null
        })
      }).fail( (ret) => {
        this.setState({
          message: {
            type: "error",
            text: ret.responseJSON.error
          }
        })
      });
  }

  render() {
    let message = this.state.message
    if (message == null) {
      return (
        <div>
          <OrderView tickers={this.state.status.orders} />
          <TradeList trades={this.state.status.trades} />
        </div>
      );
    } else {
      if (message.type == "error") {
        return (
          <div className="negative-feedback">Could not fetch Trade Engine Status: {message.text}</div>
        );
      } else if (message.type == "info") {
        return (
          <div className="positive-feedback">{message.text}</div>
        );
      }

    }
  }
}

export default TradeEngineView;