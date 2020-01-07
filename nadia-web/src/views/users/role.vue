<template>
  <div class="app-container">
    <el-button v-if="checkButtonPermission('Role','Add')" type="primary" @click="handleAddRole">New Role</el-button>

    <el-table :data="rolesList" style="width: 100%;margin-top:30px;" border>
      <el-table-column align="center" label="Role Name" width="220">
        <template slot-scope="scope">
          {{ scope.row.name }}
        </template>
      </el-table-column>
      <el-table-column align="header-center" label="Description">
        <template slot-scope="scope">
          {{ scope.row.description }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="Approver Name" width="220">
        <template slot-scope="scope">
          {{ scope.row.approvers }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="Operations">
        <template slot-scope="scope">
          <el-button v-if="checkButtonPermission('Role','Edit')" type="primary" size="small" @click="handleEdit(scope)">Edit</el-button>
          <el-button v-if="checkButtonPermission('Role','Delete')" type="danger" size="small" @click="handleDelete(scope)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogVisible" :title="dialogType==='edit'?'Edit Role':'New Role'" @close="closeDialog">
      <el-form :model="role" label-width="80px" label-position="left">
        <el-form-item label="Name">
          <el-input v-model="role.name" placeholder="Role Name" :disabled="dialogType==='edit'?true:false" />
        </el-form-item>
        <el-form-item label="Desc">
          <el-input
            v-model="role.description"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Role Description"
          />
        </el-form-item>
        <el-form-item label="Approver">
          <el-select v-model="approvers" placeholder="Approver" multiple clearable>
            <el-option v-for="item in approverOptions" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="Menus">
          <el-tree
            ref="tree"
            :data="routes"
            :props="defaultProps"
            show-checkbox
            node-key="key"
            class="permission-tree"
            default-expand-all
            check-strictly
          />
        </el-form-item>
      </el-form>
      <div style="text-align:right;">
        <el-button type="danger" @click="dialogVisible=false">Cancel</el-button>
        <el-button type="primary" @click="confirmRole">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { deepClone } from '@/utils'
import { getApprovers, getRoles, addRole, deleteRole, updateRole } from '@/api/role'
import { getRoutes, getRoleRoutes } from '@/api/system'
import checkButtonPermission from '@/utils/button'

export default {
  data() {
    return {
      approvers: [],
      approverOptions: [],
      role: {
        name: '',
        description: ''
      },
      routes: [],
      rolesList: [],
      dialogVisible: false,
      dialogType: 'new',
      checkStrictly: false,
      defaultProps: {
        children: 'children',
        label: 'title',
        type: 'type',
        menuId: 'menuId',
        buttonId: 'buttonId',
        key: 'key'
      }
    }
  },
  created() {
    // Mock: get all routes and roles list from server
    this.getRoles()
  },
  methods: {
    checkButtonPermission,
    getRoutes() {
      getRoutes().then(response => {
        this.routes = response.data
      })
    },
    getRoleRoutes(role) {
      getRoleRoutes(role).then(response => {
        this.$refs.tree.setCheckedKeys(response.data)
      })
    },
    async getRoles() {
      const res = await getRoles()
      this.rolesList = res.data
    },
    async getApprovers(role) {
      const res = await getApprovers({ roleName: role })
      if (role !== '') {
        this.approvers = res.data.approvers
      }
      this.approverOptions = res.data.approverOptions
    },
    handleAddRole() {
      if (this.$refs.tree) {
        this.$refs.tree.setCheckedNodes([])
      }
      this.getRoutes()
      this.getApprovers(this.role.name)
      this.dialogType = 'new'
      this.dialogVisible = true
    },
    handleEdit(scope) {
      this.dialogType = 'edit'
      this.dialogVisible = true
      this.checkStrictly = true
      this.role = deepClone(scope.row)
      this.getApprovers(this.role.name)
      this.getRoutes()
      this.getRoleRoutes(this.role.name)
    },
    handleDelete({ $index, row }) {
      this.$confirm('Confirm to remove the role?', 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      })
        .then(async() => {
          await deleteRole({ name: row.name })
          this.getRoles()
          this.$message({
            type: 'success',
            message: 'Delete succed!'
          })
        })
        .catch(err => { console.error(err) })
    },
    resetData() {
      this.approvers = []
      this.role.name = ''
      this.role.description = ''
      this.routes = []
    },
    closeDialog() {
      this.resetData()
    },
    async confirmRole() {
      const isEdit = this.dialogType === 'edit'
      var checkNode = this.$refs.tree.getCheckedNodes(false, true)
      const data = {
        name: '',
        description: '',
        approvers: [],
        routes: []
      }
      data.name = this.role.name
      data.description = this.role.description
      data.approvers = this.approvers
      data.routes = checkNode
      if (isEdit) {
        await updateRole(data)
      } else {
        await addRole(data)
      }
      this.getRoles()
      this.dialogVisible = false
      this.$notify({
        title: 'Success',
        message: 'Successfully',
        type: 'success',
        duration: 2000
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.app-container {
  .roles-table {
    margin-top: 30px;
  }
  .permission-tree {
    margin-bottom: 30px;
  }
}
</style>
