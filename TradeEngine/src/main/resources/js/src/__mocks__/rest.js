
import $ from 'jquery'

let fail_ret = {
  responseJSON: {
    error: "you are a fail"
  }
}

export default function get_status() {
  return $.Deferred().reject(fail_ret).promise()
}