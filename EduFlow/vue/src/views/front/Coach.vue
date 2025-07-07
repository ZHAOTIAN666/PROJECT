<template>
  <div class="main-content">
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

    <!-- Enrollment Dialog -->
    <el-dialog
        title="Course Enrollment"
        :visible.sync="fromVisible"
        width="500px"
        :close-on-click-modal="false"
        destroy-on-close
        class="enrollment-dialog"
    >
      <el-form label-width="120px" class="enrollment-form">
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
        <el-button @click="fromVisible = false" class="cancel-btn">Cancel</el-button>
        <el-button type="primary" @click="submit" class="submit-btn">
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
.main-content {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* Content Section */
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

/* Teacher Cards */
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

/* Dialog Styles */
.enrollment-dialog .el-dialog {
  border-radius: 15px;
}

.enrollment-dialog .el-dialog__header {
  background: linear-gradient(45deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  padding: 20px 30px;
  border-radius: 15px 15px 0 0;
}

.enrollment-dialog .el-dialog__title {
  font-size: 1.3rem;
  font-weight: 600;
}

.enrollment-form {
  padding: 30px;
}

.enrollment-form .el-textarea__inner {
  border-radius: 10px;
  border: 2px solid #e9ecef;
  transition: border-color 0.3s ease;
}

.enrollment-form .el-textarea__inner:focus {
  border-color: #4f46e5;
}

.dialog-footer {
  padding: 20px 30px;
  text-align: right;
  background: #f8f9fa;
  border-radius: 0 0 15px 15px;
}

.cancel-btn {
  margin-right: 15px;
  padding: 10px 25px;
  border-radius: 25px;
}

.submit-btn {
  padding: 10px 30px;
  border-radius: 25px;
  background: linear-gradient(45deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  font-weight: 600;
}

.submit-btn:hover {
  background: linear-gradient(45deg, #4338ca 0%, #6d28d9 100%);
}

/* Responsive Design */
@media (max-width: 768px) {
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
}
</style>