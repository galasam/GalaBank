
class Trade extends React.Component {
  render() {
    let trade=this.props.trade
    return (
      <li>{trade.buyOrder} {trade.sellOrder} {trade.matchQuantity} {trade.matchPrice}</li>
      );
  }
}

class TradeList extends React.Component {
  render() {
    return (
      <div>
        {this.props.tradeList.map((trade) => <Trade key={trade.buyOrder} trade={trade} />)}
      </div>
      );
  }
}

class TradesView extends React.Component {

  constructor(props) {
    super(props)
    this.state = {
      message: {
        type: "info",
        text: "Fetching trades"
      }
    }
    $.ajax("http://localhost:2222/trades", {
          method: "GET",
          contentType: "application/json"
        }).done( (trades) => {
          console.log(trades)
          if (trades.length == 0) {
            this.setState({
              message: {
                type: "info",
                text: "No Trades available"
              }
            })
          } else {
            this.setState({
              trades: trades,
              message: null
            })
          }

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
        <TradeList tradeList={this.state.trades} />
      );
    } else {
      if (message.type == "error") {
        return (
          <span className="negative-feedback">Could not fetch trades: {message.text}</span>
        );
      } else if (message.type == "info") {
        return (
          <span className="positive-feedback">{message.text}</span>
        );
      }

    }
  }
}

ReactDOM.render(
  <TradesView />,
  document.getElementById('root')
);
