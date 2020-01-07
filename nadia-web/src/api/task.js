import request from '@/utils/request'

export function getTask(data) {
  return request({
    // url: '/task/get',
    url: '/task/get',
    method: 'post',
    data: data
  })
}

export function getProcessingTask(data) {
  return request({
    url: '/processingtask/get',
    method: 'post',
    data: data
  })
}

export function approve(data) {
  return request({
    url: '/task/approve',
    method: 'post',
    data: data
  })
}

export function getHistoryTask(data) {
  return request({
    url: '/historytask/get',
    method: 'post',
    data: data
  })
}
