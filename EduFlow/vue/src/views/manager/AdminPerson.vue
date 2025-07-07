<template>
  <div class="profile-container">
    <el-card class="profile-card" shadow="hover">
      <div slot="header" class="card-header">
        <h3 class="card-title">
          <i class="el-icon-user"></i>
          Personal Profile
        </h3>
        <p class="card-subtitle">Manage your account information</p>
      </div>

      <el-form :model="user" label-width="120px" class="profile-form">
        <!-- Avatar Upload Section -->
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/upload'"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
              <div class="avatar-container">
                <img v-if="user.avatar" :src="user.avatar" class="avatar" />
                <div v-else class="avatar-placeholder">
                  <i class="el-icon-plus avatar-uploader-icon"></i>
                  <p class="upload-text">Upload Avatar</p>
                </div>
                <div class="avatar-overlay">
                  <i class="el-icon-camera"></i>
                  <p>Change</p>
                </div>
              </div>
            </el-upload>
          </div>
          <div class="avatar-tips">
            <p class="tip-title">Avatar Guidelines</p>
            <ul class="tip-list">
              <li>Recommended size: 200x200px</li>
              <li>Format: JPG, PNG</li>
              <li>Max size: 2MB</li>
            </ul>
          </div>
        </div>

        <!-- Form Fields -->
        <div class="form-content">
          <el-form-item label="Username" prop="username">
            <el-input
                v-model="user.username"
                placeholder="Username"
                disabled
                prefix-icon="el-icon-user">
              <template slot="suffix">
                <el-tooltip content="Username cannot be changed" placement="top">
                  <i class="el-icon-info"></i>
                </el-tooltip>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item label="Full Name" prop="name">
            <el-input
                v-model="user.name"
                placeholder="Enter your full name"
                prefix-icon="el-icon-user-solid"
                clearable>
            </el-input>
          </el-form-item>

          <el-form-item label="Phone Number" prop="phone">
            <el-input
                v-model="user.phone"
                placeholder="Enter your phone number"
                prefix-icon="el-icon-phone"
                clearable>
            </el-input>
          </el-form-item>

          <el-form-item label="Email Address" prop="email">
            <el-input
                v-model="user.email"
                placeholder="Enter your email address"
                type="email"
                prefix-icon="el-icon-message"
                clearable>
            </el-input>
          </el-form-item>
        </div>

        <!-- Action Buttons -->
        <div class="form-actions">
          <el-button
              type="primary"
              @click="update"
              class="save-btn"
              :loading="loading">
            <i class="el-icon-check"></i>
            Save Changes
          </el-button>
          <el-button
              @click="resetForm"
              class="reset-btn">
            <i class="el-icon-refresh"></i>
            Reset
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "AdminPerson",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      originalUser: {},
      loading: false
    }
  },
  created() {
    // Store original user data for reset functionality
    this.originalUser = JSON.parse(JSON.stringify(this.user))
  },
  methods: {
    update() {
      this.loading = true
      // Save current user information to database
      this.$request.put('/admin/update', this.user).then(res => {
        if (res.code === '200') {
          // Successfully updated
          this.$message.success('Profile updated successfully')

          // Update user information in browser cache
          localStorage.setItem('xm-user', JSON.stringify(this.user))

          // Update original user data
          this.originalUser = JSON.parse(JSON.stringify(this.user))

          // Trigger parent component data update
          this.$emit('update:user')
        } else {
          this.$message.error(res.msg)
        }
      }).catch(error => {
        this.$message.error('Failed to update profile')
        console.error('Update error:', error)
      }).finally(() => {
        this.loading = false
      })
    },
    handleAvatarSuccess(response, file, fileList) {
      // Update user avatar property with uploaded image link
      this.$set(this.user, 'avatar', response.data)
      this.$message.success('Avatar uploaded successfully')
    },
    beforeAvatarUpload(file) {
      const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPGorPNG) {
        this.$message.error('Avatar must be JPG or PNG format!')
      }
      if (!isLt2M) {
        this.$message.error('Avatar size cannot exceed 2MB!')
      }
      return isJPGorPNG && isLt2M
    },
    resetForm() {
      // Reset form to original values
      this.user = JSON.parse(JSON.stringify(this.originalUser))
      this.$message.info('Form has been reset')
    }
  }
}
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f7fa;
}

