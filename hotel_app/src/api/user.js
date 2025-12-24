import request from '@/utils/request'

const URL = 'user'
const jsonRequestConfig = {
  headers: {
    'Content-Type': 'application/json'
  },
  transformRequest: [function (data) {
    return JSON.stringify(data)
  }]
}

function postJson(config) {
  return request(Object.assign({}, jsonRequestConfig, config))
}

export function userLogin(username, password) {
  return postJson({
    url: 'login/user',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

export function userRegister(data) {
  return postJson({
    url: 'register/user',
    method: 'post',
    data: data
  })
}

export function getUserInfoByUsername(username) {
  return request({
    url: URL + '/withUsername',
    method: 'post',
    data: {
      username
    }
  })
}

export function getUserInfo() {
  return request({
    url: URL + '/profile',
    method: 'get'
  })
}

export function getUserInfoById(id) {
  return request({
    url: URL + '/withId',
    method: 'post',
    data: {
      userId: id
    }
  })
}

