import request from '@/utils/request'

export function getCounts() {
  return request({
    url: '/admin/counts',
    method: 'get'
  })
}

export function getErrors() {
  return request({
    url: '/admin/error',
    method: 'get'
  })
}

