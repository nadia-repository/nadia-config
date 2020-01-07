import store from '@/store'

/**
 * @param {Array} value
 * @returns {Boolean}
 * @example see @/views/permission/directive.vue
 */
export default function checkButtonPermission(menu, button) {
  const buttons = store.getters && store.getters.buttons
  var buttonlist = buttons[menu]
  if (Array.isArray(buttonlist) && buttonlist.includes(button)) {
    return true
  }
  return false
}
