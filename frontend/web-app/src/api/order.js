import request from '@/utils/request'

const URL = 'user/order'
const jsonRequestConfig = {
  headers: {
    'Content-Type': 'application/json'
  },
  transformRequest: [function(data) {
    return JSON.stringify(data)
  }]
}

function postJson(config) {
  return request(Object.assign({}, jsonRequestConfig, config))
}

function toDateOnly(value) {
  if (!value) {
    return value
  }
  const date = new Date(value)
  if (isNaN(date.getTime())) {
    return value
  }
  const month = `${date.getMonth() + 1}`.padStart(2, '0')
  const day = `${date.getDate()}`.padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

export function getOrders(userId) {
  return request({
    url: URL,
    method: 'get',
    params: {
      userId
    }
  })
}

export function addOrder(data) {
  const payload = Object.assign({}, data, {
    orderDate: toDateOnly(data.orderDate)
  })
  return postJson({
    url: URL + '/add',
    method: 'post',
    data: payload
  })
}

export function delOrder(orderId) {
  return request({
    url: `${URL}/${orderId}`,
    method: 'delete'
  })
}

export function cancelOrder(orderId) {
  return postJson({
    url: URL + '/cancel',
    method: 'post',
    data: {
      orderId
    }
  })
}

export function payOrder(orderId, username, password) {
  return postJson({
    url: URL + '/pay',
    method: 'post',
    data: {
      orderId,
      username,
      password
    }
  })
}

export function getOrderById(orderId) {
  return request({
    url: `${URL}/${orderId}`,
    method: 'get'
  })
}
