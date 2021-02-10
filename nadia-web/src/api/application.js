import request from '@/utils/request'

export function getApplications() {
  return request({
    url: '/applications/grouped',
    method: 'get'
  })
}
