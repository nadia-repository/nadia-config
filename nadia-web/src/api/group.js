import request from '@/utils/request'

export function getGroups(applicationId) {
  return request({
    url: `/${applicationId}/groups`,
    method: 'post',
    data: {
      applicationId: applicationId
    }
  })
}
