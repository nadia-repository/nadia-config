import { asyncRoutes, constantRoutes } from '@/router'
import { getRoleMenuButtons } from '@/api/system'

/**
 * Filter asynchronous routing tables by recursion
 * @param routes asyncRoutes
 * @param menus
 */
export function filterAsyncRoutes(routes, menus) {
  const res = []
  if (!Array.isArray(menus)) {
    return res
  }
  routes.forEach(route => {
    const tmp = { ...route }
    if (menus.includes(tmp.name)) {
      if (tmp.children) {
        tmp.children = filterAsyncRoutes(tmp.children, menus)
      }
      res.push(tmp)
    }
  })

  return res
}

const state = {
  routes: [],
  addRoutes: [],
  buttons: null
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.addRoutes = routes
    state.routes = constantRoutes.concat(routes)
  },
  SET_BUTTON: (state, buttons) => {
    state.buttons = buttons
  }
}

const actions = {
  generateRoutes({ commit }, roles) {
    return new Promise((resolve, reject) => {
      getRoleMenuButtons({ roles: roles }).then(response => {
        commit('SET_BUTTON', response.data.buttons)
        const accessedRoutes = filterAsyncRoutes(asyncRoutes, response.data.menus)
        commit('SET_ROUTES', accessedRoutes)
        resolve(accessedRoutes)
      }).catch(error => {
        reject(error)
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
