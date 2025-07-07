<template>
  <div class="admin-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h2>Expert Teachers Management</h2>
      <p>Browse and manage our team of certified education professionals</p>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-wrapper">
        <el-input
            placeholder="Search teachers by name or phone"
            class="search-input"
            prefix-icon="el-icon-search"
            clearable>
        </el-input>
        <el-button type="primary" class="search-btn">
          <i class="el-icon-search"></i> Search
        </el-button>
        <el-button type="info" class="reset-btn">
          <i class="el-icon-refresh"></i> Reset
        </el-button>
      </div>
    </div>

    <!-- 操作区域 -->
    <div class="operation-section">
      <div class="operation-left">
        <el-button type="primary" class="add-btn">
          <i class="el-icon-plus"></i> Add Teacher
        </el-button>
        <el-button type="info" class="view-btn">
          <i class="el-icon-view"></i> View All
        </el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="table-section">
      <!-- Content Section -->
      <div class="content-container">
        <div class="section-header">
          <h2 class="section-title">Our Expert Teachers</h2>
          <p class="section-description">Learn from our team of certified education professionals</p>
        </div>

        <!-- Teacher Cards -->
        <div class="teachers-grid">
          <el-row :gutter="24">
            <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in coachData" :key="item.id" class="teacher-col">
              <div class="teacher-card">
                <div class="teacher-image-container" @click="$router.push('/front/coachDetail?id=' + item.id)">
                  <img
                      :src="item.avatar"
                      alt="Teacher Photo"
                      class="teacher-image"
                  >
                  <div class="image-overlay">
                    <span class="view-details">View Profile</span>
                  </div>
                </div>
                <div class="teacher-info">
                  <h3 class="teacher-name">{{ item.name }}</h3>
                  <div class="teacher-contact">
                    <span class="phone-info">
                      <i class="el-icon-phone"></i>
                      {{ item.phone }}
                    </span>
                  </div>
                  <el-button
                      type="primary"
                      class="enroll-btn"
                      @click="reserveInit(item.id)"
                      round
                  >
                    <i class="el-icon-edit-outline"></i>
                    Enroll Now
                  </el-button>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 分页组件 -->
      <div class="pagination-wrapper">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="100"
            :page-sizes="[8, 16, 24, 32]"
            :page-size="8"
            class="custom-pagination">
        </el-pagination>
      </div>
    </div>

    <!-- Enrollment Dialog -->
    <el-dialog
        title="Course Enrollment"
        :visible.sync="fromVisible"
        width="500px"
        :close-on-click-modal="false"
        destroy-on-close
        class="enrollment-dialog custom-dialog"
    >
      <el-form label-width="120px" class="enrollment-form custom-form">
        <el-form-item prop="content" label="Learning Goals">
          <el-input
              type="textarea"
              :rows="4"
              v-model="content"
              placeholder="Tell us about your learning objectives, preferred study methods, or any specific requirements..."
              autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i>
          Cancel
        </el-button>
        <el-button type="primary" @click="submit" class="submit-btn confirm-btn">
          <i class="el-icon-check"></i>
          Confirm Enrollment
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      coachData: [],
      content: null,
      coachId: null,
      fromVisible: false,
    }
  },
  mounted() {
    this.loadCoach()
  },
  methods: {
    loadCoach() {
      this.$request.get('/coach/selectAll').then(res => {
        if (res.code === '200') {
          this.coachData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reserveInit(coachId) {
      if (this.user.role !== 'USER') {
        this.$message.warning('Access denied: Only students can enroll in courses')
        return
      }
      this.coachId = coachId
      this.fromVisible = true
    },
    submit() {
      const data = {
        userId: this.user.id,
        coachId: this.coachId,
        content: this.content,
      }
      this.$request.post('/reserve/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Enrollment confirmed! Your teacher will contact you soon.')
          this.coachId = null
          this.content = null
          this.fromVisible = false
        }
      })
    }
  }
}
</script>

