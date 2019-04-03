
import $ from 'jquery'

export default function get_status() {
  return $.ajax("http://localhost:2222/status", {
      method: "GET",
      contentType: "application/json"
    })
}