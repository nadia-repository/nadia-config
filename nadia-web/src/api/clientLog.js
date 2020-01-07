import request from '@/utils/request'

export function getClientLog(data) {
  return request({
    url: '/client/logs',
    method: 'post',
    data: data
  })
}
