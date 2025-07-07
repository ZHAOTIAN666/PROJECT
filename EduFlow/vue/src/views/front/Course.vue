<template>
  <div class="main-content">
    <div class="container">
      <!-- Search Section -->
      <div class="search-section">
        <div class="search-container">
          <el-input
              placeholder="Enter course name to search"
              class="search-input"
              v-model="name"
              clearable>
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button
              type="primary"
              class="search-btn"
              @click="loadCourse"
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

      <!-- Course Grid Section -->
      <div class="course-grid">
        <el-row :gutter="30">
          <el-col :span="6" v-for="item in courseData" :key="item.id" class="course-col">
            <div class="course-card" @click="$router.push('/front/courseDetail?id=' + item.id)">
              <!-- Course Image -->
              <div class="image-container">
                <img :src="item.img" alt="Course Image" class="course-image">
                <div class="image-overlay">
                  <i class="el-icon-view course-view-icon"></i>
                </div>
              </div>

              <!-- Course Info -->
              <div class="course-info">
                <div class="course-header">
                  <div class="course-name">{{item.name}}</div>
                  <div class="course-price">RM {{item.price}}</div>
                </div>

                <div class="course-details">
                  <div class="detail-item">
                    <i class="el-icon-user-solid"></i>
                    <span>Teacher: {{item.coachName}}</span>
                  </div>
                  <div class="detail-item">
                    <i class="el-icon-time"></i>
                    <span>Duration: {{item.time}}</span>
                  </div>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- Empty State -->
        <div v-if="courseData.length === 0" class="empty-state">
          <i class="el-icon-folder-opened"></i>
          <p>No courses found</p>
          <p class="empty-subtitle">Try adjusting your search criteria</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CourseList',
  data() {
    return {
      courseData: [],
      name: null,
    }
  },
  mounted() {
    this.loadCourse()
  },
  methods: {
    loadCourse() {
      this.$request.get('/course/selectAll', {
        params: {
          name: this.name
        }
      }).then(res => {
        if (res.code === '200') {
          this.courseData = res.data
        } else {
          this.$message.error(res.msg)
        }
      }).catch(error => {
        console.error('Failed to load courses:', error)
        this.$message.error('Failed to load courses')
      })
    },
    reset() {
      this.name = null
      this.loadCourse()
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

/* Course Grid */
.course-grid {
  margin-top: 40px;
}

.course-col {
  margin-bottom: 40px;
}

.course-card {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: all 0.4s ease;
  cursor: pointer;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

/* Image Container */
.image-container {
  position: relative;
  overflow: hidden;
  height: 280px;
}

.course-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: all 0.4s ease;
}

.course-card:hover .course-image {
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
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
}

.course-card:hover .image-overlay {
  opacity: 1;
}

.course-view-icon {
  font-size: 48px;
  color: white;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

/* Course Info */
.course-info {
  padding: 25px;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.course-name {
  flex: 1;
  font-size: 18px;
  font-weight: 700;
  color: #2d3748;
  line-height: 1.4;
  margin-right: 15px;
}

.course-price {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  color: white;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 16px;
  font-weight: 700;
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
}

.course-details {
  space-y: 12px;
}

.detail-item {
  display: flex;
  align-items: center;
  color: #718096;
  font-size: 14px;
  margin-bottom: 12px;
  transition: color 0.3s ease;
}

.detail-item i {
  margin-right: 10px;
  color: #6366f1;
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.course-card:hover .detail-item {
  color: #4a5568;
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

/* Responsive Design */
@media (max-width: 1200px) {
  .course-col {
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

  .course-col {
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

  .course-col {
    width: 100%;
  }

  .course-header {
    flex-direction: column;
    gap: 15px;
  }

  .course-price {
    align-self: flex-start;
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