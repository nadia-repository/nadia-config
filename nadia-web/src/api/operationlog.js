import request from '@/utils/request'

export function getOperationLog(data) {
  return request({
    url: '/operationlog/get',
    method: 'post',
    data: data
  })
}

export function clearLog(type) {
  return request({
    url: '/operationlog/clear',
    method: 'post',
    data: { type }
  })
}
