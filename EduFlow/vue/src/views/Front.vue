<template>
  <div class="front-layout">
    <!-- Notification Bar -->
    <div class="notification-bar">
      <div class="notification-content">
        <i class="el-icon-bell notification-icon"></i>
        <span class="notification-label">Notice:</span>
        <div class="notification-text">{{ top || 'Welcome to EduFlow Learning Platform!' }}</div>
      </div>
    </div>

    <!-- Header -->
    <div class="front-header">
      <div class="front-header-left">
        <div class="logo-section" @click="$router.push('/front/home')">
          <img src="@/assets/imgs/EduFlow.png" alt="EduFlow Logo" class="logo-image">
          <div class="title">EduFlow Learning Platform</div>
        </div>
      </div>

      <div class="front-header-center">
        <nav class="front-header-nav">
          <el-menu
              :default-active="$route.path"
              mode="horizontal"
              router
              class="main-navigation">
            <el-menu-item index="/front/home" class="nav-item">
              <i class="el-icon-house"></i>
              <span>Home</span>
            </el-menu-item>
            <el-menu-item index="/front/coach" class="nav-item">
              <i class="el-icon-user"></i>
              <span>Teachers</span>
            </el-menu-item>
            <el-menu-item index="/front/course" class="nav-item">
              <i class="el-icon-reading"></i>
              <span>Courses</span>
            </el-menu-item>
            <el-menu-item index="/front/equipment" class="nav-item">
              <i class="el-icon-files"></i>
              <span>Resources</span>
            </el-menu-item>
            <el-menu-item index="/front/menu" class="nav-item">
              <i class="el-icon-document"></i>
              <span>Study Plan</span>
            </el-menu-item>
            <el-menu-item index="/front/experience" class="nav-item">
              <i class="el-icon-chat-dot-round"></i>
              <span>Reviews</span>
            </el-menu-item>
          </el-menu>
        </nav>
      </div>

      <div class="front-header-right">
        <!-- Guest User -->
        <div v-if="!user.username" class="guest-actions">
          <el-button
              type="primary"
              size="medium"
              @click="$router.push('/login')"
              icon="el-icon-user">
            Sign In
          </el-button>
          <el-button
              type="success"
              size="medium"
              plain
              @click="$router.push('/register')"
              icon="el-icon-plus">
            Sign Up
          </el-button>
        </div>

        <!-- Logged In User -->
        <div v-else class="user-section">
          <el-dropdown trigger="hover" placement="bottom-end">
            <div class="user-dropdown">
              <el-avatar
                  :size="40"
                  :src="user.avatar"
                  icon="el-icon-user-solid"
                  class="user-avatar">
              </el-avatar>
              <div class="user-info">
                <span class="user-name">{{ user.name || user.username }}</span>
                <i class="el-icon-arrow-down dropdown-arrow"></i>
              </div>
            </div>

            <el-dropdown-menu slot="dropdown" class="user-dropdown-menu">
              <el-dropdown-item v-if="user.role !== 'ADMIN'" icon="el-icon-tickets">
                <div @click="$router.push('/front/reserve')" class="dropdown-link">
                  <i class="el-icon-tickets"></i>
                  <span>My Enrollments</span>
                </div>
              </el-dropdown-item>

              <el-dropdown-item v-if="user.role === 'USER'" icon="el-icon-shopping-bag-1">
                <div @click="$router.push('/front/orders')" class="dropdown-link">
                  <i class="el-icon-shopping-bag-1"></i>
                  <span>My Orders</span>
                </div>
              </el-dropdown-item>

              <el-dropdown-item v-if="user.role === 'USER'" icon="el-icon-date">
                <div @click="$router.push('/front/eqReserve')" class="dropdown-link">
                  <i class="el-icon-date"></i>
                  <span>Resource Booking</span>
                </div>
              </el-dropdown-item>

              <el-dropdown-item v-if="user.role !== 'ADMIN'" icon="el-icon-edit-outline">
                <div @click="$router.push('/front/myExperience')" class="dropdown-link">
                  <i class="el-icon-edit-outline"></i>
                  <span>My Reviews</span>
                </div>
              </el-dropdown-item>

              <el-dropdown-item divided icon="el-icon-user">
                <div @click="person" class="dropdown-link">
                  <i class="el-icon-user"></i>
                  <span>Profile Settings</span>
                </div>
              </el-dropdown-item>

              <el-dropdown-item icon="el-icon-switch-button">
                <div @click="logout" class="dropdown-link logout-link">
                  <i class="el-icon-switch-button"></i>
                  <span>Sign Out</span>
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <!-- User Balance Display -->
          <div v-if="user.account !== undefined" class="balance-display">
            <i class="el-icon-wallet"></i>
            <span>Credits: {{ user.account || '0' }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Main Content -->
    <div class="main-body">
      <router-view ref="child" @update:user="updateUser" />
    </div>

    <!-- Footer -->
    <div class="front-footer">
      <div class="footer-content">
        <div class="footer-info">
          <p>&copy; 2025 EduFlow Learning Platform. All rights reserved.</p>
          <p>Making education accessible and enjoyable for everyone.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FrontLayout",
  data() {
    return {
      top: '',
      notice: [],
      user: JSON.parse(localStorage.getItem("xm-user") || '{}'),
      notificationInterval: null
    }
  },
  mounted() {
    this.loadNotice()
  },
  beforeDestroy() {
    if (this.notificationInterval) {
      clearInterval(this.notificationInterval)
    }
  },
  methods: {
    // Load notices from server
    loadNotice() {
      this.$request.get('/notice/selectAll').then(res => {
        this.notice = res.data || []
        this.startNotificationRotation()
      }).catch(err => {
        console.error('Failed to load notices:', err)
        this.top = 'Welcome to EduFlow Learning Platform!'
      })
    },

    // Start notification rotation
    startNotificationRotation() {
      if (this.notice && this.notice.length > 0) {
        let i = 0
        this.top = this.notice[0].content

        if (this.notice.length > 1) {
          this.notificationInterval = setInterval(() => {
            this.top = this.notice[i].content
            i++
            if (i === this.notice.length) {
              i = 0
            }
          }, 3000) // Changed to 3 seconds for better readability
        }
      } else {
        this.top = 'Welcome to EduFlow Learning Platform!'
      }
    },

    // Update user info
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}')
      this.$forceUpdate() // Force re-render to update balance display
    },

    // Logout function
    logout() {
      this.$confirm('Are you sure you want to sign out?', 'Confirm Sign Out', {
        confirmButtonText: 'Sign Out',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem("xm-user")
        this.$router.push("/login")
        this.$message.success('Signed out successfully')
      }).catch(() => {
        this.$message.info('Sign out cancelled')
      })
    },

    // Navigate to profile page
    person() {
      if (this.user.role === 'COACH') {
        this.$router.push('/front/coachPerson')
      } else {
        this.$router.push('/front/person')
      }
    }
  }
}
</script>

