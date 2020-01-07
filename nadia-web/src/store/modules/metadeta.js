import { applicationList } from '@/api/metadata'

const actions = {
  applicationList({ commit }) {
    return new Promise((resolve, reject) => {
      applicationList().then(response => {
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  }
}

export default {
  namespaced: true,
  actions
}
