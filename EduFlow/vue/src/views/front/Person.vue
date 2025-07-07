<template>
  <div class="main-content">
    <div class="profile-container">
      <!-- Profile Card -->
      <el-card class="profile-card">
        <!-- Header Actions -->
        <div class="card-header">
          <h2 class="profile-title">My Profile</h2>
          <div class="header-actions">
            <el-button type="success" class="action-btn" @click="rechargeInit" icon="el-icon-coin">
              Top Up
            </el-button>
            <el-button type="primary" class="action-btn" @click="updatePassword" icon="el-icon-key">
              Change Password
            </el-button>
          </div>
        </div>

        <!-- Profile Form -->
        <el-form :model="user" label-width="100px" class="profile-form">
          <!-- Avatar Upload -->
          <div class="avatar-section">
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/upload'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess">
              <img v-if="user.avatar" :src="user.avatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <i class="el-icon-plus"></i>
                <p>Upload Avatar</p>
              </div>
            </el-upload>
          </div>

          <!-- Form Fields -->
          <el-form-item label="Username" prop="username">
            <el-input v-model="user.username" placeholder="Username" disabled class="custom-input">
              <i slot="prefix" class="el-input__icon el-icon-user"></i>
            </el-input>
          </el-form-item>

          <el-form-item label="Full Name" prop="name">
            <el-input v-model="user.name" placeholder="Full Name" class="custom-input">
              <i slot="prefix" class="el-input__icon el-icon-user-solid"></i>
            </el-input>
          </el-form-item>

          <el-form-item label="Phone" prop="phone">
            <el-input v-model="user.phone" placeholder="Phone Number" class="custom-input">
              <i slot="prefix" class="el-input__icon el-icon-phone"></i>
            </el-input>
          </el-form-item>

          <el-form-item label="Email" prop="email">
            <el-input v-model="user.email" placeholder="Email Address" class="custom-input">
              <i slot="prefix" class="el-input__icon el-icon-message"></i>
            </el-input>
          </el-form-item>

          <el-form-item label="Balance" prop="account">
            <div class="balance-display">
              <span class="currency">RM</span>
              <span class="amount">{{ user.account }}</span>
            </div>
          </el-form-item>

          <!-- Save Button -->
          <div class="form-actions">
            <el-button type="primary" class="save-btn" @click="update" icon="el-icon-check">
              Save Changes
            </el-button>
          </div>
        </el-form>
      </el-card>
    </div>

    <!-- Change Password Dialog -->
    <el-dialog
        title="Change Password"
        :visible.sync="dialogVisible"
        width="400px"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form :model="user" label-width="120px" :rules="rules" ref="formRef" class="password-form">
        <el-form-item label="Current Password" prop="password">
          <el-input show-password v-model="user.password" placeholder="Enter current password" class="custom-input">
            <i slot="prefix" class="el-input__icon el-icon-lock"></i>
          </el-input>
        </el-form-item>
        <el-form-item label="New Password" prop="newPassword">
          <el-input show-password v-model="user.newPassword" placeholder="Enter new password" class="custom-input">
            <i slot="prefix" class="el-input__icon el-icon-key"></i>
          </el-input>
        </el-form-item>
        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input show-password v-model="user.confirmPassword" placeholder="Confirm new password" class="custom-input">
            <i slot="prefix" class="el-input__icon el-icon-key"></i>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save" icon="el-icon-check">Confirm</el-button>
      </div>
    </el-dialog>

    <!-- Add Credits Dialog -->
    <el-dialog
        title="Top Up Balance"
        :visible.sync="rechargeVisible"
        width="400px"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form label-width="120px" class="recharge-form">
        <el-form-item label="Amount" prop="account">
          <el-input v-model="account" placeholder="Enter amount" class="custom-input">
            <template slot="prepend">RM</template>
          </el-input>
        </el-form-item>
        <el-form-item label="Payment Method" prop="type">
          <el-radio-group v-model="type" class="payment-methods">
            <el-radio label="weipay" class="payment-option">
              <i class="el-icon-chat-dot-square"></i>
              WeChat Pay
            </el-radio>
            <el-radio label="alipay" class="payment-option">
              <i class="el-icon-wallet"></i>
              Alipay
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rechargeVisible = false">Cancel</el-button>
        <el-button type="primary" @click="recharge" icon="el-icon-check">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ProfilePage',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please confirm password'))
      } else if (value !== this.user.newPassword) {
        callback(new Error('Password confirmation does not match'))
      } else {
        callback()
      }
    }
    return {
      user: {},
      dialogVisible: false,
      rechargeVisible: false,
      account: null,
      type: 'weipay',
      rules: {
        password: [
          { required: true, message: 'Please enter current password', trigger: 'blur' },
        ],
        newPassword: [
          { required: true, message: 'Please enter new password', trigger: 'blur' },
        ],
        confirmPassword: [
          { validator: validatePassword, required: true, trigger: 'blur' },
        ],
      }
    }
  },
  created() {
    this.initUser()
    this.loadUser()
  },
  methods: {
    initUser() {
      try {
        const userStr = localStorage.getItem('xm-user')
        this.user = userStr ? JSON.parse(userStr) : {}
      } catch (error) {
        this.user = {}
      }
    },
    loadUser() {
      this.$request.get('/user/selectById/' + this.user.id).then(res => {
        if (res.code === '200') {
          this.user = res.data
          localStorage.setItem('xm-user', JSON.stringify(this.user))
        } else {
          this.$message.error(res.msg || 'Failed to load user data')
        }
      }).catch(() => {
        this.$message.error('Failed to load user data')
      })
    },
    rechargeInit() {
      this.account = 500
      this.rechargeVisible = true
    },
    recharge() {
      if (!this.account) {
        this.$message.warning('Please enter credit amount')
        return
      }
      this.$request.get('/user/recharge/' + this.account).then(res => {
        if (res.code === '200') {
          this.$message.success('Credits added successfully')
          this.loadUser()
          this.rechargeVisible = false
        }
      }).catch(() => {
        this.$message.error('Credit addition failed')
      })
    },
    update() {
      this.$request.put('/user/update', this.user).then(res => {
        if (res.code === '200') {
          this.$message.success('Profile updated successfully')
          localStorage.setItem('xm-user', JSON.stringify(this.user))
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg || 'Update failed')
        }
      }).catch(() => {
        this.$message.error('Update failed')
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      this.$set(this.user, 'avatar', response.data)
    },
    updatePassword() {
      this.dialogVisible = true
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request.put('/updatePassword', this.user).then(res => {
            if (res.code === '200') {
              this.$message.success('Password changed successfully')
              this.$router.push('/login')
            } else {
              this.$message.error(res.msg || 'Password change failed')
            }
          }).catch(() => {
            this.$message.error('Password change failed')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.main-content {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  padding: 40px 20px;
}

.profile-container {
  max-width: 600px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.profile-card /deep/ .el-card__body {
  padding: 40px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f8fafc;
}

.profile-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  border-radius: 20px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
}

.avatar-section {
  text-align: center;
  margin-bottom: 40px;
}

.avatar-uploader /deep/ .el-upload {
  border: 3px dashed #8b5cf6;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.avatar-uploader /deep/ .el-upload:hover {
  border-color: #6366f1;
  transform: scale(1.05);
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 120px;
  height: 120px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #6366f1;
  font-size: 14px;
}

.avatar-placeholder i {
  font-size: 28px;
  margin-bottom: 8px;
}

.profile-form /deep/ .el-form-item__label {
  font-weight: 600;
  color: #374151;
}

.custom-input /deep/ .el-input__inner {
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  padding-left: 45px;
  transition: all 0.3s ease;
}

.custom-input /deep/ .el-input__inner:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.custom-input /deep/ .el-input__prefix {
  left: 15px;
}

.balance-display {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
  color: #059669;
  background: #ecfdf5;
  padding: 12px 20px;
  border-radius: 12px;
  border: 2px solid #a7f3d0;
}

.currency {
  margin-right: 8px;
  font-size: 14px;
  color: #047857;
}

.amount {
  font-size: 20px;
}

.form-actions {
  text-align: center;
  margin-top: 30px;
}

.save-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 40px;
  font-weight: 600;
  font-size: 16px;
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.custom-dialog /deep/ .el-dialog {
  border-radius: 16px;
}

.custom-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 20px 30px;
}

.custom-dialog /deep/ .el-dialog__title {
  color: white;
  font-weight: 600;
}

.custom-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.password-form, .recharge-form {
  padding: 20px 0;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-option {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.payment-option:hover {
  border-color: #6366f1;
  background: #f8fafc;
}

.payment-option i {
  margin-right: 8px;
  font-size: 18px;
  color: #6366f1;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content {
    padding: 20px 10px;
  }

  .profile-card /deep/ .el-card__body {
    padding: 30px 20px;
  }

  .card-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .header-actions {
    width: 100%;
    justify-content: center;
  }

  .profile-title {
    font-size: 24px;
  }

  .custom-dialog /deep/ .el-dialog {
    width: 95% !important;
  }
}
</style>