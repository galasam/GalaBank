
class OrderSystem extends React.Component {
  constructor(props) {
    super(props)
  }

  render() {
    return (
      <div>
        <OrderForm />
      </div>
    );
  }

}

class OrderForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        submitValue: '',
        canSubmit: false,
        feedback: {type: "none"}
      };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    this.setState({
      submitValue: event.target.value,
      canSubmit: event.target.value != ""
    });
  }

  handleSubmit(event) {
    this.setState({
      feedback: {type: "none"}
    })
    $.ajax("http://localhost:3333/enter-order", {
      method: "POST",
      dataType: "json",
      contentType: "text/plain",
      processData: false,
      data: this.state.submitValue,
    }).done( (ret) => {
      this.setState({
        feedback: {type: "positive"}
      })
    }).fail( (ret) => {
      this.setState({
        feedback: {type: "negative", text: ret.responseJSON.error}
      })
    });
    event.preventDefault();
  }

  render() {
    let feedback;
    if (this.state.feedback.type == "positive") {
      feedback = <span className="positive-feedback">Order Entered Successfully</span>
    } else if (this.state.feedback.type == "negative") {
      feedback = <span className="negative-feedback">{this.state.feedback.text}</span>
    } else {
      feedback = <span></span>;
    }
    return (
      <div>
        <form onSubmit={this.handleSubmit}>
          <textarea className="form-control" value={this.state.submitValue} onChange={this.handleChange} ></textarea>
          <input type="submit" className="form-control" value="Enter Order" disabled={!this.state.canSubmit}/>
        </form>
        {feedback}
      </div>
    );
  }

}

ReactDOM.render(
  <OrderSystem />,
  document.getElementById('root')
);
