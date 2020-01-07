<template>
  <div>
    <el-badge :is-dot="true" style="line-height: 25px;margin-top: -5px;" @click.native="fetchErrors">
      <el-button style="padding: 8px 10px;" size="small" type="danger">
        <svg-icon icon-class="bug" />
      </el-button>
    </el-badge>

    <el-dialog :visible.sync="dialogTableVisible" width="80%" append-to-body>
      <div slot="title">
        <span style="padding-right: 10px;">Error Log</span>
        <el-button size="mini" type="primary" icon="el-icon-delete" @click="clearAll">Clear All</el-button>
      </div>
      <el-table :data="list" border>
        <el-table-column label="Type" align="center">
          <template slot-scope="{row}">{{ row.type }}</template>
        </el-table-column>
        <el-table-column label="Error" align="center">
          <template slot-scope="scope">{{ scope.row.error }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { getErrors } from '@/api/admin'
import { isSuccessful } from '@/utils'

export default {
  name: 'ErrorLog',
  data() {
    return {
      dialogTableVisible: false,
      list: []
    }
  },
  computed: {
    errorLogs() {
      return this.$store.getters.errorLogs
    }
  },
  methods: {
    clearAll() {
      this.dialogTableVisible = false
      this.$store.dispatch('errorLog/clearErrorLog')
    },
    fetchErrors() {
      this.dialogTableVisible = true
      getErrors().then(response => {
        if (isSuccessful(response)) {
          this.list = response.data
        }
      })
        .catch(error => {
          console.error(error)
        })
    }
  }
}
</script>

<style scoped>
.message-title {
  font-size: 16px;
  color: #333;
  font-weight: bold;
  padding-right: 8px;
}
</style>
