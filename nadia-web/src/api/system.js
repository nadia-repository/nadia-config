import request from '@/utils/request'

export function getRoleMenuButtons(data) {
  return request({
    url: '/system/menu/button',
    method: 'post',
    data
  })
}

export function getRoutes() {
  return request({
    url: '/system/route/list',
    method: 'get'
  })
}

export function getRoleRoutes(role) {
  return request({
    url: `/system/route/${role}`,
    method: 'get'
  })
}
