<template>
  <div class="login-container">
    <!-- Background overlay -->
    <div class="background-overlay"></div>

    <!-- Login card -->
    <div class="login-card">
      <!-- Header section -->
      <div class="login-header">
        <div class="logo-section">
          <i class="el-icon-reading logo-icon"></i>
          <h2 class="welcome-title">Welcome to EduFlow</h2>
        </div>
        <p class="welcome-subtitle">Sign in to access your learning journey</p>
      </div>

      <!-- Login form -->
      <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          class="login-form"
          @submit.native.prevent>

        <el-form-item prop="username">
          <el-input
              prefix-icon="el-icon-user"
              placeholder="Enter username"
              v-model="form.username"
              size="large"
              clearable
              @keyup.enter.native="login">
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              prefix-icon="el-icon-lock"
              placeholder="Enter password"
              show-password
              v-model="form.password"
              size="large"
              @keyup.enter.native="login">
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-select
              v-model="form.role"
              placeholder="Select role"
              size="large"
              style="width: 100%">
            <el-option
                label="Administrator"
                value="ADMIN">
              <i class="el-icon-setting"></i>
              <span style="margin-left: 8px;">Administrator</span>
            </el-option>
            <el-option
                label="Teacher"
                value="COACH">
              <i class="el-icon-user-solid"></i>
              <span style="margin-left: 8px;">Teacher</span>
            </el-option>
            <el-option
                label="Student"
                value="USER">
              <i class="el-icon-user"></i>
              <span style="margin-left: 8px;">Student</span>
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item class="login-button-section">
          <el-button
              size="large"
              class="login-button"
              @click="login"
              :loading="loading">
            <i class="el-icon-right login-icon" v-if="!loading"></i>
            {{ loading ? 'Signing in...' : 'Login' }}
          </el-button>
        </el-form-item>

        <!-- Footer links -->
        <div class="login-footer">
          <div class="register-link">
            <span class="footer-text">No account yet?</span>
            <el-button
                type="text"
                @click="$router.push('/register')"
                class="register-btn">
              Register
            </el-button>
          </div>

          <div class="additional-links">
            <el-button type="text" size="mini" class="link-btn">
              Forgot Password?
            </el-button>
            <el-divider direction="vertical"></el-divider>
            <el-button type="text" size="mini" class="link-btn">
              Help Center
            </el-button>
          </div>
        </div>
      </el-form>
    </div>

    <!-- Features showcase -->
    <div class="features-section">
      <div class="feature-item">
        <i class="el-icon-reading"></i>
        <span>Interactive Learning</span>
      </div>
      <div class="feature-item">
        <i class="el-icon-user-solid"></i>
        <span>Expert Teachers</span>
      </div>
      <div class="feature-item">
        <i class="el-icon-date"></i>
        <span>Flexible Schedule</span>
      </div>
      <div class="feature-item">
        <i class="el-icon-files"></i>
        <span>Rich Resources</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Login",
  data() {
    return {
      loading: false,
      form: {},
      dialogVisible: true,
      rules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur' },
        ],
        password: [
          { required: true, message: 'Please enter password', trigger: 'blur' },
        ]
      }
    }
  },
  mounted() {
    // Auto-focus on username field
    this.$nextTick(() => {
      const usernameInput = this.$refs.formRef.$el.querySelector('input')
      if (usernameInput) {
        usernameInput.focus()
      }
    })
  },
  methods: {
    login() {
      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          this.loading = true

          this.$request.post('/login', this.form).then(res => {
            if (res.code === '200') {
              localStorage.setItem("xm-user", JSON.stringify(res.data))
              if (res.data.role === 'ADMIN') {
                this.$router.push('/home')
              } else {
                location.href = '/front/home'
              }
              this.$message.success('Login successful')
            } else {
              this.$message.error(res.msg)
            }
          }).catch(err => {
            console.error('Login error:', err)
            this.$message.error('Network error. Please try again later.')
          }).finally(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 25%, #2563eb 75%, #1d4ed8 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(1px);
}

.login-card {
  position: relative;
  z-index: 10;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.98);
  border-radius: 16px;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  animation: slideUp 0.6s ease-out;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 10px;
}

.logo-icon {
  font-size: 32px;
  color: #4f46e5;
  animation: pulse 2s infinite;
}

.welcome-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  color: #666;
  font-size: 14px;
  margin: 0;
  font-weight: 400;
}

.login-form {
  margin-bottom: 20px;
}

.login-button-section {
  margin-bottom: 0;
}

.login-button {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  color: white;
  transition: all 0.3s ease;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(79, 70, 229, 0.4);
}

.login-icon {
  margin-left: 8px;
}

.login-footer {
  text-align: center;
  margin-top: 25px;
}

.register-link {
  margin-bottom: 15px;
}

.footer-text {
  color: #666;
  font-size: 14px;
}

.register-btn {
  color: #4f46e5;
  font-weight: 600;
  padding: 0;
  margin-left: 5px;
}

.register-btn:hover {
  color: #7c3aed;
}

.additional-links {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.link-btn {
  color: #999;
  font-size: 12px;
  padding: 0;
}

.link-btn:hover {
  color: #4f46e5;
}

.features-section {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 40px;
  z-index: 10;
}

.feature-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 12px;
  text-align: center;
}

.feature-item i {
  font-size: 24px;
  margin-bottom: 5px;
  opacity: 0.9;
}

/* Form styling overrides */
/deep/ .el-form-item {
  margin-bottom: 20px;
}

/deep/ .el-input__inner {
  height: 48px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
  font-size: 14px;
}

/deep/ .el-input__inner:focus {
  border-color: #4f46e5;
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.2);
}

/deep/ .el-select .el-input__inner {
  cursor: pointer;
}

/deep/ .el-form-item__error {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 5px;
}

/* Animations */
@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

/* Responsive design */
@media (max-width: 768px) {
  .login-card {
    width: 90%;
    max-width: 380px;
    padding: 30px 25px;
    margin: 20px;
  }

  .features-section {
    flex-direction: column;
    gap: 20px;
    bottom: 20px;
  }

  .feature-item {
    flex-direction: row;
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .login-card {
    padding: 25px 20px;
  }

  .welcome-title {
    font-size: 20px;
  }

  .features-section {
    display: none;
  }
}
</style>