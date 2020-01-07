<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="params.applicationId"
        placeholder="Application"
        clearable
        style="width: 120px"
        class="filter-item"
        @change="handleApplicationChange"
        @clear="handleApplicationChange"
      >
        <el-option v-for="item in applications" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="params.groupId" placeholder="Group" clearable style="width: 120px" class="filter-item">
        <el-option v-for="item in groups" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-input
        v-model="params.name"
        placeholder="Name"
        clearable
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-input
        v-model="params.key"
        placeholder="Key"
        clearable
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">Search</el-button>
      <el-button v-waves class="filter-item" type="info" icon="el-icon-error" @click="handleClean">Clean</el-button>
      <el-button
        v-if="checkButtonPermission('Configs','Add')"
        class="filter-item"
        style="margin-left: 10px;"
        type="success"
        icon="el-icon-edit"
        @click="handleAddConfig"
      >
        Add
      </el-button>
      <el-button
        v-if="checkButtonPermission('Configs','Delete')"
        v-waves
        :loading="loading"
        class="filter-item"
        type="danger"
        icon="el-icon-delete"
        @click="handleDelete"
      >
        Delete
      </el-button>
      <el-button
        v-if="checkButtonPermission('Configs','Export')"
        v-waves
        :loading="loading"
        class="filter-item"
        type="primary"
        icon="el-icon-download"
        @click="handleExport"
      >
        Export
      </el-button>
      <el-upload
        v-if="checkButtonPermission('Configs','Import')"
        ref="fileupload"
        action="#"
        accept=".csv"
        :auto-upload="true"
        :show-file-list="false"
        :before-upload="beforeUpload"
        style="display:inline-block"
      >
        <el-button v-waves :loading="loading" class="filter-item" type="primary" icon="el-icon-upload2">Import</el-button>
      </el-upload>
    </div>

    <el-table
      v-loading="loading"
      :data="list"
      border
      fit
      highlight-current-row
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column width="40px" type="selection" :selectable="checkIfCanBeSelected" @select="handleSelectionChange" />
      <el-table-column label="Config Name" width="120px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Key" width="120px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.key }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Value" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.value }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Description" align="center">
        <template slot-scope="{row}">
          <span>{{ row.description }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Modifier" width="100px">
        <template slot-scope="{row}">
          <span>{{ row.updatedBy }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Modify Time" width="160px">
        <template slot-scope="{row}">
          <span>{{ num2string(row.updatedAt,undefined) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Status" class-name="status-col" width="100">
        <template slot-scope="{row}">
          <el-tag :type="row.status | statusFilter">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Actions" align="center" width="250" class-name="small-padding fixed-width">
        <template slot-scope="{row}">
          <el-button
            v-if="checkButtonPermission('Configs','Edit')"
            v-show="row.status==='published'"
            type="primary"
            size="mini"
            @click="handleEditConfig(row)"
          >Edit</el-button>
          <el-button
            v-if="checkButtonPermission('Configs','Publish')"
            v-show="row.status==='approved'"
            type="primary"
            size="mini"
            @click="handlePublishConfig(row)"
          >Publish</el-button>
          <el-button
            v-if="checkButtonPermission('Configs','Instance')"
            size="mini"
            type="success"
            @click="handleInstanceConfig(row)"
          >Instance</el-button>
          <el-button
            v-if="checkButtonPermission('Configs','History')"
            size="mini"
            type="success"
            @click="handleHistoryConfig(row)"
          >History</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.page"
      :limit.sync="params.limit"
      @pagination="fetchConfigs"
    />

    <el-dialog title="Add Config" :visible.sync="dialogAddConfigVisible" @close="handleCancel('additionForm')">
      <el-form
        ref="additionForm"
        :rules="rules"
        :model="additionForm"
        label-position="left"
        label-width="100px"
        style="margin:0 50px;"
      >
        <el-form-item label="Name" required prop="name">
          <el-input
            v-model="additionForm.name"
            type="text"
            placeholder="Name"
            maxlength="64"
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Key" required prop="key">
          <el-input
            v-model="additionForm.key"
            type="text"
            placeholder="Key"
            maxlength="128"
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Value" required prop="value">
          <el-input
            v-model="additionForm.value"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Please input"
            maxlength="2024"
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Description" required prop="description">
          <el-input
            v-model="additionForm.description"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Please input"
            maxlength="128"
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Role" prop="roleIds">
          <el-select v-model="additionForm.roleIds" multiple placeholder="Please select">
            <el-option v-for="item in roles" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="loading" @click="handleCancel('additionForm')">Cancel</el-button>
        <el-button type="primary" :loading="loading" @click="handleAdditionSubmit">Confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog
      title="Edit Config"
      :visible.sync="dialogEditConfigVisible"
      @close="handleCancel('modificationForm')"
    >
      <el-form
        ref="modificationForm"
        :rules="rules"
        :model="modificationForm"
        label-position="left"
        label-width="100px"
        style="margin:0 50px;"
      >
        <el-form-item label="Name" required prop="name">
          <el-input
            v-model="modificationForm.name"
            type="text"
            maxlength="64"
            disabled
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Key" required prop="key">
          <el-input
            v-model="modificationForm.key"
            type="text"
            maxlength="128"
            disabled
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Value" required prop="value">
          <el-input
            v-model="modificationForm.value"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Please input"
            maxlength="2024"
            clearable
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="Description" required prop="description">
          <el-input
            v-model="modificationForm.description"
            :autosize="{ minRows: 2, maxRows: 4}"
            type="textarea"
            placeholder="Please input"
            maxlength="128"
            clearable
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="loading" @click="handleCancel('modificationForm')">Cancel</el-button>
        <el-button type="primary" :loading="loading" @click="handleModificationSubmit">Confirm</el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="dialogInstanceVisible" title="Instances Configs" @close="handleClose('instances')">
      <el-table :data="instances" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="serverConfig" label="Server Config" />
        <el-table-column label="Client Config">
          <el-table-column prop="clientConfig.instance" label="Instance" />
          <el-table-column prop="clientConfig.value" label="Value" />
        </el-table-column>
      </el-table>
    </el-dialog>

    <el-dialog :visible.sync="dialogHistoryVisible" title="Config Histories" @close="handleClose('histories')">
      <el-table :data="histories" border fit highlight-current-row style="width: 100%">
        <el-table-column prop="value" label="Value" />
        <el-table-column prop="updatedBy" label="Modifier" />
        <el-table-column prop="updatedAt" label="Modify Time">
          <template slot-scope="scope">
            <span>{{ num2string(scope.row.updatedAt,undefined) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import {
  getConfigs,
  addConfig,
  updateConfig,
  deleteConfigs,
  getConfigHistories,
  getConfigInstances,
  publishConfig,
  exportConfig,
  importConfig
} from '@/api/config'
import { getApplications } from '@/api/application'
import { getGroups } from '@/api/group'
import { getRoles } from '@/api/role'
import waves from '@/directive/waves' // waves directive
import { isSuccessful, num2string } from '@/utils'
import checkButtonPermission from '@/utils/button'
import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import Papa from 'papaparse'
import FileSaver from 'file-saver'

export default {
  name: 'ConfigPage',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        new: 'primary',
        edited: 'warning',
        approved: 'danger',
        published: 'success',
        removing: 'warning'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      instances: [],
      histories: [],
      applications: [],
      groups: [],
      list: [],
      total: 0,
      loading: false,
      uploads: [],
      roles: [],
      params: {
        page: 1,
        limit: 10,
        applicationId: null,
        groupId: null,
        name: '',
        key: ''
      },
      additionForm: {
        applicationId: null,
        groupId: null,
        name: '',
        key: '',
        value: '',
        description: '',
        roleIds: []
      },
      modificationForm: {
        id: null,
        name: '',
        key: '',
        value: '',
        description: '',
        roleIds: []
      },
      dialogAddConfigVisible: false,
      dialogEditConfigVisible: false,
      dialogInstanceVisible: false,
      dialogHistoryVisible: false,
      rules: {
        name: [
          {
            required: true,
            message: 'name is required',
            trigger: 'blur'
          }
        ],
        key: [
          {
            required: true,
            message: 'key is required',
            trigger: 'blur'
          }
        ],
        value: [
          {
            required: true,
            message: 'value is required',
            trigger: 'blur'
          }
        ],
        description: [
          {
            required: true,
            message: 'description is required',
            trigger: 'blur'
          }
        ]
      },
      multipleSelection: []
    }
  },
  created() {
    this.fetchApplications()
  },
  methods: {
    checkButtonPermission,
    num2string,
    fetchConfigs() {
      this.loading = true
      getConfigs({
        page: this.params.page,
        limit: this.params.limit,
        applicationId: this.params.applicationId,
        groupId: this.params.groupId,
        name: this.params.name,
        key: this.params.key
      })
        .then(response => {
          if (isSuccessful(response)) {
            this.total = response.data.total
            this.list = response.data.items
          }
        })
        .catch(() => {
          this.$message({
            message: 'Operation failed',
            type: 'danger',
            duration: 800
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleFilter() {
      this.fetchConfigs()
    },
    fetchRoles() {
      getRoles()
        .then(response => {
          if (isSuccessful(response)) {
            this.roles = response.data
          }
        })
        .catch(() => {
          this.$message({
            message: 'Operation failed',
            type: 'danger',
            duration: 800
          })
        })
    },
    fetchApplications() {
      getApplications()
        .then(response => {
          if (isSuccessful(response)) {
            this.applications = response.data
          }
        })
        .catch(() => {
          this.$message({
            message: 'Operation failed',
            type: 'danger',
            duration: 800
          })
        })
    },
    handleAddConfig() {
      if (!this.params.applicationId) {
        this.$message({
          message: 'Please select one application before adding',
          type: 'warning'
        })
        return
      }
      if (!this.params.groupId) {
        this.$message({
          message: 'Please select one group before adding',
          type: 'warning'
        })
        return
      }
      this.dialogAddConfigVisible = true
      this.fetchRoles()
      this.$nextTick(() => {
        this.$refs['additionForm'].clearValidate()
      })
    },
    handleEditConfig(row) {
      this.dialogEditConfigVisible = true
      this.modificationForm = Object.assign({}, row)
    },
    handleInstanceConfig(row) {
      this.dialogInstanceVisible = true
      getConfigInstances(row.id)
        .then(response => {
          if (isSuccessful(response)) {
            this.instances = response.data
          }
        })
        .catch(() => {
          this.$message({
            message: 'Operation failed',
            type: 'danger',
            duration: 800
          })
        })
    },
    handleHistoryConfig(row) {
      this.dialogHistoryVisible = true
      this.loading = true
      getConfigHistories(row.id)
        .then(response => {
          if (isSuccessful(response)) {
            this.histories = response.data
          }
        })
        .catch(() => {
          this.$message({
            message: 'Operation failed',
            type: 'danger',
            duration: 800
          })
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleDelete() {
      if (this.multipleSelection.length === 0) {
        this.$message({
          message: 'Please select at least one item to delete',
          type: 'warning',
          duration: 800
        })
        return
      }
      this.$confirm(
        'Are you sure to delete the selected item?',
        'Confirmation',
        {
          confirmButtonText: 'OK',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      )
        .then(() => {
          const indexs = []
          const ids = []
          const $list = this.list
          const $this = this
          this.multipleSelection.forEach(function(row, index) {
            indexs[index] = $list.indexOf(row)
            ids[index] = row.id
          })
          this.loading = true
          deleteConfigs(ids)
            .then(response => {
              if (isSuccessful(response)) {
                this.$message({
                  message: 'Successfully done!',
                  type: 'success',
                  duration: 800,
                  onClose: function() {
                    $this.fetchConfigs()
                  }
                })
                this.multipleSelection = []
              } else {
                this.$message({
                  message: 'Delete config failed',
                  type: 'warning',
                  duration: 800
                })
              }
            })
            .catch(() => {
              this.$message({
                message: 'Operation failed',
                type: 'danger',
                duration: 800
              })
            })
            .finally(() => {
              this.loading = false
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: 'Deletion was cancelled'
          })
        })
    },
    handleExport() {
      exportConfig(this.params)
        .then(response => {
          if (isSuccessful(response)) {
            const csv = Papa.unparse({
              fields: [
                'application',
                'group',
                'name',
                'key',
                'value',
                'description'
              ],
              data: response.data
            })
            const blob = new Blob([csv], {
              type: 'text/csv;charset=utf-8'
            })
            FileSaver.saveAs(blob, new Date().getTime())
          } else {
            this.$message({
              message: 'Operation failed',
              type: 'danger',
              duration: 800
            })
          }
        })
        .catch(() => {
          this.$message({
            message: 'Operation failed',
            type: 'danger',
            duration: 800
          })
        })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleClean() {
      this.params.applicationId = null
      this.params.groupId = null
      this.params.name = ''
      this.params.key = ''
    },
    handleCancel(ref) {
      if (ref === 'additionForm') {
        this.dialogAddConfigVisible = false
      } else if (ref === 'modificationForm') {
        this.dialogEditConfigVisible = false
      }
      this.$refs[ref].clearValidate()
      this.$refs[ref].resetFields()
    },
    handleAdditionSubmit() {
      this.$refs['additionForm'].validate(valid => {
        if (valid) {
          const $this = this
          this.loading = true
          this.additionForm.applicationId = this.params.applicationId
          this.additionForm.groupId = this.params.groupId
          addConfig(this.additionForm)
            .then(response => {
              if (isSuccessful(response)) {
                this.handleCancel('additionForm')
                this.$message({
                  message: 'Successfully done!',
                  type: 'success',
                  duration: 800,
                  onClose: function() {
                    $this.fetchConfigs()
                  }
                })
              } else {
                this.$message({
                  message: 'Add config failed',
                  type: 'warning',
                  duration: 800
                })
              }
            })
            .catch(() => {
              this.$message({
                message: 'Operation failed',
                type: 'danger',
                duration: 800
              })
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    handlePublishConfig(row) {
      const $this = this
      this.$confirm(
        'Would you like to publish this configuratioin?',
        'Confirmation',
        {
          confirmButtonText: 'Publish',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      )
        .then(() => {
          $this.loading = true
          publishConfig(row.id)
            .then(response => {
              if (isSuccessful(response)) {
                $this.fetchConfigs()
              } else {
                this.$message({
                  message: 'Operation failed',
                  type: 'danger',
                  duration: 800
                })
              }
            })
            .catch(() => {
              this.$message({
                message: 'Operation failed',
                type: 'danger',
                duration: 800
              })
            })
            .finally(() => {
              $this.loading = false
            })
        })
        .catch(() => {})
    },
    handleImport(arr) {
      const $this = this
      if (arr === undefined || arr.length === 0) {
        this.$message({
          message: 'Invalid data from imported file',
          type: 'warning',
          duration: 800
        })
      } else {
        this.loading = true
        importConfig({ configs: arr })
          .then(response => {
            if (isSuccessful(response)) {
              this.$message({
                message: 'Successfully done!',
                type: 'success',
                duration: 800,
                onClose: function() {
                  $this.fetchApplications()
                }
              })
            } else {
              this.$message({
                message: 'Operation failed',
                type: 'danger',
                duration: 800
              })
            }
          })
          .catch(() => {
            this.$message({
              message: 'Operation failed',
              type: 'danger',
              duration: 800
            })
          })
          .finally(() => {
            this.loading = false
          })
      }
    },
    handleApplicationChange(row) {
      if (row) {
        getGroups(row)
          .then(response => {
            if (isSuccessful(response)) {
              this.groups = response.data
            }
          })
          .catch(() => {
            this.$message({
              message: 'Operation failed',
              type: 'danger',
              duration: 800
            })
          })
      } else {
        this.groups = []
        this.params.applicationId = ''
      }
    },
    handleModificationSubmit() {
      this.$refs['modificationForm'].validate(valid => {
        if (valid) {
          const $this = this
          this.loading = true
          updateConfig(this.modificationForm)
            .then(response => {
              if (isSuccessful(response)) {
                this.handleCancel('modificationForm')
                this.$message({
                  message: 'Successfully done!',
                  type: 'success',
                  duration: 800,
                  onClose: function() {
                    $this.fetchConfigs()
                  }
                })
              } else {
                this.$message({
                  message: 'Update config failed',
                  type: 'warning',
                  duration: 800
                })
              }
            })
            .catch(() => {
              this.$message({
                message: 'Operation failed',
                type: 'danger',
                duration: 800
              })
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    },
    beforeUpload(file) {
      Papa.parse(file, {
        header: true,
        skipEmptyLines: true,
        trimHeaders: true,
        complete: (results, file) => {
          this.handleImport(results.data)
        }
      })
    },
    handleClose(key) {
      if (key === 'instances') {
        this.instances = []
      } else if (key === 'histories') {
        this.histories = []
      }
    },
    checkIfCanBeSelected(row, index) {
      if (row.status === 'published') {
        return true
      } else {
        false
      }
    }
  }
}
</script>
