class OrderForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {value: '', canSubmit: false};
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event) {
    this.setState({
      value: event.target.value,
      canSubmit: event.target.value != ""
    });
  }

  handleSubmit(event) {
    $.ajax("http://localhost:3333/enter-order", {
        method: "POST",
        dataType: "json",
        contentType: "text/plain",
        processData: false,
        data: this.state.value,
    }).done( (ret) =>
        console.log(ret)
    ).fail( (ret) =>
        console.log(ret)
    );
    alert('Order Definition Was Submitted: ' + this.state.value);
    event.preventDefault();
  }

  render() {
    return (
        <form onSubmit={this.handleSubmit}>
          <textarea class="form-control" value={this.state.value} onChange={this.handleChange} ></textarea>
          <input type="submit" class="form-control" value="Enter Order" disabled={!this.state.canSubmit}/>
        </form>
    );
  }

}

ReactDOM.render(
  <OrderForm />,
  document.getElementById('root')
);
