import request from '@/utils/request'

const URL = 'op/user'
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

export function addUser(form) {
  return postJson({
    url: URL + '/add',
    method: 'post',
    data: form
  })
}

export function getAllUser() {
  return request({
    url: URL,
    method: 'get'
  })
}

export function getUserCount() {
  return request({
    url: URL + '/count',
    method: 'get'
  })
}

export function getUserById(userId) {
  return request({
    url: URL + '/' + userId,
    method: 'get'
  })
}

export function updateUser(data) {
  return postJson({
    url: URL + '/update',
    method: 'post',
    data: data
  })
}

export function delUser(id) {
  return request({
    url: URL + '/' + id,
    method: 'delete'
  })
}

