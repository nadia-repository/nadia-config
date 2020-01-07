import request from '@/utils/request'

export function getRoutes() {
  return request({
    url: '/routes',
    method: 'get'
  })
}

export function getRoles() {
  return request({
    url: '/role/list',
    method: 'get'
  })
}

export function getUserRoles(userName) {
  return request({
    url: `/role/user/role/list/${userName}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/role/add',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: `/role/update`,
    method: 'post',
    data
  })
}

export function deleteRole(data) {
  return request({
    url: `/role/delete`,
    method: 'post',
    data
  })
}

export function getRoleConfigs(role) {
  return request({
    url: `/role/config/list/${role}`,
    method: 'get'
  })
}

export function getAllConfigs(role) {
  return request({
    url: `/role/config/configs/${role}`,
    method: 'get'
  })
}

export function updateRoleConfigs(data) {
  return request({
    url: `/role/config/update`,
    method: 'post',
    data
  })
}

export function updateUserRole(data) {
  return request({
    url: `/role/user/role/update`,
    method: 'post',
    data
  })
}

export function getApprovers(data) {
  return request({
    url: `/role/approver`,
    method: 'post',
    data
  })
}
