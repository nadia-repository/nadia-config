import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

export function signup(data) {
  return request({
    url: '/user/signup',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/user/info',
    method: 'post',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
}

export function list(data) {
  return request({
    url: '/user/list',
    method: 'post',
    data
  })
}

export function statuslist() {
  return request({
    url: '/user/status/list',
    method: 'get'
  })
}

export function deleteUser(userName) {
  return request({
    url: `/user/delete/${userName}`,
    method: 'get'
  })
}

export function activateUser(userName) {
  return request({
    url: `/user/activate/${userName}`,
    method: 'get'
  })
}
