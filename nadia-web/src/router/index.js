import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/* Router Modules */

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: 'Dashboard', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/signup',
    component: () => import('@/views/login/signup'),
    hidden: true
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  {
    path: '/config',
    component: Layout,
    name: 'Config',
    meta: {
      title: 'Config',
      icon: 'example'
    },
    children: [
      {
        path: 'metadata',
        name: 'Metadata',
        component: () => import('@/views/config/metadata'),
        meta: { title: 'Metadata', icon: 'component' }
      },
      {
        path: 'configs',
        name: 'Configs',
        component: () => import('@/views/config/configs'),
        meta: { title: 'Configs', icon: 'skill' }
      },
      {
        path: 'allocate',
        name: 'Allocate Configs',
        component: () => import('@/views/config/allocate'),
        meta: { title: 'Allocate Configs', icon: 'tree' }
      }
    ]
  },
  {
    path: '/notification-center',
    name: 'Notification Center',
    component: Layout,
    meta: {
      title: 'Notification Center',
      icon: 'message'
    },
    children: [
      {
        path: 'operation',
        name: 'Operation Log',
        component: () => import('@/views/notification/operations'),
        meta: { title: 'Operation Log', icon: 'documentation' }
      },
      {
        path: 'task',
        name: 'Task',
        component: () => import('@/views/notification/task'),
        meta: { title: 'Task', icon: 'edit' }
      },
      {
        path: 'clientLog',
        name: 'Client Log',
        component: () => import('@/views/notification/clientLog'),
        meta: { title: 'Client Log', icon: 'list' }
      }
    ]
  },
  {
    path: '/user',
    name: 'User',
    component: Layout,
    meta: {
      title: 'User',
      icon: 'peoples'
    },
    children: [
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/users/role'),
        meta: {
          title: 'Role',
          icon: 'tree-table',
          affix: true
        }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/users/user'),
        meta: { title: 'Users', icon: 'user' }
      }
    ]
  }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
