import request from '@/utils/request'

const URL = 'login'
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

export function login(data) {
  return postJson({
    url: URL + '/admin',
    method: 'post',
    data: data
  })
}

export function getInfo(token, username) {
  return request({
    url: URL + '/info',
    method: 'post',
    data: {
      token,
      username
    }
  })
}

export function getByUsername(val) {
  return request({
    url: URL + '/withUsername',
    method: 'post',
    data: {
      username: val
    }
  })
}

export function logout() {
  return request({
    url: URL + '/logout',
    method: 'post'
  })
}
