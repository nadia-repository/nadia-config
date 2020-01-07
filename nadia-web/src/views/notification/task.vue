<template>
  <div class="tab-container">
    <el-tabs v-model="activeName" style="margin-top:15px;" type="border-card" @tab-click="changePane">
      <el-tab-pane v-for="item in tabMapOptions" :key="item.key" :label="item.label" :name="item.key">
        <keep-alive>
          <el-table
            v-loading="listLoading"
            :data="list"
            border
            fit
            highlight-current-row
            style="width: 100%"
          >
            <el-table-column label="Operation Type" align="center">
              <template slot-scope="{row}">
                <el-tag :type="row.type | statusFilter">{{ row.type }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="Config" align="center">
              <template slot-scope="{row}">
                <el-popover
                  placement="right"
                  width="400"
                  trigger="click"
                >
                  <div>Total : {{ configNum }}</div>
                  <el-tree
                    :data="actionParams.app"
                    :props="props"
                    default-expand-all
                  />
                  <el-button slot="reference" @click="open(row.action)">config</el-button>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column v-if="activeName=='COMPLETE'" label="Proceed" align="center">
              <template slot-scope="{row}">
                <el-popover
                  placement="bottom"
                  width="400"
                  trigger="click"
                >
                  <el-steps direction="vertical" :active="activeNum" align-center>
                    <el-step v-for="i in proceed" :key="i.title" :title="i.title" :description="i.text" />
                  </el-steps>
                  <el-button slot="reference" @click="open2(row.nodeList, row.action)">proceed</el-button>
                </el-popover>
              </template>
            </el-table-column>
            <el-table-column prop="createDate" label="Created At" sortable align="center">
              <template slot-scope="{row}">
                <span>{{ row.createdAt }}</span>
              </template>
            </el-table-column>
            <el-table-column v-if="activeName=='PENDING'" label="Created By" align="center" width="170px">
              <template slot-scope="{row}">
                <span>{{ row.createdBy }}</span>
              </template>
            </el-table-column>
            <el-table-column v-if="activeName=='COMPLETE'" prop="endDate" label="End at" sortable align="center">
              <template slot-scope="{row}">
                <span>{{ row.endAt }}</span>
              </template>
            </el-table-column>
            <el-table-column v-if="activeName=='PENDING' || activeName=='PROCESSING'" label="Next Approvers" align="center">
              <template slot-scope="{row}">
                <span>{{ row.nextApproverRoles }}</span>
              </template>
            </el-table-column>
            <el-table-column v-if="activeName=='PENDING'" label="Actions" align="center" class-name="small-padding fixed-width">
              <template slot-scope="{row}">
                <el-button v-if="checkButtonPermission('Task','Approve')" type="success" size="mini" @click="approve(row.id)">
                  Approve
                </el-button>
                <el-button v-if="checkButtonPermission('Task','Reject')" type="danger" size="mini" @click="reject(row.id)">
                  Reject
                </el-button>
              </template>
            </el-table-column>
            <el-table-column v-if="activeName=='COMPLETE'" label="status" align="center">
              <template slot-scope="{row}">
                <el-tag :type="row.status | statusFilter">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </keep-alive>
      </el-tab-pane>
    </el-tabs>
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="params.page"
      :limit.sync="params.limit"
      @pagination="changePane"
    />
  </div>
</template>

<script>
import { getTask, approve, getHistoryTask, getProcessingTask } from '@/api/task'
import waves from '@/directive/waves' // waves directive
import Pagination from '@/components/Pagination'
import { isSuccessful } from '@/utils'
import checkButtonPermission from '@/utils/button'// secondary package based on el-pagination

export default {
  name: 'Tab',
  components: { Pagination },
  directives: { waves },
  filters: {
    statusFilter(status) {
      const statusMap = {
        completed: 'success',
        reject: 'danger',
        add: 'success',
        delete: 'warning'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      tabMapOptions: [
        { label: 'Pending Task', key: 'PENDING' },
        { label: 'Processing Task', key: 'PROCESSING' },
        { label: 'Complete Task', key: 'COMPLETE' }
      ],
      activeName: 'PENDING',
      listLoading: true,
      list: null,
      total: 0,
      configNum: 0,
      params: {
        page: 1,
        limit: 20,
        app: '',
        group: '',
        name: '',
        before: '',
        after: '',
        createdBy: '',
        createdAt: [],
        sort: '+id'
      },
      listQuery: {
        page: 1,
        limit: 20
      },
      actionParams: {
        type: '',
        app: [],
        group: [],
        config: [],
        name: '',
        key: '',
        value: ''
      },
      proceed: [],
      activeNum: 0,
      props: {
        label: 'name',
        children: 'children'
      }
    }
  },
  watch: {
    activeName(val) {
      this.$router.push(`${this.$route.path}?tab=${val}`)
    }
  },
  created() {
    // init the default selected tab
    const tab = this.$route.query.tab
    if (tab) {
      this.activeName = tab
    }
    if (this.activeName === 'PENDING') {
      this.fetchTasks()
    } else if (this.activeName === 'COMPLETE') {
      this.fetchHistoryTasks()
    } else if (this.activeName === 'PROCESSING') {
      this.fetchProcessingTasks()
    }
  },
  methods: {
    checkButtonPermission,
    changePane() {
      if (this.activeName === 'PENDING') {
        this.fetchTasks()
      } else if (this.activeName === 'COMPLETE') {
        this.fetchHistoryTasks()
      } else if (this.activeName === 'PROCESSING') {
        this.fetchProcessingTasks()
      }
    },
    approve(id) {
      approve({ taskId: id, flag: true }).then(response => {
        if (isSuccessful(response)) {
          this.$message({
            message: 'OK',
            type: 'success'
          })
        } else {
          this.$message.error(response.msg)
        }
      })
        .catch(error => {
          console.error(error)
        })
        .finally(() => {
          this.fetchTasks()
        })
    },
    reject(id) {
      approve({ taskId: id, flag: false }).then(response => {
        if (isSuccessful(response)) {
          if (isSuccessful(response)) {
            this.$message({
              message: 'OK',
              type: 'success'
            })
          } else {
            this.$message.error(response.msg)
          }
        }
      })
        .catch(error => {
          console.error(error)
        })
        .finally(() => {
          this.fetchTasks()
        })
    },
    open(action) {
      var obj = JSON.parse(action)
      this.actionParams.type = obj.type
      this.actionParams.app = obj.children
      this.getConfigNum(action)
    },
    open2(nodeList, action) {
      this.proceed = []
      var i = 0
      for (i = 0; i < nodeList.length; i++) {
        const obj = { text: '', title: '' }
        obj.text = nodeList[i].operateAt
        if (nodeList[i].actionType === 'create') {
          const actionObj = JSON.parse(action)
          this.getConfigNum(action)
          if (this.configNum === 1) {
            obj.title = nodeList[i].operator + ' apply to ' + actionObj.type + ' a config.'
          } else if (this.configNum > 1) {
            obj.title = nodeList[i].operator + ' apply to ' + actionObj.type + ' ' + this.configNum + ' configs.'
          }
        } else {
          obj.title = nodeList[i].operator + ' ' + nodeList[i].actionType + ' the task.'
        }
        this.proceed.push(obj)
      }
      this.activeNum = i
    },
    fetchTasks() {
      this.listLoading = true
      getTask({ page: this.params.page, limit: this.params.limit }).then(response => {
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
    fetchHistoryTasks() {
      this.listLoading = true
      getHistoryTask({ page: this.params.page, limit: this.params.limit }).then(response => {
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
    fetchProcessingTasks() {
      this.listLoading = true
      getProcessingTask({ page: this.params.page, limit: this.params.limit }).then(response => {
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
    getConfigNum(action) {
      var obj = JSON.parse(action)
      let temp = 0
      for (const app of obj.children) {
        for (const group of app.children) {
          temp += group.length
        }
      }
      this.configNum = temp
    }
  }
}
</script>

<style scoped>
  .tab-container {
    margin: 30px;
  }
</style>
