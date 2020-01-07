--menu
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('1', '0', 'Config', 'config', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('2', '1', 'Metadata', 'metadata', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('3', '1', 'Configs', 'configs', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('4', '1', 'Allocate Configs', 'allocate', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('5', '0', 'Notification Center', 'notification-center', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('6', '5', 'Operation Log', 'operation', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('7', '5', 'Task', 'task', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('8', '0', 'User', 'user', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('9', '8', 'Role', 'role', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('10', '8', 'Users', 'user', '');
INSERT INTO `menu` (`id`, `parent_id`, `name`, `path`, `icon`) VALUES ('11', '5', 'Client Log', 'clientLog', '');

--button
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('1', '2', 'Add', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('2', '2', 'Delete', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('3', '2', 'Edit', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('4', '2', 'Group', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('5', '4', 'Allocate', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('6', '9', 'Add', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('7', '9', 'Edit', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('8', '9', 'Delete', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('9', '10', 'Edit', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('10', '10', 'Delete', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('11', '10', 'Activate', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('12', '7', 'Approve', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('13', '7', 'Reject', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('14', '6', 'Clean', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('15', '2', 'Instance', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('16', '3', 'Add', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('17', '3', 'Edit', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('18', '3', 'Delete', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('19', '3', 'Import', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('20', '3', 'Export', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('21', '3', 'Publish', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('22', '3', 'Instance', '');
INSERT INTO `menu_button` (`id`, `menu_id`, `name`, `icon`) VALUES ('23', '3', 'History', '');

--role
INSERT INTO `role` (`id`, `name`, `description`) VALUES ('1', 'admin', 'admin');

--role_menu
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('1', '1', '1', '2020-01-03 18:15:33', 'shixiang', '2020-01-03 18:15:33', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('2', '1', '9', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('3', '1', '8', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('4', '1', '11', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('5', '1', '7', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('6', '1', '6', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('7', '1', '5', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('8', '1', '4', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('9', '1', '3', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('10', '1', '2', '2020-01-03 18:15:33', 'shixiang', '2020-01-03 18:15:33', 'shixiang');
INSERT INTO `role_menu` (`id`, `role_id`, `menu_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('11', '1', '10', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');

--role_menu_button
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('1', '10', '1', '2020-01-03 18:15:33', 'shixiang', '2020-01-03 18:15:33', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('2', '10', '2', '2020-01-03 18:15:33', 'shixiang', '2020-01-03 18:15:33', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('3', '10', '3', '2020-01-03 18:15:33', 'shixiang', '2020-01-03 18:15:33', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('4', '10', '4', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('5', '10', '15', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('6', '9', '16', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('7', '9', '17', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('8', '9', '18', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('9', '9', '19', '2020-01-03 18:15:34', 'shixiang', '2020-01-03 18:15:34', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('10', '9', '20', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('11', '9', '21', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('12', '9', '22', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('13', '9', '23', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('14', '8', '5', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('15', '6', '14', '2020-01-03 18:15:35', 'shixiang', '2020-01-03 18:15:35', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('16', '5', '12', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('17', '5', '13', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('18', '2', '6', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('19', '2', '7', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('20', '2', '8', '2020-01-03 18:15:36', 'shixiang', '2020-01-03 18:15:36', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('21', '11', '9', '2020-01-03 18:15:37', 'shixiang', '2020-01-03 18:15:37', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('22', '11', '10', '2020-01-03 18:15:37', 'shixiang', '2020-01-03 18:15:37', 'shixiang');
INSERT INTO `role_menu_button` (`id`, `role_menu_id`, `button_id`, `created_at`, `created_by`, `updated_at`, `updated_by`) VALUES ('23', '11', '11', '2020-01-03 18:15:37', 'shixiang', '2020-01-03 18:15:37', 'shixiang');

--user
INSERT INTO `user` (`id`, `name`, `password`, `email`, `status`) VALUES ('1', 'admin', '111111', '', 'published');

--user_role
INSERT INTO `user_role` (`id`, `user_id`, `role_id`) VALUES ('1', '1', '1');
