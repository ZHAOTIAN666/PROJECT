<template>
  <div class="manager-container">
    <!-- Header -->
    <div class="manager-header">
      <div class="manager-header-left">
        <img src="@/assets/imgs/EduFlow.png" />
        <div class="title">EduFlow Admin Panel</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <el-dropdown placement="bottom">
          <div class="avatar">
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div>{{ user.name || 'Administrator' }}</div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="goToPerson">Profile</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/password')">Change Password</el-dropdown-item>
            <el-dropdown-item @click.native="logout">Sign Out</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- Main Content -->
    <div class="manager-main">
      <!-- Sidebar -->
      <div class="manager-main-left">
        <el-menu :default-openeds="['info', 'user']" router style="border: none" :default-active="$route.path">
          <el-menu-item index="/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">Dashboard</span>
          </el-menu-item>
          <el-submenu index="info">
            <template slot="title">
              <i class="el-icon-menu"></i><span>Content Management</span>
            </template>
            <el-menu-item index="/notice">Announcements</el-menu-item>
            <el-menu-item index="/reserve">Course Enrollments</el-menu-item>
            <el-menu-item index="/course">Online Courses</el-menu-item>
            <el-menu-item index="/orders">Course Orders</el-menu-item>
            <el-menu-item index="/equipment">Learning Resources</el-menu-item>
            <el-menu-item index="/eqReserve">Resource Bookings</el-menu-item>
            <el-menu-item index="/menu">Study Plans</el-menu-item>
            <el-menu-item index="/experience">Student Reviews</el-menu-item>
          </el-submenu>

          <el-submenu index="user">
            <template slot="title">
              <i class="el-icon-menu"></i><span>User Management</span>
            </template>
            <el-menu-item index="/admin">Administrator Info</el-menu-item>
            <el-menu-item index="/coach">Teacher Info</el-menu-item>
            <el-menu-item index="/user">Student Info</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>

      <!-- Data Table Area -->
      <div class="manager-main-right">
        <router-view @update:user="updateUser" />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Manager",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/login')
    }
  },
  methods: {
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')
    },
    goToPerson() {
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      }
    },
    logout() {
      this.$confirm('Are you sure you want to sign out?', 'Confirm Sign Out', {
        confirmButtonText: 'Sign Out',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('xm-user')
        this.$router.push('/login')
        this.$message.success('Signed out successfully')
      }).catch(() => {
        this.$message.info('Sign out cancelled')
      })
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/manager.css";
</style>