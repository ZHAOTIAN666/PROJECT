<template>
  <div class="main-content">
    <div class="container">
      <!-- Search Section -->
      <div class="search-section">
        <div class="search-container">
          <el-input
              placeholder="Enter resource name to search"
              class="search-input"
              v-model="name"
              clearable>
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button
              type="primary"
              class="search-btn"
              @click="loadEquipment"
              icon="el-icon-search">
            Search
          </el-button>
          <el-button
              type="warning"
              plain
              class="reset-btn"
              @click="reset"
              icon="el-icon-refresh-left">
            Reset
          </el-button>
        </div>
      </div>

      <!-- Resource Grid Section -->
      <div class="resource-grid">
        <el-row :gutter="30">
          <el-col :span="6" v-for="item in equipmentData" :key="item.id" class="resource-col">
            <div class="resource-card">
              <!-- Resource Image -->
              <div class="image-container">
                <img :src="item.img" alt="Resource Image" class="resource-image">
                <div class="status-overlay">
                  <el-tag
                      :type="item.status === '空闲中' ? 'success' : 'danger'"
                      class="status-tag">
                    {{ getStatusText(item.status) }}
                  </el-tag>
                </div>
              </div>

              <!-- Resource Info -->
              <div class="resource-info">
                <div class="resource-header">
                  <h3 class="resource-name">{{ item.name }}</h3>
                  <div class="resource-status">
                    <el-tag
                        :type="item.status === '空闲中' ? 'success' : 'danger'"
                        size="small">
                      {{ getStatusText(item.status) }}
                    </el-tag>
                  </div>
                </div>

                <div class="resource-details">
                  <div class="detail-row">
                    <div class="detail-item">
                      <i class="el-icon-location-outline"></i>
                      <span class="detail-label">Location:</span>
                      <span class="detail-value">{{ item.location }}</span>
                    </div>
                  </div>
                  <div class="detail-row">
                    <div class="detail-item">
                      <i class="el-icon-tickets"></i>
                      <span class="detail-label">Code:</span>
                      <span class="detail-value">{{ item.code }}</span>
                    </div>
                  </div>
                </div>

                <div class="action-buttons">
                  <el-button
                      type="primary"
                      size="small"
                      class="action-btn info-btn"
                      @click="viewDescri(item.descr)"
                      icon="el-icon-info">
                    Instructions
                  </el-button>
                  <el-button
                      type="success"
                      size="small"
                      class="action-btn reserve-btn"
                      :disabled="item.status === '使用中'"
                      @click="reserveInit(item.id)"
                      icon="el-icon-date">
                    {{ item.status === '使用中' ? 'In Use' : 'Reserve' }}
                  </el-button>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- Empty State -->
        <div v-if="equipmentData.length === 0" class="empty-state">
          <i class="el-icon-folder-opened"></i>
          <p>No learning resources found</p>
          <p class="empty-subtitle">Try adjusting your search criteria</p>
        </div>
      </div>
    </div>

    <!-- Instructions Dialog -->
    <el-dialog
        title="Resource Instructions"
        :visible.sync="fromVisible"
        width="50%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <div class="dialog-content">
        <div class="instruction-content">
          {{ descr }}
        </div>
      </div>
    </el-dialog>

    <!-- Reservation Dialog -->
    <el-dialog
        title="Resource Reservation"
        :visible.sync="reserveVisible"
        width="50%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form label-width="120px" class="reservation-form">
        <el-form-item label="Start Time" prop="start" class="form-item">
          <el-date-picker
              style="width: 100%"
              v-model="start"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="Select start date and time"
              class="custom-date-picker">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="End Time" prop="end" class="form-item">
          <el-date-picker
              style="width: 100%"
              v-model="end"
              type="datetime"
              value-format="yyyy-MM-dd HH:mm:ss"
              placeholder="Select end date and time"
              class="custom-date-picker">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="reserveVisible = false" class="cancel-btn">Cancel</el-button>
        <el-button type="primary" @click="submit" class="submit-btn" icon="el-icon-check">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ResourceList',
  data() {
    return {
      user: {},
      equipmentData: [],
      descr: null,
      fromVisible: false,
      reserveVisible: false,
      name: null,
      start: null,
      end: null,
      equipmentId: null
    }
  },
  mounted() {
    this.initUser()
    this.loadEquipment()
  },
  methods: {
    // 安全地初始化用户数据
    initUser() {
      try {
        const userStr = localStorage.getItem('xm-user')
        this.user = userStr ? JSON.parse(userStr) : {}
      } catch (error) {
        console.error('Failed to parse user data:', error)
        this.user = {}
      }
    },

    loadEquipment() {
      this.$request.get('/equipment/selectAll', {
        params: {
          name: this.name
        }
      }).then(res => {
        if (res.code === '200') {
          this.equipmentData = res.data
        } else {
          this.$message.error(res.msg || 'Failed to load resource data')
        }
      }).catch(error => {
        console.error('Failed to load resources:', error)
        this.$message.error('Failed to load resource data')
      })
    },

    viewDescri(descr) {
      this.descr = descr
      this.fromVisible = true
    },

    reset() {
      this.name = null
      this.loadEquipment()
    },

    reserveInit(equipmentId) {
      if (this.user.role !== 'USER') {
        this.$message.warning('Only students can make reservations')
        return
      }
      this.equipmentId = equipmentId
      this.start = null
      this.end = null
      this.reserveVisible = true
    },

    submit() {
      // 验证输入
      if (!this.start || !this.end) {
        this.$message.warning('Please select both start and end time')
        return
      }

      if (new Date(this.start) >= new Date(this.end)) {
        this.$message.warning('End time must be after start time')
        return
      }

      const data = {
        userId: this.user.id,
        equipmentId: this.equipmentId,
        start: this.start,
        end: this.end,
        status: '待审核'
      }

      this.$request.post('/eqreserve/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Reservation successful! Waiting for admin approval. You can check the result in Resource Reservations.')
          this.reserveVisible = false
          this.equipmentId = null
        } else {
          this.$message.error(res.msg || 'Reservation failed')
        }
      }).catch(error => {
        console.error('Reservation failed:', error)
        this.$message.error('Reservation failed, please try again')
      })
    },

    // 获取状态的英文文本
    getStatusText(status) {
      const statusMap = {
        '空闲中': 'Available',
        '使用中': 'In Use'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style scoped>
.main-content {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  padding: 40px 0;
}

.container {
  width: 85%;
  max-width: 1400px;
  margin: 0 auto;
}

/* Search Section */
.search-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 40px;
}

.search-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.search-input {
  width: 350px;
}

.search-input /deep/ .el-input__inner {
  border-radius: 25px;
  height: 45px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.search-input /deep/ .el-input__inner:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.search-btn {
  height: 45px;
  padding: 0 25px;
  border-radius: 25px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.reset-btn {
  height: 45px;
  padding: 0 25px;
  border-radius: 25px;
  border: 2px solid #f59e0b;
  color: #f59e0b;
  font-weight: 600;
  transition: all 0.3s ease;
}

.reset-btn:hover {
  background: #f59e0b;
  color: white;
  transform: translateY(-2px);
}

/* Resource Grid */
.resource-grid {
  margin-top: 40px;
}

.resource-col {
  margin-bottom: 40px;
}

.resource-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.4s ease;
  height: 100%;
}

.resource-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

/* Image Container */
.image-container {
  position: relative;
  overflow: hidden;
  height: 280px;
}

.resource-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s ease;
}

.resource-card:hover .resource-image {
  transform: scale(1.05);
}

.status-overlay {
  position: absolute;
  top: 15px;
  right: 15px;
}

.status-tag {
  font-weight: 600;
  padding: 8px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Resource Info */
.resource-info {
  padding: 25px;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.resource-name {
  flex: 1;
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
  margin: 0;
  line-height: 1.4;
}

.resource-status {
  margin-left: 15px;
}

.resource-details {
  margin-bottom: 25px;
}

.detail-row {
  margin-bottom: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  color: #718096;
  font-size: 14px;
}

.detail-item i {
  margin-right: 8px;
  color: #6366f1;
  font-size: 16px;
  width: 18px;
}

.detail-label {
  font-weight: 600;
  margin-right: 8px;
}

.detail-value {
  color: #4a5568;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
  border-radius: 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.info-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
}

.reserve-btn {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border: none;
}

.action-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

/* Empty State */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #a0aec0;
}

.empty-state i {
  font-size: 80px;
  margin-bottom: 20px;
  display: block;
}

.empty-state p {
  font-size: 18px;
  margin-bottom: 8px;
  color: #718096;
}

.empty-subtitle {
  font-size: 14px;
  color: #a0aec0;
}

/* Dialog Styles */
.custom-dialog /deep/ .el-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.custom-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 20px 30px;
}

.custom-dialog /deep/ .el-dialog__title {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.custom-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.custom-dialog /deep/ .el-dialog__body {
  padding: 30px;
}

.dialog-content {
  padding: 0;
}

.instruction-content {
  background: #f8f9fc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  line-height: 1.6;
  color: #4a5568;
  font-size: 16px;
}

.reservation-form {
  padding: 0;
}

.form-item {
  margin-bottom: 25px;
}

.custom-date-picker /deep/ .el-input__inner {
  border-radius: 8px;
  height: 40px;
}

.dialog-footer {
  text-align: right;
  padding: 20px 0 0 0;
  border-top: 1px solid #e2e8f0;
}

.cancel-btn {
  border-radius: 20px;
  padding: 10px 25px;
}

.submit-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 20px;
  padding: 10px 25px;
  font-weight: 600;
}

/* Responsive Design */
@media (max-width: 1200px) {
  .resource-col {
    width: 33.333333%;
  }
}

@media (max-width: 992px) {
  .container {
    width: 95%;
  }

  .search-container {
    flex-direction: column;
    gap: 15px;
  }

  .search-input {
    width: 100%;
    max-width: 400px;
  }

  .resource-col {
    width: 50%;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px 0;
  }

  .search-section {
    padding: 20px;
    margin-bottom: 30px;
  }

  .resource-col {
    width: 100%;
  }

  .resource-header {
    flex-direction: column;
    gap: 15px;
  }

  .resource-status {
    margin-left: 0;
    align-self: flex-start;
  }

  .action-buttons {
    flex-direction: column;
  }

  .custom-dialog /deep/ .el-dialog {
    width: 95% !important;
  }
}

@media (max-width: 576px) {
  .search-btn,
  .reset-btn {
    width: 100%;
    max-width: 200px;
  }
}
</style>