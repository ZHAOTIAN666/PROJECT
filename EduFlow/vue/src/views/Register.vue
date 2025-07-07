<template>
  <div class="register-container">
    <!-- Background overlay -->
    <div class="background-overlay"></div>

    <!-- Register card -->
    <div class="register-card">
      <!-- Header section -->
      <div class="register-header">
        <div class="logo-section">
          <i class="el-icon-edit logo-icon"></i>
          <h2 class="welcome-title">Join EduFlow</h2>
        </div>
        <p class="welcome-subtitle">Create your account to start your learning journey</p>
      </div>

      <!-- Register form -->
      <el-form
          :model="form"
          :rules="rules"
          ref="formRef"
          class="register-form"
          @submit.native.prevent>

        <el-form-item prop="username">
          <el-input
              prefix-icon="el-icon-user"
              placeholder="Choose a username"
              v-model="form.username"
              size="large"
              clearable
              @keyup.enter.native="register">
            <template slot="prepend">
              <i class="el-icon-user"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              prefix-icon="el-icon-lock"
              placeholder="Create a password"
              show-password
              v-model="form.password"
              size="large">
            <template slot="prepend">
              <i class="el-icon-lock"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPass">
          <el-input
              prefix-icon="el-icon-check"
              placeholder="Confirm your password"
              show-password
              v-model="form.confirmPass"
              size="large"
              @keyup.enter.native="register">
            <template slot="prepend">
              <i class="el-icon-check"></i>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="role">
          <el-select
              v-model="form.role"
              placeholder="Select your role"
              size="large"
              style="width: 100%">
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

        <!-- Password strength indicator -->
        <div class="password-strength" v-if="form.password">
          <div class="strength-label">Password Strength:</div>
          <div class="strength-bar">
            <div
                class="strength-fill"
                :class="passwordStrengthClass"
                :style="{ width: passwordStrengthWidth }">
            </div>
          </div>
          <span class="strength-text" :class="passwordStrengthClass">
            {{ passwordStrengthText }}
          </span>
        </div>

        <el-form-item class="register-button-section">
          <el-button
              type="success"
              size="large"
              class="register-button"
              @click="register"
              :loading="loading">
            <i class="el-icon-check register-icon" v-if="!loading"></i>
            {{ loading ? 'Creating Account...' : 'Create Account' }}
          </el-button>
        </el-form-item>

        <!-- Terms and conditions -->
        <div class="terms-section">
          <el-checkbox v-model="acceptTerms" size="small">
            I agree to the
            <el-button type="text" size="mini" class="terms-link">Terms of Service</el-button>
            and
            <el-button type="text" size="mini" class="terms-link">Privacy Policy</el-button>
          </el-checkbox>
        </div>

        <!-- Footer links -->
        <div class="register-footer">
          <div class="login-link">
            <span class="footer-text">Already have an account?</span>
            <el-button
                type="text"
                @click="$router.push('/login')"
                class="login-btn">
              Sign In
            </el-button>
          </div>

          <div class="additional-links">
            <el-button type="text" size="mini" class="link-btn">
              Need Help?
            </el-button>
            <el-divider direction="vertical"></el-divider>
            <el-button type="text" size="mini" class="link-btn">
              Contact Support
            </el-button>
          </div>
        </div>
      </el-form>
    </div>

    <!-- Benefits showcase -->
    <div class="benefits-section">
      <div class="benefit-item">
        <i class="el-icon-medal"></i>
        <span>Expert Instruction</span>
      </div>
      <div class="benefit-item">
        <i class="el-icon-timer"></i>
        <span>Flexible Learning</span>
      </div>
      <div class="benefit-item">
        <i class="el-icon-document"></i>
        <span>Rich Content</span>
      </div>
      <div class="benefit-item">
        <i class="el-icon-star-on"></i>
        <span>Quality Education</span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    const validatePassword = (rule, confirmPass, callback) => {
      if (confirmPass === '') {
        callback(new Error('Please confirm your password'))
      } else if (confirmPass !== this.form.password) {
        callback(new Error('Passwords do not match'))
      } else {
        callback()
      }
    }

    const validateUsername = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please enter a username'))
      } else if (value.length < 3) {
        callback(new Error('Username must be at least 3 characters'))
      } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
        callback(new Error('Username can only contain letters, numbers and underscores'))
      } else {
        callback()
      }
    }

    const validatePasswordStrength = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please enter a password'))
      } else if (value.length < 6) {
        callback(new Error('Password must be at least 6 characters'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      acceptTerms: false,
      form: {
        username: '',
        password: '',
        confirmPass: '',
        role: ''
      },
      rules: {
        username: [
          { validator: validateUsername, trigger: 'blur' }
        ],
        password: [
          { validator: validatePasswordStrength, trigger: 'blur' }
        ],
        confirmPass: [
          { validator: validatePassword, trigger: 'blur' }
        ],
        role: [
          { required: true, message: 'Please select your role', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    passwordStrength() {
      const password = this.form.password
      if (!password) return 0

      let strength = 0
      if (password.length >= 6) strength += 1
      if (password.length >= 8) strength += 1
      if (/[A-Z]/.test(password)) strength += 1
      if (/[0-9]/.test(password)) strength += 1
      if (/[^A-Za-z0-9]/.test(password)) strength += 1

      return strength
    },
    passwordStrengthClass() {
      const strength = this.passwordStrength
      if (strength <= 1) return 'weak'
      if (strength <= 3) return 'medium'
      return 'strong'
    },
    passwordStrengthWidth() {
      return `${(this.passwordStrength / 5) * 100}%`
    },
    passwordStrengthText() {
      const strength = this.passwordStrength
      if (strength <= 1) return 'Weak'
      if (strength <= 3) return 'Medium'
      return 'Strong'
    }
  },
  mounted() {
    // Auto-focus on username field
    this.$nextTick(() => {
      const usernameInput = this.$refs.formRef.$el.querySelector('input[placeholder*="username"]')
      if (usernameInput) {
        usernameInput.focus()
      }
    })
  },
  methods: {
    register() {
      if (!this.acceptTerms) {
        this.$message.warning('Please accept the Terms of Service and Privacy Policy to continue.')
        return
      }

      this.$refs['formRef'].validate((valid) => {
        if (valid) {
          this.loading = true

          this.$request.post('/register', this.form).then(res => {
            if (res.code === '200') {
              this.$message.success('Registration successful! Please sign in with your new account.')

              setTimeout(() => {
                this.$router.push('/login')
              }, 1500)
            } else {
              this.$message.error(res.msg || 'Registration failed. Please try again.')
            }
          }).catch(err => {
            console.error('Registration error:', err)
            this.$message.error('Network error. Please try again later.')
          }).finally(() => {
            this.loading = false
          })
        } else {
          this.$message.warning('Please fill in all required fields correctly.')
        }
      })
    }
  }
}
</script>

<style scoped>
.register-container {
  height: 100vh;
  position: relative;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 25%, #2563eb 75%, #1d4ed8 100%);
  background-image: url("@/assets/imgs/bg1.jpg");
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow-y: auto;
  padding: 20px 0;
}

.background-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
}

.register-card {
  position: relative;
  z-index: 10;
  width: 440px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: slideUp 0.6s ease-out;
  margin: auto;
}

.register-header {
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
  animation: bounce 2s infinite;
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

.register-form {
  margin-bottom: 20px;
}

.password-strength {
  margin: 10px 0 20px;
  font-size: 12px;
}

.strength-label {
  color: #666;
  margin-bottom: 5px;
}

.strength-bar {
  width: 100%;
  height: 4px;
  background: #ebeef5;
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 5px;
}

.strength-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 2px;
}

.strength-fill.weak {
  background: #f56c6c;
}

.strength-fill.medium {
  background: #e6a23c;
}

.strength-fill.strong {
  background: #67c23a;
}

.strength-text {
  font-weight: 600;
  font-size: 11px;
}

.strength-text.weak {
  color: #f56c6c;
}

.strength-text.medium {
  color: #e6a23c;
}

.strength-text.strong {
  color: #67c23a;
}

.register-button-section {
  margin-bottom: 0;
}

.register-button {
  width: 100%;
  height: 48px;
  background: linear-gradient(135deg, #4f46e5, #7c3aed);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(79, 70, 229, 0.4);
}

.register-icon {
  margin-left: 8px;
}

.terms-section {
  text-align: center;
  margin: 20px 0;
  font-size: 12px;
}

.terms-link {
  padding: 0;
  color: #4f46e5;
  font-size: 12px;
}

.terms-link:hover {
  color: #7c3aed;
}

.register-footer {
  text-align: center;
  margin-top: 25px;
}

.login-link {
  margin-bottom: 15px;
}

.footer-text {
  color: #666;
  font-size: 14px;
}

.login-btn {
  color: #4f46e5;
  font-weight: 600;
  padding: 0;
  margin-left: 5px;
}

.login-btn:hover {
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

.benefits-section {
  position: absolute;
  bottom: 30px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 40px;
  z-index: 10;
}

.benefit-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: white;
  font-size: 12px;
  text-align: center;
}

.benefit-item i {
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

/deep/ .el-input-group__prepend {
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-right: none;
  border-radius: 8px 0 0 8px;
}

/deep/ .el-select .el-input__inner {
  cursor: pointer;
}

/deep/ .el-form-item__error {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 5px;
}

/deep/ .el-checkbox__label {
  font-size: 12px;
  color: #666;
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

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

/* Responsive design */
@media (max-width: 768px) {
  .register-card {
    width: 90%;
    max-width: 400px;
    padding: 30px 25px;
    margin: 20px;
  }

  .benefits-section {
    flex-direction: column;
    gap: 20px;
    bottom: 20px;
  }

  .benefit-item {
    flex-direction: row;
    gap: 10px;
  }
}

@media (max-width: 480px) {
  .register-card {
    padding: 25px 20px;
  }

  .welcome-title {
    font-size: 20px;
  }

  .benefits-section {
    display: none;
  }
}
</style>