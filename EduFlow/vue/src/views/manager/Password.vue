<template>
  <div class="password-container">
    <el-card class="password-card" shadow="hover">
      <div slot="header" class="card-header">
        <i class="el-icon-lock"></i>
        <span class="header-title">Change Password</span>
      </div>

      <el-form
          ref="formRef"
          :model="user"
          :rules="rules"
          label-width="140px"
          class="password-form"
          @submit.native.prevent>

        <el-form-item label="Current Password" prop="password">
          <el-input
              show-password
              v-model="user.password"
              placeholder="Enter current password"
              prefix-icon="el-icon-lock"
              clearable>
          </el-input>
        </el-form-item>

        <el-form-item label="New Password" prop="newPassword">
          <el-input
              show-password
              v-model="user.newPassword"
              placeholder="Enter new password"
              prefix-icon="el-icon-key"
              clearable>
          </el-input>
        </el-form-item>

        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input
              show-password
              v-model="user.confirmPassword"
              placeholder="Confirm new password"
              prefix-icon="el-icon-check"
              clearable
              @keyup.enter.native="update">
          </el-input>
        </el-form-item>

        <div class="form-actions">
          <el-button
              type="primary"
              @click="update"
              :loading="loading"
              icon="el-icon-check"
              size="medium">
            {{ loading ? 'Updating...' : 'Update Password' }}
          </el-button>
          <el-button
              @click="resetForm"
              icon="el-icon-refresh"
              size="medium">
            Reset
          </el-button>
        </div>
      </el-form>
    </el-card>

    <!-- Security Tips -->
    <el-card class="tips-card" shadow="never">
      <div slot="header" class="tips-header">
        <i class="el-icon-info"></i>
        <span>Security Tips</span>
      </div>
      <div class="tips-content">
        <p><i class="el-icon-check"></i> Use at least 8 characters</p>
        <p><i class="el-icon-check"></i> Include uppercase and lowercase letters</p>
        <p><i class="el-icon-check"></i> Include numbers and special characters</p>
        <p><i class="el-icon-check"></i> Don't use common passwords</p>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "Password",
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please confirm your password'))
      } else if (value !== this.user.newPassword) {
        callback(new Error('Password confirmation does not match'))
      } else {
        callback()
      }
    }

    const validateNewPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please enter a new password'))
      } else if (value.length < 6) {
        callback(new Error('Password must be at least 6 characters'))
      } else if (value === this.user.password) {
        callback(new Error('New password must be different from current password'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        password: [
          { required: true, message: 'Please enter your current password', trigger: 'blur' },
          { min: 1, message: 'Current password cannot be empty', trigger: 'blur' }
        ],
        newPassword: [
          { validator: validateNewPassword, required: true, trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePassword, required: true, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // Initialize user data if needed
    if (!this.user.password) this.user.password = ''
    if (!this.user.newPassword) this.user.newPassword = ''
    if (!this.user.confirmPassword) this.user.confirmPassword = ''
  },
  methods: {
    update() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.loading = true

          this.$request.put('/updatePassword', this.user).then(res => {
            if (res.code === '200') {
              localStorage.removeItem('xm-user')
              this.$message({
                message: 'Password updated successfully! Please login again.',
                type: 'success',
                duration: 3000
              })

              // Delay redirect to show success message
              setTimeout(() => {
                this.$router.push('/login')
              }, 1500)
            } else {
              this.$message.error(res.msg || 'Failed to update password')
            }
          }).catch(err => {
            console.error('Password update error:', err)
            this.$message.error('Network error, please try again')
          }).finally(() => {
            this.loading = false
          })
        } else {
          this.$message.warning('Please fill in all required fields correctly')
        }
      })
    },

    resetForm() {
      this.$refs.formRef.resetFields()
      this.user.password = ''
      this.user.newPassword = ''
      this.user.confirmPassword = ''
      this.$message.info('Form has been reset')
    }
  }
}
</script>

<style scoped>
.password-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  padding: 40px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  gap: 30px;
}

.password-card {
  width: 100%;
  max-width: 500px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.card-header i {
  font-size: 20px;
  color: #409eff;
}

.password-form {
  padding: 20px 30px 10px;
}

.form-actions {
  text-align: center;
  margin-top: 30px;
  margin-bottom: 20px;
}

.form-actions .el-button {
  margin: 0 10px;
  padding: 12px 30px;
  border-radius: 6px;
  font-weight: 500;
}

.tips-card {
  width: 100%;
  max-width: 350px;
  border-radius: 12px;
  border: 1px solid #e8f4fd;
  background: rgba(255, 255, 255, 0.95);
}

.tips-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
}

.tips-content {
  padding: 10px 0;
}

.tips-content p {
  margin: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.tips-content i {
  color: #67c23a;
  font-size: 12px;
}

/* Form styling enhancements */
/deep/ .el-form-item__label {
  font-weight: 600;
  color: #333;
}

/deep/ .el-input__inner {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s ease;
}

/deep/ .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

/deep/ .el-card__header {
  background: #f8f9fa;
  border-bottom: 1px solid #ebeef5;
  padding: 20px;
}

/deep/ .el-button--primary {
  background: linear-gradient(45deg, #409eff, #66b3ff);
  border: none;
  transition: all 0.3s ease;
}

/deep/ .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(64, 158, 255, 0.3);
}

/* Responsive design */
@media (max-width: 768px) {
  .password-container {
    flex-direction: column;
    padding: 20px 15px;
  }

  .password-card,
  .tips-card {
    max-width: 100%;
  }

  .password-form {
    padding: 15px 20px;
  }

  /deep/ .el-form-item__label {
    width: 120px !important;
  }
}

@media (max-width: 480px) {
  .form-actions .el-button {
    margin: 5px;
    width: 45%;
  }
}
</style>