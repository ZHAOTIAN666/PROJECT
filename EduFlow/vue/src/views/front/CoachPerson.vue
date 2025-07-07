<template>
  <div class="main-content">
    <el-card class="profile-card">
      <div class="card-header">
        <h2 class="profile-title">Profile Settings</h2>
        <el-button
            type="primary"
            @click="updatePassword"
            class="change-password-btn"
            icon="el-icon-lock"
        >
          Change Password
        </el-button>
      </div>

      <el-form :model="user" label-width="120px" class="profile-form">
        <div class="avatar-section">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img v-if="user.avatar" :src="user.avatar" class="avatar" />
            <div v-else class="avatar-placeholder">
              <i class="el-icon-camera avatar-uploader-icon"></i>
              <div class="upload-text">Upload Avatar</div>
            </div>
          </el-upload>
        </div>

        <div class="form-content">
          <el-form-item label="Username" prop="username">
            <el-input
                v-model="user.username"
                placeholder="Enter username"
                disabled
                prefix-icon="el-icon-user"
            ></el-input>
          </el-form-item>

          <el-form-item label="Full Name" prop="name">
            <el-input
                v-model="user.name"
                placeholder="Enter full name"
                prefix-icon="el-icon-user-solid"
            ></el-input>
          </el-form-item>

          <el-form-item label="Phone" prop="phone">
            <el-input
                v-model="user.phone"
                placeholder="Enter phone number"
                prefix-icon="el-icon-phone"
            ></el-input>
          </el-form-item>

          <el-form-item label="Email" prop="email">
            <el-input
                v-model="user.email"
                placeholder="Enter email address"
                prefix-icon="el-icon-message"
            ></el-input>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button
              type="primary"
              @click="update"
              class="save-btn"
              icon="el-icon-check"
              size="medium"
          >
            Save Changes
          </el-button>
        </div>
      </el-form>
    </el-card>

    <el-dialog
        title="Change Password"
        :visible.sync="dialogVisible"
        width="400px"
        :close-on-click-modal="false"
        destroy-on-close
        class="password-dialog"
    >
      <el-form
          :model="user"
          label-width="140px"
          :rules="rules"
          ref="formRef"
          class="password-form"
      >
        <el-form-item label="Current Password" prop="password">
          <el-input
              show-password
              v-model="user.password"
              placeholder="Enter current password"
              prefix-icon="el-icon-lock"
          ></el-input>
        </el-form-item>

        <el-form-item label="New Password" prop="newPassword">
          <el-input
              show-password
              v-model="user.newPassword"
              placeholder="Enter new password"
              prefix-icon="el-icon-key"
          ></el-input>
        </el-form-item>

        <el-form-item label="Confirm Password" prop="confirmPassword">
          <el-input
              show-password
              v-model="user.confirmPassword"
              placeholder="Confirm new password"
              prefix-icon="el-icon-key"
          ></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" class="cancel-btn">Cancel</el-button>
        <el-button type="primary" @click="save" class="confirm-btn">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
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
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      dialogVisible: false,

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

  },
  methods: {
    update() {
      // 保存用户信息到数据库
      this.$request.put('/coach/update', this.user).then(res => {
        if (res.code === '200') {
          // 更新成功
          this.$message.success('Profile updated successfully')
          // 更新浏览器缓存中的用户信息
          localStorage.setItem('xm-user', JSON.stringify(this.user))

          // 触发父组件数据更新
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // 用上传的图片链接替换用户头像属性
      this.$set(this.user, 'avatar', response.data)
    },
    // 修改密码
    updatePassword() {
      this.dialogVisible = true
    },
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request.put('/updatePassword', this.user).then(res => {
            if (res.code === '200') {
              // 更新成功
              this.$message.success('Password updated successfully')
              this.$router.push('/login')
            } else {
              this.$message.error(res.msg)
            }
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.profile-card {
  width: 100%;
  max-width: 600px;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30px 30px 20px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  margin: -20px -20px 30px -20px;
}

.profile-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: white;
}

.change-password-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  color: white;
  border-radius: 25px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.change-password-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
}

.profile-form {
  padding: 0 30px 30px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 40px;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-placeholder {
  width: 120px;
  height: 120px;
  border: 3px dashed #e0e6ed;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.avatar-placeholder:hover {
  border-color: #6366f1;
  transform: scale(1.05);
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #8c939d;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
  color: #8c939d;
  font-weight: 500;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 4px solid #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
}

.avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
}

.form-content {
  display: grid;
  gap: 25px;
}

.form-actions {
  text-align: center;
  margin-top: 40px;
}

.save-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 8px 24px rgba(99, 102, 241, 0.4);
  transition: all 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(99, 102, 241, 0.6);
}

/deep/.el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

/deep/.el-input__inner {
  border-radius: 12px;
  border: 2px solid #e0e6ed;
  padding: 12px 15px;
  font-size: 14px;
  transition: all 0.3s ease;
}

/deep/.el-input__inner:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

/deep/.el-input.is-disabled .el-input__inner {
  background-color: #f8f9fa;
  color: #6c757d;
}

/deep/.password-dialog .el-dialog {
  border-radius: 20px;
  overflow: hidden;
}

/deep/.password-dialog .el-dialog__header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 25px 30px;
}

/deep/.password-dialog .el-dialog__title {
  color: white;
  font-weight: 600;
  font-size: 20px;
}

/deep/.password-dialog .el-dialog__body {
  padding: 30px;
  background: rgba(255, 255, 255, 0.95);
}

.password-form {
  display: grid;
  gap: 20px;
}

.dialog-footer {
  text-align: center;
  padding: 20px 30px 30px;
  background: rgba(255, 255, 255, 0.95);
}

.cancel-btn {
  border-radius: 20px;
  padding: 10px 25px;
  margin-right: 15px;
  border: 2px solid #e0e6ed;
  color: #6c757d;
  font-weight: 500;
}

.cancel-btn:hover {
  border-color: #6366f1;
  color: #6366f1;
}

.confirm-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 25px;
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.4);
}

.confirm-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(99, 102, 241, 0.6);
}

/deep/.el-upload {
  border-radius: 50%;
  overflow: hidden;
}

/deep/.avatar-uploader .el-upload:hover {
  border-color: #6366f1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: 10px;
  }

  .profile-card {
    margin: 0;
  }

  .card-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .profile-title {
    font-size: 24px;
  }

  .profile-form {
    padding: 0 20px 20px;
  }

  /deep/.el-form-item__label {
    width: 100px !important;
  }

  /deep/.password-dialog .el-dialog {
    width: 90% !important;
    margin: 0 5%;
  }
}
</style>