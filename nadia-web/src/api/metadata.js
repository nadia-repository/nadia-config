import request from '@/utils/request'

export function applicationList() {
  return request({
    url: '/applications/grouped',
    method: 'get'
  })
}

export function metaList(applications) {
  return request({
    url: '/meta/list',
    method: 'post',
    data: { applications }
  })
}

export function addApplication(data) {
  return request({
    url: '/meta/application/add',
    method: 'post',
    data
  })
}

export function deleteApplication(data) {
  return request({
    url: '/meta/application/delete',
    method: 'post',
    data
  })
}

export function updateApplication(data) {
  return request({
    url: '/meta/application/update',
    method: 'post',
    data
  })
}

export function groupList(application) {
  return request({
    url: `/meta/group/list/${application}`,
    method: 'get'
  })
}

export function addGroup(data) {
  return request({
    url: '/meta/group/add',
    method: 'post',
    data
  })
}

export function instanceList(application, group) {
  return request({
    url: `/meta/instances/list/${application}/${group}`,
    method: 'get'
  })
}

export function instanceConfigsList(data) {
  return request({
    url: `/meta/instance/configs/list`,
    method: 'post',
    data
  })
}

export function compareGroupConfigs(data) {
  return request({
    url: `/meta/group/configs/compare`,
    method: 'post',
    data
  })
}

export function getgroupInstances(application) {
  return request({
    url: `/meta/groups/instances/list/${application}`,
    method: 'get'
  })
}
export function switchInstances(data) {
  return request({
    url: `/meta/groups/instances/switch`,
    method: 'post',
    data
  })
}

