<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="params.type"
        placeholder="Type"
        clearable
        style="width: 120px"
        class="filter-item"
      >
        <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item" />
      </el-select>
      <el-select
        v-model="params.view"
        placeholder="Page"
        clearable
        style="width: 160px"
        class="filter-item"
      >
        <el-option v-for="item in pageOptions" :key="item.name" :label="item.name" :value="item.value" />
      </el-select>
      <el-input
        v-model="params.createdBy"
        placeholder="Modifier"
        clearable
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-date-picker
        v-model="params.createdAt"
        class="filter-item"
        value-format="yyyy-MM-dd HH:mm:ss"
        type="datetimerange"
        :picker-options="pickerOptions"
        range-separator="to"
        start-placeholder="Start Time"
        end-placeholder="End Time"
        align="right"
        :default-time="['00:00:00','23:59:59']"
      />
      <el-button
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-search"
        @click="handleFilter"
      >
        Search
      </el-button>
      <el-button
        v-if="checkButtonPermission('Operation Log','Clean')"
        v-waves
        class="filter-item"
        type="primary"
        icon="el-icon-delete"
        style="margin-left: 30px"
        @click="handleDelete"
      >Clean
      </el-button>
    </div>

    <el-table
      v-loading="listLoading"
      :data="list"
      fit
      highlight-current-row
      style="width: 100%"
    >
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <div v-if="props.row.before!==''" style="word-wrap: break-word; word-break: break-all;float: left;width: 24%;" v-html="handleBefore(props.row.before)" />
            <div v-if="props.row.after!==''" style="word-wrap: break-word; word-break: break-all;float: left;width: 24%;" v-html="handleAfter(props.row.after)" />
            <b v-if="props.row.before === '' && props.row.after === ''">no data</b>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="Page" width="200px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.pageName }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Type" width="200px" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.type | statusFilter">{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Target" width="200px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.target }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Modifier" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createdBy }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="date" label="Modify Time" sortable align="center">
        <template slot-scope="{row}">
          <span>{{ row.createdAt }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.page"
      :limit.sync="params.limit"
      @pagination="fetchOperationLogs"
    />

    <el-dialog title="Clean Operation Log" :visible.sync="dialogCleanVisible">
      <el-form
        ref="logForm"
        :rules="rules"
        :model="logForm"
        label-position="left"
        label-width="90px"
        style="width: 400px; margin-left:50px;"
      >
        <el-form-item label="method">
          <el-select
            v-model="logForm.type"
            placeholder="method"
            clearable
            style="width: 300px"
            class="filter-item"
          >
            <el-option v-for="item in cleanOptions" :key="item.name" :label="item.name" :value="item.value" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogCleanVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="deleteData()">
          Confirm
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getOperationLog, clearLog } from '@/api/operationlog'
import waves from '@/directive/waves' // waves directive
import { isSuccessful } from '@/utils'
import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import lang from 'element-ui/lib/locale/lang/en'
import locale from 'element-ui/lib/locale'
import checkButtonPermission from '@/utils/button'

locale.use(lang)

export default {
  name: 'ComplexTable',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        add: 'success',
        modify: 'info',
        delete: 'danger',
        switch: 'warning'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      pickerOptions: {
        shortcuts: [{
          text: 'last week',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: 'last month',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: 'last 3 months',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },
      typeOptions: ['add', 'delete', 'modify', 'switch', 'publish'],
      pageOptions: [{ name: 'Metadata', value: 'Metadata' }, { name: 'Configs', value: 'Configs' }, { name: 'Allocate Configs', value: 'Allocate' }, { name: 'Role', value: 'Role' }, { name: 'Users', value: 'Users' }],
      cleanOptions: [{ name: 'Clean up log data 7 days ago', value: 0 }, { name: 'Clean up log data 30 days ago', value: 1 }, { name: 'Clean up log data 90 days ago', value: 2 }, { name: 'Clean up log data 1 year ago', value: 3 }],
      list: null,
      total: 0,
      listLoading: true,
      params: {
        page: 1,
        limit: 20,
        id: '',
        pageName: '',
        type: '',
        target: '',
        before: '',
        after: '',
        createdBy: '',
        createdAt: [],
        sort: '+id'
      },
      logForm: {
        type: ''
      },
      dialogCleanVisible: false,
      dialogStatus: '',
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      pvData: [],
      rules: {
        method: [{ required: true, message: 'method is required', trigger: 'blur' }]
      },
      beforeHtml: '',
      afterHtml: ''
    }
  },
  created() {
    this.fetchOperationLogs()
  },
  methods: {
    checkButtonPermission,
    fetchOperationLogs() {
      this.listLoading = true
      getOperationLog({ page: this.params.page, limit: this.params.limit, id: this.params.id, view: this.params.view, type: this.params.type, target: this.params.target, before: this.params.before, createdBy: this.params.createdBy, createdAt: this.params.createdAt
      }).then(response => {
        if (isSuccessful(response)) {
          this.total = response.data.total
          this.list = response.data.items
        }
      })
        .catch(error => {
          console.error(error)
        })
        .finally(() => {
          this.listLoading = false
        })
    },
    handleBefore(before) {
      var html = ''
      if (before != null && before !== '') {
        before = JSON.stringify(JSON.parse(before), null, '<br/>')
        html = '<b>before: </b><br/><br/>' + before.replace(/<br\/>/g, '</font><br/>&nbsp;&nbsp;&nbsp;&nbsp;').replace(/}/g, '<br/>}')
      } else {
        html = ''
      }
      return html
    },
    handleAfter(after) {
      var html = ''
      if (after != null && after !== '') {
        after = JSON.stringify(JSON.parse(after), null, '<br/>')
        html = '<b>after: </b><br/><br/>' + after.replace(/<br\/>/g, '</font><br/>&nbsp;&nbsp;&nbsp;&nbsp;').replace(/}/g, '<br/>}')
      } else {
        html = ''
      }
      return html
    },
    handleFilter() {
      this.params.page = 1
      this.fetchOperationLogs()
    },
    deleteData() {
      const $this = this
      if (this.logForm.type === '') {
        this.$message({
          type: 'error',
          message: 'Please choose method'
        })
        return
      }
      this.listLoading = true
      clearLog(this.logForm.type).then(response => {
        if (isSuccessful(response)) {
          this.dialogCleanVisible = false
          this.logForm.type = ''
          this.$notify({
            title: 'Success',
            message: 'clean log Successfully',
            type: 'success',
            duration: 800
          })
          $this.fetchOperationLogs()
        }
      })
        .catch(error => {
          console.error(error)
        })
        .finally(() => {
          this.listLoading = false
        })
    },
    handleDelete(row) {
      this.dialogCleanVisible = true
      this.$nextTick(() => {
        this.$refs['logForm'].clearValidate()
      })
    }
  }
}
</script>
