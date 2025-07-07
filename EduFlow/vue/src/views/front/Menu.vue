<template>
  <div class="main-content">
    <div class="container">
      <!-- Page Header -->
      <div class="page-header">
        <h1 class="page-title">Study Plans</h1>
        <p class="page-subtitle">Discover effective study plans and schedules to optimize your learning journey</p>
      </div>

      <!-- Search Section -->
      <div class="search-section">
        <div class="search-container">
          <el-input
              placeholder="Enter study plan name to search"
              class="search-input"
              v-model="name"
              clearable>
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button
              type="primary"
              class="search-btn"
              @click="loadMenu"
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

      <!-- Study Plans Grid -->
      <div class="plans-grid">
        <el-row :gutter="30">
          <el-col :span="6" v-for="item in menuData" :key="item.id" class="plan-col">
            <div class="plan-card">
              <div class="image-container">
                <img :src="item.img" alt="Study Plan Image" class="plan-image">
                <div class="image-overlay">
                  <i class="el-icon-date plan-icon"></i>
                  <span>View Plan</span>
                </div>
              </div>

              <div class="plan-info">
                <h3 class="plan-name">{{ item.name }}</h3>
                <div class="plan-meta">
                  <i class="el-icon-time"></i>
                  <span>Published: {{ item.time }}</span>
                </div>
                <el-button
                    type="primary"
                    class="view-btn"
                    @click="viewDescri(item.content)"
                    icon="el-icon-date">
                  View Study Plan
                </el-button>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- Empty State -->
        <div v-if="menuData.length === 0" class="empty-state">
          <i class="el-icon-date"></i>
          <p>No study plans found</p>
        </div>
      </div>
    </div>

    <!-- Study Plan Detail Dialog -->
    <el-dialog
        title="Study Plan Details"
        :visible.sync="fromVisible"
        width="60%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <div class="plan-content-wrapper">
        <div v-html="viewData" class="plan-content w-e-text w-e-text-container"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import E from 'wangeditor'

export default {
  name: 'StudyPlansList',
  data() {
    return {
      user: {},
      menuData: [],
      fromVisible: false,
      name: null,
      viewData: null,
    }
  },
  mounted() {
    this.initUser()
    this.loadMenu()
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
    loadMenu() {
      this.$request.get('/menu/selectAll', {
        params: { name: this.name }
      }).then(res => {
        if (res.code === '200') {
          this.menuData = res.data
        } else {
          this.$message.error(res.msg || 'Failed to load study plans')
        }
      }).catch(() => {
        this.$message.error('Failed to load study plans')
      })
    },
    viewDescri(content) {
      this.viewData = content
      this.fromVisible = true
    },
    reset() {
      this.name = null
      this.loadMenu()
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

.page-header {
  text-align: center;
  margin-bottom: 40px;
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 15px 0;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: #6b7280;
  margin: 0;
}

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
  gap: 15px;
}

.search-input {
  width: 350px;
}

.search-input /deep/ .el-input__inner {
  border-radius: 25px;
  height: 45px;
  border: 2px solid #e5e7eb;
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

.plans-grid {
  margin-top: 40px;
}

.plan-col {
  margin-bottom: 40px;
}

.plan-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.4s ease;
  height: 100%;
}

.plan-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 60px rgba(99, 102, 241, 0.15);
}

.image-container {
  position: relative;
  overflow: hidden;
  height: 220px;
}

.plan-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s ease;
  cursor: pointer;
}

.plan-card:hover .plan-image {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.8) 0%, rgba(139, 92, 246, 0.8) 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  color: white;
}

.plan-card:hover .image-overlay {
  opacity: 1;
}

.plan-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.plan-info {
  padding: 25px;
}

.plan-name {
  font-size: 18px;
  font-weight: 700;
  color: #374151;
  margin: 0 0 15px 0;
  line-height: 1.4;
}

.plan-meta {
  display: flex;
  align-items: center;
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 20px;
}

.plan-meta i {
  margin-right: 8px;
  color: #6366f1;
}

.view-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 20px;
  padding: 12px 30px;
  font-weight: 600;
  width: 100%;
  transition: all 0.3s ease;
}

.view-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #9ca3af;
}

.empty-state i {
  font-size: 80px;
  margin-bottom: 20px;
  color: #8b5cf6;
}

.custom-dialog /deep/ .el-dialog {
  border-radius: 16px;
}

.custom-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 25px 30px;
}

.custom-dialog /deep/ .el-dialog__title {
  color: white;
  font-weight: 600;
}

.custom-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.plan-content-wrapper {
  background: #f8fafc;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e5e7eb;
}

.plan-content {
  line-height: 1.8;
  color: #4b5563;
}

/* Responsive */
@media (max-width: 1200px) {
  .plan-col { width: 33.333333%; }
}

@media (max-width: 992px) {
  .container { width: 95%; }
  .search-container { flex-direction: column; gap: 15px; }
  .search-input { width: 100%; max-width: 400px; }
  .plan-col { width: 50%; }
}

@media (max-width: 768px) {
  .main-content { padding: 20px 0; }
  .page-header { padding: 30px 20px; }
  .page-title { font-size: 28px; }
  .search-section { padding: 20px; }
  .plan-col { width: 100%; }
  .custom-dialog /deep/ .el-dialog { width: 95% !important; }
}
</style>

<style>
.plan-content p {
  color: #4b5563 !important;
  margin-bottom: 16px;
  line-height: 1.8;
}

.plan-content h1,
.plan-content h2,
.plan-content h3 {
  color: #374151 !important;
  font-weight: 600;
  margin: 24px 0 16px 0;
}

.plan-content h2 {
  color: #6366f1 !important;
}

.plan-content strong {
  color: #6366f1 !important;
  font-weight: 700;
}

.plan-content ul,
.plan-content ol {
  margin: 16px 0;
  padding-left: 30px;
}

.plan-content li {
  margin-bottom: 8px;
  color: #4b5563;
}

.plan-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.plan-content blockquote {
  border-left: 4px solid #6366f1;
  background: #f8fafc;
  padding: 16px 20px;
  margin: 20px 0;
  border-radius: 0 8px 8px 0;
}

.plan-content code {
  background: #f1f3f4;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e53e3e;
}

.plan-content pre {
  background: #2d3748;
  color: #e2e8f0;
  padding: 20px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
}
</style>