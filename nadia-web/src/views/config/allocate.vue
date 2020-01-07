<template>
  <div class="app-container">
    <div class="filter-container">

      <el-select v-model="postForm.role" :method="getRoles" placeholder="Role">
        <el-option v-for="item in rolesOptions" :key="item.name" :label="item.name" :value="item.name" />
      </el-select>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        Search
      </el-button>
      <el-button v-if="checkButtonPermission('Allocate Configs','Allocate')" v-waves class="filter-item" type="primary" icon="el-icon-edit-outline" @click="handleAddConfig">
        Allocate
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column label="Application" align="center">
        <template slot-scope="{row}">
          <span>{{ row.application }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Group">
        <template slot-scope="{row}">
          <span>{{ row.group }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Config Name">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Config Key">
        <template slot-scope="{row}">
          <span>{{ row.key }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" />

    <el-dialog title="Add Config" :visible.sync="dialogAddConfigVisible" @close="closeDialogAddConfig">
      <el-form ref="dataForm" label-position="left" label-width="90px" style="width: 400px; margin-left:50px;">
        <el-form-item label="Configs">
          <el-tree
            ref="tree"
            :data="allConfigs"
            :props="defaultProps"
            show-checkbox
            node-key="id"
            class="permission-tree"
            default-expand-all
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogAddConfigVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="handleRoleConfigs()">
          Confirm
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getRoles, getRoleConfigs, getAllConfigs, updateRoleConfigs } from '@/api/role'
import waves from '@/directive/waves' // waves directive
import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import checkButtonPermission from '@/utils/button'

const defaultForm = {
  role: ''
}

export default {
  name: 'ComplexTable',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      rolesOptions: null,
      defaultProps: {
        children: 'children',
        label: 'title',
        id: 'id',
        name: 'name'
      },
      allConfigs: null,
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: false,
      listQuery: {
        page: 1,
        limit: 20,
        importance: undefined,
        title: undefined,
        type: undefined,
        sort: '+name'
      },
      sortOptions: [{ label: 'ID Ascending', key: '+id' }, { label: 'ID Descending', key: '-id' }],
      dialogAddConfigVisible: false,
      multipleSelection: []
    }
  },
  created() {
    this.getRoles()
  },
  methods: {
    checkButtonPermission,
    routesData() {
      return this.routes
    },
    getList() {
      this.listLoading = true
      getRoleConfigs(this.postForm.role).then(response => {
        this.list = response.data
        // this.total = response.data.total

        // Just to simulate the time of the request
        setTimeout(() => {
          this.listLoading = false
        }, 1.5 * 1000)
      })
    },
    getRoles() {
      getRoles().then(response => {
        this.rolesOptions = response.data
      })
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    getAllConfigs() {
      getAllConfigs(this.postForm.role).then(response => {
        var list = response.data.data
        if (!Array.isArray(list) || !list.length) {
          return
        }
        var hasConfig = false
        var data = []
        var ids = []
        for (const application of list) {
          const groups = application.groups
          if (!Array.isArray(groups) || !groups.length) {
            continue
          }
          var gs = []
          for (const group of groups) {
            const configs = group.configs
            if (!Array.isArray(configs) || !configs.length) {
              continue
            }
            var cs = []
            for (const cg of configs) {
              const c = { title: cg.key, id: cg.id, configId: cg.configId }
              cs.push(c)
              hasConfig = true
              if (cg.checked) {
                ids.push(cg.id)
              }
            }
            const g = { title: group.name, id: group.id, children: cs }
            gs.push(g)
          }
          if (hasConfig) {
            const a = { title: application.name, id: application.id, children: gs }
            data.push(a)
          }
          hasConfig = false
        }
        this.$refs.tree.setCheckedKeys(ids)
        this.allConfigs = data
      })
    },
    handleAddConfig() {
      if (this.postForm.role === '' || this.postForm.role === null) {
        this.$message({
          type: 'error',
          message: 'Please choose 1 role'
        })
        return
      }
      this.dialogAddConfigVisible = true
      this.getAllConfigs()
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    closeDialogAddConfig() {
      this.allConfigs = null
    },
    handleRoleConfigs() {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.updateRoleConfigs()
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Has been cancel'
        })
      })
    },
    updateRoleConfigs() {
      var checkNode = this.$refs.tree.getCheckedNodes()
      var configIds = []
      for (const node of checkNode) {
        if (node.configId != null) {
          configIds.push(node.configId)
        }
      }
      if (Array.isArray(configIds) && configIds.length) {
        updateRoleConfigs({ role: this.postForm.role, configIds: configIds }).then(response => {
          this.dialogAddConfigVisible = false
          this.getList()
        })
      }
    }
  }
}
</script>