.profile-card {
  width: 100%;
  max-width: 600px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-card /deep/ .el-card__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-bottom: none;
}

.card-header {
  text-align: center;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: white;
}

.card-subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
  color: white;
}

.profile-form {
  padding: 40px 30px 30px;
  background: white;
}

.avatar-section {
  display: flex;
  gap: 30px;
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 1px solid #e4e7ed;
}

.avatar-wrapper {
  flex-shrink: 0;
}

.avatar-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-container:hover {
  transform: scale(1.05);
}

.avatar-container:hover .avatar-overlay {
  opacity: 1;
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  border: 2px dashed #dcdfe6;
  border-radius: 50%;
  transition: all 0.3s;
}

.avatar-placeholder:hover {
  border-color: #667eea;
  background: #ecf5ff;
}

.avatar-uploader-icon {
  font-size: 32px;
  color: #909399;
  margin-bottom: 8px;
}

.upload-text {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(102, 126, 234, 0.8);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 50%;
}

.avatar-overlay i {
  font-size: 20px;
  margin-bottom: 4px;
}

.avatar-overlay p {
  margin: 0;
  font-size: 12px;
}

.avatar-tips {
  flex: 1;
  padding-left: 20px;
}

.tip-title {
  margin: 0 0 12px 0;
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.tip-list {
  margin: 0;
  padding-left: 16px;
  color: #606266;
  font-size: 13px;
}

.tip-list li {
  margin-bottom: 6px;
}

.form-content {
  margin-bottom: 40px;
}

.profile-form /deep/ .el-form-item {
  margin-bottom: 24px;
}

.profile-form /deep/ .el-form-item__label {
  font-weight: 600;
  color: #303133;
  line-height: 40px;
}

.profile-form /deep/ .el-input__inner {
  height: 44px;
  line-height: 44px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
  font-size: 14px;
}

.profile-form /deep/ .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

.profile-form /deep/ .el-input.is-disabled .el-input__inner {
  background-color: #f5f7fa;
  color: #909399;
}

.profile-form /deep/ .el-input__prefix,
.profile-form /deep/ .el-input__suffix {
  line-height: 44px;
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.save-btn, .reset-btn {
  min-width: 120px;
  height: 44px;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
}

.save-btn:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.reset-btn {
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.reset-btn:hover {
  background: #ecf5ff;
  border-color: #c0c4cc;
  color: #303133;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-uploader /deep/ .el-upload {
  border: none;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 120px;
  height: 120px;
}

/* Responsive Design */
@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }

  .profile-form {
    padding: 30px 20px 20px;
  }

  .avatar-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: 20px;
  }

  .avatar-tips {
    padding-left: 0;
  }

  .form-actions {
    flex-direction: column;
  }

  .save-btn, .reset-btn {
    width: 100%;
  }

  .profile-form /deep/ .el-form-item__label {
    text-align: left;
  }
}

/* Loading Animation */
.save-btn.is-loading {
  pointer-events: none;
}

/* Success Animation */
@keyframes successPulse {
  0% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(102, 126, 234, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(102, 126, 234, 0);
  }
}

.form-success {
  animation: successPulse 0.6s ease-out;
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .profile-container {
    background-color: #1a1a1a;
  }

  .profile-card {
    background: #2d2d2d;
  }

  .profile-form {
    background: #2d2d2d;
  }

  .tip-title {
    color: #ffffff;
  }

  .tip-list {
    color: #cccccc;
  }

  .profile-form /deep/ .el-form-item__label {
    color: #ffffff;
  }

  .profile-form /deep/ .el-input__inner {
    background-color: #333333;
    border-color: #4c4d4f;
    color: #ffffff;
  }

  .profile-form /deep/ .el-input.is-disabled .el-input__inner {
    background-color: #404040;
    color: #cccccc;
  }

  .avatar-placeholder {
    background: #404040;
    border-color: #4c4d4f;
  }

  .upload-text {
    color: #cccccc;
  }

  .avatar-uploader-icon {
    color: #cccccc;
  }

  .reset-btn {
    background: #404040;
    border-color: #4c4d4f;
    color: #cccccc;
  }

  .reset-btn:hover {
    background: #4c4d4f;
    border-color: #666666;
    color: #ffffff;
  }
}
</style>