<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select
        v-model="params.application"
        placeholder="Application"
        clearable
        style="width: 120px"
        class="filter-item"
        @change="handleApplicationChange"
        @clear="handleApplicationChange"
      >
        <el-option-group v-for="appgroup in appgroups" :key="appgroup.label" :label="appgroup.label">
            <el-option v-for="item in appgroup.items" :key="item.id" :label="item.name" :value="item.id" />
        </el-option-group>
      </el-select>
      <el-select v-model="params.group" placeholder="Group" clearable style="width: 120px" class="filter-item">
        <el-option v-for="item in groups" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-input
        v-model="params.instance"
        placeholder="Instance"
        clearable
        style="width: 200px;"
        class="filter-item"
      />
      <el-input
        v-model="params.traceId"
        placeholder="Trace Id"
        clearable
        style="width: 200px;"
        class="filter-item"
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
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column label="Create At" width="160px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.createdAt }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Application" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.application }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Group" width="100px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.group }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Instance" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.instance }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Trace ID" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.traceId }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Operation Type" width="150px" align="center">
        <template slot-scope="{row}">
          <span>{{ row.type }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Level" width="80px" align="center">
        <template slot-scope="{row}">
          <el-tag :type="row.level | statusFilter">
            {{ row.level }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="Log" header-align="center">
        <template slot-scope="{row}" align="left">
          <span>{{ row.log }}</span>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.page"
      :limit.sync="params.limit"
      @pagination="fetchClientLogs"
    />
  </div>
</template>

<script>
import { getClientLog } from '@/api/clientLog'
import { getApplications } from '@/api/application'
import { getGroups } from '@/api/group'
import waves from '@/directive/waves' // waves directive
import { parseTime, isSuccessful } from '@/utils'
import Pagination from '@/components/Pagination' // secondary package based on el-pagination
import lang from 'element-ui/lib/locale/lang/en'
import locale from 'element-ui/lib/locale'

locale.use(lang)

export default {
  name: 'ComplexTable',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        log: 'success',
        warn: 'warning',
        error: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      appgroups: [],
      groups: [],
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
      typeOptions: ['add', 'delete', 'modify'],
      pageOptions: [{ name: 'Metadata', value: 'Metadata' }, { name: 'Configs', value: 'Configs' }, { name: 'Allocate Configs', value: 'Allocate' }, { name: 'Role', value: 'Role' }, { name: 'Users', value: 'Users' }],
      cleanOptions: [{ name: 'Clean up log data 7 days ago', value: 0 }, { name: 'Clean up log data 30 days ago', value: 1 }, { name: 'Clean up log data 90 days ago', value: 2 }, { name: 'Clean up log data 1 year ago', value: 3 }],
      tableKey: 0,
      list: null,
      total: 0,
      listLoading: true,
      params: {
        page: 1,
        limit: 20,
        application: '',
        group: '',
        instance: '',
        traceId: '',
        type: '',
        level: '',
        sort: '+id'
      },
      logForm: {
        type: ''
      },
      importanceOptions: [1, 2, 3],
      sortOptions: [{ label: 'ID Ascending', key: '+id' }, { label: 'ID Descending', key: '-id' }],
      statusOptions: ['published', 'draft', 'deleted'],
      showReviewer: false,
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      dialogPvVisible: false,
      pvData: [],
      rules: {
        method: [{ required: true, message: 'method is required', trigger: 'blur' }]
      },
      downloadLoading: false,
      multipleSelection: []
    }
  },
  created() {
    this.fetchClientLogs()
    this.fetchApplications()
  },
  methods: {
    fetchApplications() {
      getApplications()
        .then(response => {
          if (isSuccessful(response)) {
            this.appgroups = response.data
          }
        })
        .catch(error => {
          console.error(error)
        })
    },
    fetchClientLogs() {
      this.listLoading = true
      getClientLog({ page: this.params.page, limit: this.params.limit, application: this.params.application, group: this.params.group, instance: this.params.instance, traceId: this.params.traceId, type: this.params.type, level: this.params.level, createdAt: this.params.createdAt
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
    handleApplicationChange(row) {
      if (row) {
        getGroups(row)
          .then(response => {
            // if (isSuccessful(response)) {
            //   this.groups = response.data
            // }
            this.groups = response.data
          })
          .catch(error => {
            console.error(error)
          })
      } else {
        this.groups = []
        this.params.applicationId = ''
      }
    },
    handleFilter() {
      this.params.page = 1
      this.fetchClientLogs()
    },
    sortChange(data) {
      const { prop, order } = data
      if (prop === 'id') {
        this.sortByID(order)
      }
    },
    sortByID(order) {
      if (order === 'ascending') {
        this.params.sort = '+id'
      } else {
        this.params.sort = '-id'
      }
      this.handleFilter()
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => {
        if (j === 'timestamp') {
          return parseTime(v[j])
        } else {
          return v[j]
        }
      }))
    },
    getSortClass: function(key) {
      const sort = this.params.sort
      return sort === `+${key}`
        ? 'ascending'
        : sort === `-${key}`
          ? 'descending'
          : ''
    }
  }
}
</script>