<style scoped>
/* 管理员容器基础样式 */
.admin-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 页面头部样式 */
.page-header {
  margin-bottom: 24px;
  padding: 0 4px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

/* 搜索区域样式 */
.search-section {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 280px;
}

.search-btn, .reset-btn {
  min-width: 100px;
}

/* 操作区域样式 */
.operation-section {
  background: white;
  padding: 20px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.operation-left {
  display: flex;
  gap: 12px;
}

.add-btn, .view-btn {
  min-width: 120px;
}

/* 主表格区域 */
.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* Content Section - 保留原始教师卡片布局 */
.content-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 60px 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 50px;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.section-description {
  font-size: 1.1rem;
  color: #7f8c8d;
  max-width: 600px;
  margin: 0 auto;
}

/* Teacher Cards - 保留原始卡片样式 */
.teachers-grid {
  margin-top: 40px;
}

.teacher-col {
  margin-bottom: 30px;
}

.teacher-card {
  background: white;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
  height: 100%;
}

.teacher-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.teacher-image-container {
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.teacher-image {
  width: 100%;
  height: 280px;
  object-fit: cover;
  transition: transform 0.3s ease;
  pointer-events: none; /* 防止图片阻挡容器的点击事件 */
}

.teacher-image-container:hover .teacher-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none; /* 确保覆盖层不会阻挡点击 */
}

.teacher-image-container:hover .image-overlay {
  opacity: 1;
}

.view-details {
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.teacher-info {
  padding: 25px;
  text-align: center;
}

.teacher-name {
  font-size: 1.4rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 15px;
}

.teacher-contact {
  margin-bottom: 20px;
}

.phone-info {
  color: #7f8c8d;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.enroll-btn {
  width: 100%;
  padding: 12px 0;
  font-size: 1rem;
  font-weight: 600;
  background: linear-gradient(45deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  transition: all 0.3s ease;
}

.enroll-btn:hover {
  background: linear-gradient(45deg, #4338ca 0%, #6d28d9 100%);
  transform: translateY(-2px);
}

/* 分页样式 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.custom-pagination {
  display: flex;
  justify-content: center;
}

/* Dialog Styles - 保留原始对话框样式并融合管理系统风格 */
.enrollment-dialog .el-dialog {
  border-radius: 15px;
}

.custom-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin: 0;
  padding: 20px 30px;
  border-radius: 15px 15px 0 0;
}

.custom-dialog /deep/ .el-dialog__title {
  color: white;
  font-size: 1.3rem;
  font-weight: 600;
}

.custom-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.custom-dialog /deep/ .el-dialog__body {
  padding: 30px;
}

.enrollment-form {
  padding: 0;
}

.custom-form /deep/ .el-form-item__label {
  font-weight: 600;
  color: #303133;
}

.enrollment-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: border-color 0.3s ease;
}

.enrollment-form .el-textarea__inner:focus {
  border-color: #4f46e5;
}

.custom-form /deep/ .el-input__inner,
.custom-form /deep/ .el-textarea__inner {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.custom-form /deep/ .el-input__inner:focus,
.custom-form /deep/ .el-textarea__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 20px 30px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  margin: 0 -30px -30px -30px;
  border-radius: 0 0 15px 15px;
}

.cancel-btn {
  margin-right: 0;
  padding: 10px 25px;
  border-radius: 25px;
  min-width: 100px;
}

.submit-btn {
  padding: 10px 30px;
  border-radius: 25px;
  background: linear-gradient(45deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  font-weight: 600;
  min-width: 100px;
}

.submit-btn:hover {
  background: linear-gradient(45deg, #4338ca 0%, #6d28d9 100%);
}

.confirm-btn {
  padding: 10px 20px;
  border-radius: 6px;
}

/* 按钮悬停效果 */
.search-btn:hover, .reset-btn:hover, .add-btn:hover, .view-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 加载和空状态 */
.table-section /deep/ .el-loading-mask {
  background-color: rgba(255, 255, 255, 0.9);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-container {
    padding: 16px;
  }

  .search-wrapper {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .operation-left {
    flex-direction: column;
  }

  .add-btn, .view-btn {
    width: 100%;
  }

  .section-title {
    font-size: 2rem;
  }

  .content-container {
    padding: 40px 15px;
  }

  .teacher-card {
    margin-bottom: 20px;
  }
}

@media (max-width: 480px) {
  .enrollment-dialog {
    width: 90% !important;
  }

  .custom-dialog {
    width: 90% !important;
  }
}

/* 成功状态 */
.table-section /deep/ .success-row {
  background-color: #f0f9ff;
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .admin-container {
    background-color: #1a1a1a;
  }

  .search-section, .operation-section, .table-section {
    background: #2d2d2d;
    color: #ffffff;
  }

  .teacher-card {
    background: #2d2d2d;
  }

  .teacher-info {
    background: #333333;
  }

  .page-header h2 {
    color: #ffffff;
  }

  .page-header p {
    color: #cccccc;
  }

  .section-title {
    color: #ffffff;
  }

  .section-description {
    color: #cccccc;
  }

  .teacher-name {
    color: #ffffff;
  }

  .phone-info {
    color: #cccccc;
  }
}

/* 空文本样式 */
.table-section /deep/ .el-table__empty-text {
  color: #909399;
  font-size: 14px;
}

/* 其他保留的原始样式 */
.username-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username-cell i {
  color: #409eff;
}

.phone-cell, .email-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.phone-cell i {
  color: #67c23a;
}

.email-cell i {
  color: #e6a23c;
}

.empty-text {
  color: #c0c4cc;
  font-style: italic;
}

.avatar-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-preview {
  cursor: pointer;
  transition: transform 0.2s;
}

.avatar-preview:hover {
  transform: scale(1.1);
}

.action-btn {
  margin: 0 4px;
}

.action-btn:hover {
  transform: translateY(-1px);
}

/* 表格样式 */
.custom-table {
  width: 100%;
}

.custom-table /deep/ .table-header {
  background: #f8f9fa !important;
}

.custom-table /deep/ .table-header th {
  background: #f8f9fa !important;
  color: #606266 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #e4e7ed !important;
}

.custom-table /deep/ .el-table__row:hover {
  background-color: #f5f7fa;
}

/* 头像上传相关样式 */
.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.avatar-uploader /deep/ .el-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader /deep/ .el-upload:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
}

.avatar-upload-tip {
  flex: 1;
}

.avatar-upload-tip p {
  margin: 0 0 4px 0;
  color: #606266;
  font-size: 14px;
}

.tip-text {
  color: #909399 !important;
  font-size: 12px !important;
}

.avatar-preview-content {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.preview-image {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
}
</style>