<template>
  <div class="main-content">
    <div class="container">
      <!-- Course Header Section -->
      <div class="course-header">
        <el-row :gutter="40">
          <el-col :span="8">
            <div class="image-container">
              <img :src="courseData.img" alt="Course Image" class="course-image">
              <div class="image-badge">
                <i class="el-icon-star-on"></i>
                <span>Featured Course</span>
              </div>
            </div>
          </el-col>
          <el-col :span="16">
            <div class="course-info">
              <h1 class="course-title">{{ courseData.name }}</h1>

              <div class="info-grid">
                <div class="info-item">
                  <div class="info-label">
                    <i class="el-icon-user-solid"></i>
                    <span>Teacher</span>
                  </div>
                  <div class="info-value">{{ courseData.coachName }}</div>
                </div>

                <div class="info-item">
                  <div class="info-label">
                    <i class="el-icon-time"></i>
                    <span>Duration</span>
                  </div>
                  <div class="info-value">{{ courseData.time }}</div>
                </div>

                <div class="info-item price-item">
                  <div class="info-label">
                    <i class="el-icon-money"></i>
                    <span>Price</span>
                  </div>
                  <div class="price-value">RM {{ courseData.price }}</div>
                </div>
              </div>

              <div class="action-section">
                <el-button
                    type="primary"
                    size="large"
                    class="buy-button"
                    @click="buy"
                    icon="el-icon-shopping-cart-2">
                  Enroll Now
                </el-button>
                <div class="purchase-note">
                  <i class="el-icon-info"></i>
                  <span>Secure payment • Instant access</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- Course Content Section -->
      <div class="course-content-section">
        <div class="section-header">
          <h2>Course Description</h2>
          <div class="header-line"></div>
        </div>
        <div class="content-wrapper">
          <div
              v-html="courseData.content"
              class="course-content w-e-text w-e-text-container">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import E from 'wangeditor'

export default {
  name: 'CourseDetail',
  data() {
    return {
      user: {},
      courseId: this.$route.query.id,
      courseData: {}
    }
  },
  mounted() {
    this.initUser()
    this.loadCourseDetail()
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

    // 加载课程详情
    loadCourseDetail() {
      if (!this.courseId) {
        this.$message.error('Course ID is required')
        return
      }

      this.$request.get('/course/selectById/' + this.courseId).then(res => {
        if (res.code === '200') {
          this.courseData = res.data
        } else {
          this.$message.error(res.msg || 'Failed to load course details')
        }
      }).catch(error => {
        console.error('Failed to load course:', error)
        this.$message.error('Failed to load course details')
      })
    },

    // 购买课程
    buy() {
      // 检查用户是否已登录
      if (!this.user.id) {
        this.$message.warning('Please login first')
        return
      }

      // 验证课程数据
      if (!this.courseData.price) {
        this.$message.error('Course price not available')
        return
      }

      const data = {
        courseId: this.courseId,
        userId: this.user.id,
        price: this.courseData.price
      }

      this.$request.post('/orders/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Enrollment successful!')
        } else {
          this.$message.error(res.msg || 'Enrollment failed')
        }
      }).catch(error => {
        console.error('Enrollment failed:', error)
        this.$message.error('Enrollment failed, please try again')
      })
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
  width: 80%;
  max-width: 1200px;
  margin: 0 auto;
}

/* Course Header */
.course-header {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.image-container {
  position: relative;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.course-image {
  width: 100%;
  height: 280px;
  object-fit: cover;
  border-radius: 15px;
  transition: transform 0.3s ease;
}

.course-image:hover {
  transform: scale(1.05);
}

.image-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #8b5a00;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 5px;
  box-shadow: 0 4px 12px rgba(255, 215, 0, 0.3);
}

.course-info {
  padding-left: 20px;
}

.course-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 30px 0;
  line-height: 1.3;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.info-grid {
  display: grid;
  gap: 25px;
  margin-bottom: 40px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f8f9fc;
  border-radius: 15px;
  border-left: 4px solid #6366f1;
  transition: all 0.3s ease;
}

.info-item:hover {
  background: #f1f3ff;
  transform: translateX(5px);
}

.price-item {
  border-left-color: #10b981;
}

.price-item:hover {
  background: #f0fdf4;
}

.info-label {
  display: flex;
  align-items: center;
  font-weight: 600;
  color: #4a5568;
  min-width: 120px;
  margin-right: 20px;
}

.info-label i {
  margin-right: 8px;
  font-size: 18px;
  color: #6366f1;
}

.price-item .info-label i {
  color: #10b981;
}

.info-value {
  font-size: 16px;
  color: #2d3748;
  font-weight: 500;
}

.price-value {
  font-size: 24px;
  font-weight: 700;
  color: #10b981;
  text-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}

.action-section {
  margin-top: 30px;
}

.buy-button {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  height: 50px;
  padding: 0 40px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 25px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(99, 102, 241, 0.3);
}

.buy-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.4);
}

.purchase-note {
  margin-top: 15px;
  display: flex;
  align-items: center;
  color: #718096;
  font-size: 14px;
}

.purchase-note i {
  margin-right: 8px;
  color: #38a169;
}

/* Course Content Section */
.course-content-section {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 30px;
  text-align: center;
}

.section-header h2 {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 15px 0;
}

.header-line {
  width: 80px;
  height: 4px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 2px;
  margin: 0 auto;
}

.content-wrapper {
  background: #fafbfc;
  border-radius: 15px;
  padding: 30px;
  border: 1px solid #e2e8f0;
}

.course-content {
  line-height: 1.8;
  font-size: 16px;
  color: #4a5568;
}

/* Responsive Design */
@media (max-width: 992px) {
  .container {
    width: 95%;
  }

  .course-header {
    padding: 30px 20px;
  }

  .course-info {
    padding-left: 0;
    margin-top: 20px;
  }

  .course-title {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px 0;
  }

  .course-header {
    padding: 20px;
  }

  .course-title {
    font-size: 24px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    text-align: left;
  }

  .info-label {
    margin-bottom: 8px;
    margin-right: 0;
  }

  .buy-button {
    width: 100%;
  }

  .course-content-section {
    padding: 20px;
  }

  .content-wrapper {
    padding: 20px;
  }
}
</style>

<style>
/* Global styles for rich text content */
.course-content p {
  color: #4a5568 !important;
  margin-bottom: 16px;
  line-height: 1.8;
}

.course-content h1,
.course-content h2,
.course-content h3,
.course-content h4,
.course-content h5,
.course-content h6 {
  color: #2d3748 !important;
  font-weight: 600;
  margin: 24px 0 16px 0;
}

.course-content ul,
.course-content ol {
  margin: 16px 0;
  padding-left: 30px;
}

.course-content li {
  margin-bottom: 8px;
  color: #4a5568;
}

.course-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-content blockquote {
  border-left: 4px solid #6366f1;
  background: #f8f9fc;
  padding: 16px 20px;
  margin: 20px 0;
  border-radius: 0 8px 8px 0;
}

.course-content code {
  background: #f1f3f4;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e53e3e;
}

.course-content pre {
  background: #2d3748;
  color: #e2e8f0;
  padding: 20px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
}
</style>