<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select v-model="listQuery.status" placeholder="Status" clearable style="width: 120px" class="filter-item">
        <el-option v-for="item in statusOptions" :key="item.name" :label="item.name" :value="item.name" />
      </el-select>
      <el-input v-model="listQuery.name" placeholder="Name" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-input v-model="listQuery.mail" placeholder="Mail" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        Search
      </el-button>
      <el-button v-waves class="filter-item" type="info" icon="el-icon-search" @click="handleClear">
        Clear
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column label="Name" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Mail" align="center">
        <template slot-scope="{row}">
          <span>{{ row.mail }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Modifier">
        <template slot-scope="{row}">
          <span>{{ row.updatedBy }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Modify Time">
        <template slot-scope="{row}">
          <span>{{ row.updatedAt }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Status" class-name="status-col">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusFilter">
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Roles">
        <template slot-scope="{row}">
          <span>{{ row.roles }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Actions" align="center" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button v-if="(row.status == 'submitted' || row.status == 'published') && checkButtonPermission('Users','Edit')" type="primary" size="small" @click="handleAddConfig(row)">
            Edit
          </el-button>
          <el-button v-if="(row.status == 'submitted' || row.status == 'published') && checkButtonPermission('Users','Delete')" type="danger" size="small" @click="deleteUser(row)">
            Delete
          </el-button>
          <el-button v-if="row.status == 'deleted' && checkButtonPermission('Users','Activate')" type="success" size="small" @click="activateUser(row)">
            activate
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-dialog title="Add Role" :visible.sync="dialogAddRoleVisible" @close="closeDialogAddRole">
      <div class="components-container">
        <el-drag-select v-model="roleValue" style="width:500px;" multiple placeholder="Please choose roles">
          <el-option v-for="item in roles" :key="item.value" :label="item.label" :value="item.value" />
        </el-drag-select>

        <div style="margin-top:30px;">
          <el-tag v-for="item of roleValue" :key="item" style="margin-right:15px;">
            {{ item }}
          </el-tag>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogAddRoleVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="updateUserRole()">
          Confirm
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { list, statuslist, deleteUser, activateUser } from '@/api/user'
import { getRoles, getUserRoles, updateUserRole } from '@/api/role'
import waves from '@/directive/waves' // waves directive
import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import ElDragSelect from '@/components/DragSelect' // base on element-ui
import checkButtonPermission from '@/utils/button'

export default {
  name: 'ComplexTable',
  components: { Pagination, ElDragSelect },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        submitted: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      roleValue: [],
      roles: [],
      rowUserName: '',
      statusOptions: null,
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      listQuery: {
        page: 1,
        limit: 20,
        status: '',
        name: '',
        mail: ''
      },
      dialogAddRoleVisible: false
    }
  },
  created() {
    this.getList()
    this.getStatusList()
  },
  methods: {
    checkButtonPermission,
    getList() {
      this.listLoading = true
      list({ status: this.listQuery.status, name: this.listQuery.name, mail: this.listQuery.mail }).then(response => {
        this.list = response.data
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
    },
    getStatusList() {
      statuslist().then(response => {
        this.statusOptions = response.data
      })
    },
    getRoleList() {
      getRoles().then(response => {
        var list = response.data
        if (Array.isArray(list) && list.length) {
          for (const item of list) {
            this.roles.push({ value: item.name, label: item.name })
          }
        }
      })
    },
    getUserRoles() {
      getUserRoles(this.rowUserName).then(response => {
        var list = response.data
        if (Array.isArray(list) && list.length) {
          for (const item of list) {
            this.roleValue.push(item.name)
          }
        }
      })
    },
    updateUserRole() {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        updateUserRole({ roles: this.roleValue, userName: this.rowUserName }).then(() => {
          this.dialogAddRoleVisible = false
          this.getList()
          this.$notify({
            title: 'Success',
            message: 'Updated Successfully',
            type: 'success',
            duration: 2000
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Has been cancel'
        })
      })
    },
    deleteUser(row) {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        deleteUser(row.name).then(() => {
          this.getList()
          this.$notify({
            title: 'Success',
            message: 'Deleted Successfully',
            type: 'success',
            duration: 2000
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Has been cancel'
        })
      })
    },
    activateUser(row) {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        activateUser(row.name).then(() => {
          this.getList()
          this.$notify({
            title: 'Success',
            message: 'Activated Successfully',
            type: 'success',
            duration: 2000
          })
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Has been cancel'
        })
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    sortChange(data) {
      const { prop, order } = data
      if (prop === 'id') {
        this.sortByID(order)
      }
    },
    sortByID(order) {
      if (order === 'ascending') {
        this.listQuery.sort = '+id'
      } else {
        this.listQuery.sort = '-id'
      }
      this.handleFilter()
    },
    handleAddConfig(row) {
      this.dialogAddRoleVisible = true
      this.rowUserName = row.name
      this.getRoleList()
      this.getUserRoles()
    },
    handleDelete(row) {
      this.$notify({
        title: 'Success',
        message: 'Delete Successfully',
        type: 'success',
        duration: 2000
      })
      const index = this.list.indexOf(row)
      this.list.splice(index, 1)
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    closeDialogAddRole() {
      this.roleValue = []
      this.roles = []
      this.rowUserName = ''
    },
    handleClear() {
      this.listQuery.status = ''
      this.listQuery.mail = ''
      this.listQuery.name = ''
    }
  }
}
</script>
