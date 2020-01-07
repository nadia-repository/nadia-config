import request from '@/utils/request'

export function getConfigs(data) {
  return request({
    url: '/configs',
    method: 'post',
    data: data
  })
}

export function addConfig(data) {
  return request({
    url: '/config',
    method: 'post',
    data: data
  })
}

export function updateConfig(data) {
  return request({
    url: '/config',
    method: 'put',
    data: data
  })
}

export function deleteConfigs(data) {
  return request({
    url: '/config',
    method: 'delete',
    data: data
  })
}

export function getConfigHistories(id) {
  return request({
    url: `/config/${id}/histories`,
    method: 'get'
  })
}

export function getConfigInstances(id) {
  return request({
    url: `/config/${id}/instances`,
    method: 'get'
  })
}

export function publishConfig(id) {
  return request({
    url: `/config/${id}/publish`,
    method: 'put'
  })
}

export function exportConfig(data) {
  return request({
    url: '/config/export',
    method: 'post',
    data: data
  })
}

export function importConfig(data) {
  return request({
    url: '/config/import',
    method: 'post',
    data: data
  })
}
