<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />

    <div class="right-menu">
      <template v-if="device!=='mobile'">
        <search id="header-search" class="right-menu-item" />

        <error-log v-if="errorCount>0" class="errLog-container right-menu-item hover-effect" />

        <el-popover
          placement="bottom"
          width="300"
          trigger="click"
        >
          <router-link to="/notification-center/task">
            <div style="font-size: 20px;">Pending Tasks:</div>
            <hr size="10px" noshade="true">
            <template v-for="item in list ">
              <span :key="item.id+'-1'" style="font-size: 18px;padding: 10px">{{ item.createdBy }}:</span>
              <span :key="item.id+'-2'" style="font-size: 15px;float: right;padding: 10px;margin-top: -8px;">{{ item.type }} {{ (getConfigNum(item.action) > 1 )? (getConfigNum(item.action) + ' configs') : 'a config' }}</span>
              <div :key="item.id+'-3'" style="padding: 10px;margin-bottom: 10px;color: grey;">{{ item.createdAt }}</div>
              <hr :key="item.id+'-4'" size="0.2px">
            </template>
            <div align="center">
              <span v-if="total > 0" style="color: rgb(64, 158, 255)"> ... {{ total }} tasks in total to approve</span>
              <span v-if="total == 0">no data</span>
            </div>
          </router-link>
          <el-button slot="reference" icon="svg-icon" class="right-menu-item hover-effect el-icon-message-solid" style="border: 0px;" @click="open()" />
          <el-badge v-if="taskCount>0" slot="reference" is-dot style="right: 9px;bottom: 23px;cursor: pointer;" @click="open()" />
        </el-popover>

        <screenfull id="screenfull" class="right-menu-item hover-effect" />

        <el-tooltip content="Global Size" effect="dark" placement="bottom">
          <size-select id="size-select" class="right-menu-item hover-effect" />
        </el-tooltip>

      </template>

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <!-- <img :src="avatar+'?imageView2/1/w/80/h/80'" class="user-avatar"> -->
          <span style="display:block;" v-text="this.$store.getters.name" />
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <!-- <router-link to="/profile/index">
            <el-dropdown-item>Profile</el-dropdown-item>
          </router-link>
          <router-link to="/">
            <el-dropdown-item>Dashboard</el-dropdown-item>
          </router-link>
          <a target="_blank" href="https://github.com/PanJiaChen/vue-element-admin/">
            <el-dropdown-item>Github</el-dropdown-item>
          </a>
          <a target="_blank" href="https://panjiachen.github.io/vue-element-admin-site/#/">
            <el-dropdown-item>Docs</el-dropdown-item>
          </a> -->
          <el-dropdown-item divided>
            <span style="display:block;" @click="logout">Log Out</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { getTask } from '@/api/task'
import { getCounts } from '@/api/admin'
import { isSuccessful } from '@/utils'
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import Hamburger from '@/components/Hamburger'
import ErrorLog from '@/components/ErrorLog'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'

export default {
  components: {
    Breadcrumb,
    Hamburger,
    ErrorLog,
    Screenfull,
    SizeSelect,
    Search
  },
  data() {
    return {
      taskCount: 0,
      errorCount: 0,
      total: 0,
      list: [],
      params: {
        page: 1,
        limit: 3
      }
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device'
    ])
  },
  created: function() {
    var that = this
    that.fetchCounts()
    this.timer = setInterval(() => {
      that.fetchCounts()
    }, 30000)
  },
  destroyed: function() {
    clearInterval(this.timer)
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      await this.$store.dispatch('user/logout')
      // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
      this.$router.push(`/login?redirect=dashboard`)
    },
    open() {
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
      return temp
    },
    fetchCounts() {
      getCounts().then(response => {
        if (isSuccessful(response)) {
          this.taskCount = response.data.taskCount
          this.errorCount = response.data.errorCount
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color:transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>