<style scoped>
/* Import base styles */
@import "@/assets/css/front.css";

.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Notification Bar */
.notification-bar {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  padding: 8px 0;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.notification-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  animation: slideIn 0.5s ease-out;
}

.notification-icon {
  animation: bell 2s infinite;
}

.notification-label {
  font-weight: 600;
}

.notification-text {
  font-size: 14px;
  max-width: 600px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Header Styles */
.front-header {
  background: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 1000;
}

.front-header-left {
  flex: 0 0 auto;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.logo-section:hover {
  transform: translateY(-1px);
}

.logo-image {
  height: 40px;
  width: auto;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.front-header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.main-navigation {
  border-bottom: none;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-item:hover {
  color: #4f46e5 !important;
}

.front-header-right {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
  gap: 15px;
}

/* Guest Actions */
.guest-actions {
  display: flex;
  gap: 10px;
}

/* User Section */
.user-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.user-dropdown:hover {
  background: #f5f7fa;
}

.user-avatar {
  border: 2px solid #ebeef5;
  transition: all 0.3s ease;
}

.user-dropdown:hover .user-avatar {
  border-color: #4f46e5;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-name {
  font-weight: 600;
  color: #333;
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dropdown-arrow {
  font-size: 12px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-dropdown:hover .dropdown-arrow {
  transform: rotate(180deg);
}

/* Balance Display */
.balance-display {
  display: flex;
  align-items: center;
  gap: 5px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

/* Dropdown Menu */
.dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  width: 100%;
  color: #333;
  transition: color 0.3s ease;
}

.dropdown-link:hover {
  color: #4f46e5;
}

.logout-link:hover {
  color: #f56c6c;
}

/* Main Body */
.main-body {
  flex: 1;
  background: #f5f7fa;
}

/* Footer */
.front-footer {
  background: #2c3e50;
  color: white;
  text-align: center;
  padding: 20px;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
}

.footer-info p {
  margin: 5px 0;
  font-size: 14px;
}

/* Animations */
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes bell {
  0%, 100% { transform: rotate(0deg); }
  10%, 30%, 50%, 70%, 90% { transform: rotate(-10deg); }
  20%, 40%, 60%, 80% { transform: rotate(10deg); }
}

/* Element UI Overrides */
/deep/ .el-menu--horizontal {
  border-bottom: none;
}

/deep/ .el-menu--horizontal .el-menu-item {
  border-bottom: 2px solid transparent;
  transition: all 0.3s ease;
}

/deep/ .el-menu--horizontal .el-menu-item.is-active {
  border-bottom-color: #4f46e5;
  color: #4f46e5;
}

/deep/ .el-dropdown-menu {
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  border: none;
  padding: 8px 0;
}

/deep/ .el-dropdown-menu__item {
  padding: 12px 20px;
  transition: all 0.3s ease;
}

/deep/ .el-dropdown-menu__item:hover {
  background: #f5f7fa;
}

/deep/ .el-button {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

/deep/ .el-button:hover {
  transform: translateY(-1px);
}

/* Responsive Design */
@media (max-width: 768px) {
  .front-header {
    flex-direction: column;
    padding: 10px;
  }

  .front-header-left,
  .front-header-center,
  .front-header-right {
    width: 100%;
    justify-content: center;
    margin: 5px 0;
  }

  .main-navigation {
    width: 100%;
  }

  .nav-item span {
    display: none;
  }

  .user-name {
    max-width: 80px;
  }

  .notification-text {
    font-size: 12px;
  }

  .guest-actions {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .title {
    font-size: 16px;
  }

  .logo-image {
    height: 30px;
  }

  .balance-display {
    font-size: 12px;
    padding: 4px 8px;
  }
}
</style>