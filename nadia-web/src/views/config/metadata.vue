<template>
  <div class="app-container">
    <div class="filter-container">
      <el-select v-model="postForm.applications" :method="getApplications" placeholder="Application" multiple clearable @change="getlist">
        <el-option v-for="item in applicationOptions" :key="item.name" :label="item.name" :value="item.name" />
      </el-select>
      <el-button v-if="checkButtonPermission('Metadata','Add')" class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleAddApplication">
        Add
      </el-button>
      <el-button v-if="checkButtonPermission('Metadata','Delete')" class="filter-item" type="danger" icon="el-icon-delete" @click="deleteApplication">
        Delete
      </el-button>
      <el-button class="filter-item" type="info" icon-class="compare" @click="handleCompare"><svg-icon icon-class="compare" />
        Compare
      </el-button>
    </div>

    <el-table
      :data="list"
      style="width: 100%"
      row-key="id"
      border
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      default-expand-all
      @selection-change="handleSelectionChange"
    >
      <el-table-column
        width="40px"
        type="selection"
      />
      <el-table-column label="Application" width="150px" prop="application" />
      <el-table-column label="Description" width="150px">
        <template slot-scope="{row}">
          <template v-if="row.edit">
            <el-input v-model="row.description" class="edit-input" size="small" />
            <el-button
              class="cancel-btn"
              size="small"
              icon="el-icon-refresh"
              type="warning"
              @click="cancelEdit(row)"
            >
              cancel
            </el-button>
          </template>
          <span v-else>{{ row.description }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="mail"
        label="Mail"
      >
        <template slot-scope="{row}">
          <template v-if="row.edit">
            <el-input v-model="row.mail" class="edit-input" size="small" />
            <el-button
              class="cancel-btn"
              size="small"
              icon="el-icon-refresh"
              type="warning"
              @click="cancelEdit(row)"
            >
              cancel
            </el-button>
          </template>
          <span v-else>{{ row.mail }}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="group"
        label="Group"
        width="150"
      />
      <el-table-column
        prop="instances"
        label="Instances"
        width="180"
      >
        <template slot-scope="{row}">
          <span class="link-type" @click="handleInstancesConfigs(row)">{{ row.instances }}</span>
        </template>
      </el-table-column>
      <el-table-column label="Actions" align="center" width="350" class-name="small-padding fixed-width">
        <template v-if="row.group=='Default'" slot-scope="{row}">
          <el-button
            v-if="row.edit"
            type="success"
            size="small"
            icon="el-icon-circle-check-outline"
            @click="confirmEdit(row)"
          >
            Ok
          </el-button>
          <el-button
            v-if="row.group=='Default' && checkButtonPermission('Metadata','Edit')"
            type="primary"
            size="small"
            icon="el-icon-edit"
            @click="handleEditApplication(row)"
          >
            Edit
          </el-button>
          <el-button v-if="row.group=='Default' && checkButtonPermission('Metadata','Group')" type="primary" icon="el-icon-edit" size="small" @click="handleAddGroup(row)">
            Group
          </el-button>
          <el-button v-if="row.group=='Default' && checkButtonPermission('Metadata','Instance')" size="small" icon="el-icon-edit" type="success" @click="handleSwitchInstance(row)">
            Instances
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :visible.sync="dialogAddApplicationVisible" :title="applicationDialogType==='edit'?'Edit Application':'New Application'" @close="closeDialogAddApplication">
      <el-form ref="dataForm" :model="temp" :rules="applicationRules" label-position="left" label-width="100px" style="width: 400px; margin-left:50px;">
        <el-form-item label="Application" prop="application">
          <el-input v-model="temp.application" type="text" :disabled="applicationDialogType==='edit'?true:false" maxlength="64" placeholder="Please input application name" />
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="temp.description" type="text" maxlength="64" placeholder="Please input application description" />
        </el-form-item>
        <el-form-item label="Mail" prop="mail">
          <el-input v-model="temp.mail" :autosize="{ minRows: 2, maxRows: 4}" type="textarea" placeholder="Please input email with ;" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogAddApplicationVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="applicationDialogType==='edit'?confirmEdit():addApplication()">
          Confirm
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="Add Group" :visible.sync="dialogAddGroupVisible" @close="closeDialogAddGroup">
      <el-form ref="dataForm" :model="temp" :rules="groupRules" label-position="left" label-width="200px" style="width: 400px; margin-left:50px;">
        <el-form-item label="Create form">
          <el-select v-model="temp.sourceGroup" class="filter-item" placeholder="Please select">
            <el-option v-for="item in groupOptions" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="Group name" prop="targetGroup">
          <el-input v-model="temp.targetGroup" type="text" maxlength="20" placeholder="Please input group name" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogAddGroupVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="addGroup()">
          Confirm
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="Switch Instance" :visible.sync="dialogSwitchInstanceVisible" width="1100px" @close="closeDialogSwitchInstance">
      <div class="components-container board">
        <Kanban v-for="item in groupInstances" :key="item.key" :list="item.instances" :header-text="item.group" :group="group" />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogSwitchInstanceVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="switchInstance()">
          Confirm
        </el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="dialogInstanceConfigsVisible" title="Instances Configs" @close="closeDialogInstanceConfigs">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="80px" style="width: 400px;">
        <el-form-item label="Instance">
          <el-select v-model="temp.instance" class="filter-item" placeholder="Please select" @change="getInstanceConfigs">
            <el-option v-for="item in instancesOptions" :key="item.name" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="configsData" border fit highlight-current-row style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column prop="serverKey" label="Server Key" />
        <el-table-column prop="serverConfig" label="Server Value" />
        <el-table-column prop="clientKey" label="Client Key" />
        <el-table-column prop="clientConfigNew" label="Client New Value" />
        <el-table-column prop="clientConfigOld" label="Client Old Value" />
      </el-table>
    </el-dialog>

    <el-dialog :visible.sync="dialogCompareVisible" title="Instances Configs" @close="closeCompares">
      <el-table :data="configTable" border fit highlight-current-row style="width: 100%" :row-class-name="tableRowClassNameCompare">
        <el-table-column
          v-for="(val,i) in configHeader"
          :key="i"
          :prop="val.props"
          :label="val.label"
        />
      </el-table>
    </el-dialog>

  </div>
</template>
<script>
import { deepClone } from '@/utils'
import Kanban from '@/components/Kanban'
import { switchInstances, getgroupInstances, compareGroupConfigs, updateApplication, instanceConfigsList, instanceList, addGroup, groupList, deleteApplication, addApplication, applicationList, metaList } from '@/api/metadata'
import checkButtonPermission from '@/utils/button'

const defaultForm = {
  applications: []
}

export default {
  components: {
    Kanban
  },
  data() {
    return {
      postForm: Object.assign({}, defaultForm),
      group: 'mission',
      applicationDialogType: 'new',
      groupInstances: null,
      configsData: null,
      instancesOptions: null,
      groupOptions: null,
      configTable: null,
      configHeader: null,
      configHeaderGroup: [],
      dialogCompareVisible: false,
      dialogInstanceConfigsVisible: false,
      dialogAddApplicationVisible: false,
      dialogAddGroupVisible: false,
      dialogSwitchInstanceVisible: false,
      applicationOptions: null,
      list: null,
      applicationRules: {
        application: [{ required: true, message: 'application is required', trigger: 'change' }],
        description: [{ required: true, message: 'description is required', trigger: 'change' }]
      },
      groupRules: {
        sourceGroup: [{ required: true, message: 'source group is required', trigger: 'change' }],
        targetGroup: [{ required: true, message: 'target group is required', trigger: 'change' }]
      },
      temp: {
        application: '',
        description: '',
        mail: '',
        sourceGroup: '',
        targetGroup: '',
        instance: '',
        group: ''
      },
      multipleSelection: []
    }
  },
  created() {
    this.getApplications()
    this.getlist()
  },
  methods: {
    checkButtonPermission,
    tableRowClassName({ row, rowIndex }) {
      if (row.clientConfigNew !== row.serverConfig) {
        return 'warning-row'
      } else if (row.serverKey !== row.clientKey) {
        return 'warning-row'
      }
      return ''
    },
    tableRowClassNameCompare({ row, rowIndex }) {
      var value = ''
      for (const item of this.configHeaderGroup) {
        if (value !== '' && value !== row[item + '_value']) {
          return 'warning-row'
        }
        value = row[item + '_value']
      }
      return ''
    },
    getApplications() {
      applicationList().then(response => {
        this.applicationOptions = response.data
      })
    },
    getGroups(row) {
      groupList(row.application).then(response => {
        this.groupOptions = response.data
      })
    },
    getlist() {
      metaList(this.postForm.applications).then(response => {
        this.list = response.data
      })
    },
    getInstances(row) {
      instanceList(row.application, row.group).then(response => {
        this.instancesOptions = response.data
      })
    },
    getInstanceConfigs() {
      instanceConfigsList({ application: this.temp.application, group: this.temp.group, instance: this.temp.instance }).then(response => {
        this.configsData = response.data
      })
    },
    getGroupInstances(application) {
      getgroupInstances(application).then(response => {
        this.groupInstances = response.data
      })
    },
    resetTemp() {
      this.temp = {
        application: '',
        description: '',
        mail: '',
        sourceGroup: '',
        targetGroup: '',
        group: '',
        instance: ''
      }
    },
    addApplication() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          addApplication({ application: this.temp.application, description: this.temp.description, mail: this.temp.mail }).then(() => {
            this.dialogAddApplicationVisible = false
            this.resetTemp()
            this.getApplications()
            this.getlist()
            this.$notify({
              title: 'Success',
              message: 'Added Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    deleteApplication() {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        for (const item of this.multipleSelection) {
          deleteApplication({ application: item.application, group: item.group }).then(() => {
            this.getApplications()
            this.getlist()
            this.$notify({
              title: 'Success',
              message: 'Deleted Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Has been cancel'
        })
      })
    },
    updateApplication(row) {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            updateApplication({ application: row.application, description: row.description, mail: row.mail }).then(() => {
              this.getlist()
              this.resetTemp()
              this.dialogAddApplicationVisible = false
              this.$notify({
                title: 'Success',
                message: 'Updated Successfully',
                type: 'success',
                duration: 2000
              })
            })
          }
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Has been cancel'
        })
      })
    },
    addGroup(row) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          addGroup({ application: this.temp.application, sourceGroup: this.temp.sourceGroup, targetGroup: this.temp.targetGroup }).then(() => {
            this.dialogAddGroupVisible = false
            this.resetTemp()
            this.getlist()
            this.$notify({
              title: 'Success',
              message: 'Added Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    compareGroupConfigs(application, groups) {
      compareGroupConfigs({ application: application, groups: groups }).then(response => {
        this.configTable = response.data.configTable
        this.configHeader = response.data.configHeader
      })
    },
    switchInstance() {
      this.$confirm('Do you want to continue?', 'Info', {
        confirmButtonText: 'Conform',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        switchInstances({ application: this.temp.application, data: this.groupInstances }).then(() => {
          this.dialogSwitchInstanceVisible = false
          this.getlist()
          this.$notify({
            title: 'Success',
            message: 'Switchde Successfully',
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
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    handleAddApplication() {
      this.applicationDialogType = 'new'
      this.dialogAddApplicationVisible = true
    },
    handleEditApplication(row) {
      this.temp = deepClone(row)
      this.applicationDialogType = 'edit'
      this.dialogAddApplicationVisible = true
    },
    handleAddGroup(row) {
      this.dialogAddGroupVisible = true
      this.getGroups(row)
      this.temp.application = row.application
    },
    handleSwitchInstance(row) {
      this.dialogSwitchInstanceVisible = true
      this.getGroupInstances(row.application)
      this.temp.application = row.application
    },
    handleInstancesConfigs(row) {
      this.dialogInstanceConfigsVisible = true
      this.getInstances(row)
      this.temp.application = row.application
      this.temp.group = row.group
    },
    handleCompare() {
      if (this.multipleSelection.length <= 1) {
        this.$message({
          type: 'error',
          message: 'Please choose 2 or more than 2 group'
        })
        return
      }
      var application = ''
      for (const item of this.multipleSelection) {
        if (application !== '' && application !== item.application) {
          this.$message({
            type: 'error',
            message: 'Please choose 1 type application'
          })
          return
        }
        application = item.application
        this.configHeaderGroup.push(item.group)
      }
      this.compareGroupConfigs(application, this.configHeaderGroup)
      this.dialogCompareVisible = true
    },
    closeDialogInstanceConfigs() {
      this.instancesOptions = []
      this.resetTemp()
      this.configsData = []
    },
    closeCompares() {
      this.configTable = []
      this.configHeader = []
      this.configHeaderGroup = []
    },
    closeDialogSwitchInstance() {
      this.resetTemp()
    },
    closeDialogAddApplication() {
      this.resetTemp()
    },
    closeDialogAddGroup() {
      this.resetTemp()
    },
    cancelEdit(row) {
      row.title = row.originalTitle
      row.edit = false
      this.$message({
        message: 'The title has been restored to the original value',
        type: 'warning'
      })
    },
    confirmEdit() {
      this.updateApplication(this.temp)
    }
  }
}
</script>
<style lang="scss">
.board {
  width: 1000px;
  margin-left: 20px;
  display: flex;
  justify-content: space-around;
  flex-direction: row;
  align-items: flex-start;
}
.el-table .cell {
  white-space: pre-line;
}
.el-table .warning-row {
  background: oldlace;
}
</style>
