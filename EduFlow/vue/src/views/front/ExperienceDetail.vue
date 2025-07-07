<template>
  <div class="main-content">
    <div class="container">
      <!-- Article Header -->
      <div class="article-header">
        <div class="header-background"></div>
        <div class="header-content">
          <h1 class="article-title">{{ experienceData.name }}</h1>
          <div class="article-meta">
            <div class="meta-item">
              <i class="el-icon-user"></i>
              <span class="meta-label">Author:</span>
              <span class="meta-value">{{ experienceData.userName }}</span>
            </div>
            <div class="meta-divider"></div>
            <div class="meta-item">
              <i class="el-icon-time"></i>
              <span class="meta-label">Published:</span>
              <span class="meta-value">{{ experienceData.time }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Article Content -->
      <div class="article-content">
        <div class="content-wrapper">
          <div
              v-html="experienceData.content"
              class="experience-content w-e-text w-e-text-container">
          </div>
        </div>

        <!-- Article Footer -->
        <div class="article-footer">
          <div class="footer-divider"></div>
          <div class="footer-content">
            <div class="author-info">
              <div class="author-avatar">
                <i class="el-icon-user-solid"></i>
              </div>
              <div class="author-details">
                <div class="author-name">{{ experienceData.userName }}</div>
                <div class="author-meta">Shared this learning experience</div>
              </div>
            </div>
            <div class="article-actions">
              <el-button
                  type="primary"
                  icon="el-icon-back"
                  @click="goBack"
                  class="back-btn">
                Back to List
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import E from 'wangeditor'

export default {
  name: 'LearningExperienceDetail',
  data() {
    return {
      experienceId: this.$route.query.id,
      experienceData: {}
    }
  },
  mounted() {
    this.loadExperience()
  },
  methods: {
    loadExperience() {
      if (!this.experienceId) {
        this.$message.error('Learning experience ID is required')
        return
      }

      this.$request.get('/experience/selectById/' + this.experienceId).then(res => {
        if (res.code === '200') {
          this.experienceData = res.data
        } else {
          this.$message.error(res.msg || 'Failed to load learning experience details')
        }
      }).catch(error => {
        console.error('Failed to load learning experience:', error)
        this.$message.error('Failed to load learning experience details')
      })
    },

    goBack() {
      this.$router.go(-1)
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
  width: 75%;
  max-width: 900px;
  margin: 0 auto;
}

/* Article Header */
.article-header {
  position: relative;
  background: white;
  border-radius: 20px 20px 0 0;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.header-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 120px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  opacity: 0.9;
}

.header-content {
  position: relative;
  padding: 40px;
  text-align: center;
  z-index: 2;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: white;
  margin: 0 0 25px 0;
  line-height: 1.3;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.article-meta {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.meta-item i {
  font-size: 16px;
}

.meta-label {
  font-weight: 600;
}

.meta-value {
  font-weight: 500;
}

.meta-divider {
  width: 1px;
  height: 20px;
  background: rgba(255, 255, 255, 0.3);
}

/* Article Content */
.article-content {
  background: white;
  border-radius: 0 0 20px 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.content-wrapper {
  padding: 40px;
}

.experience-content {
  line-height: 1.8;
  font-size: 16px;
  color: #4a5568;
  max-width: none;
}

/* Article Footer */
.article-footer {
  padding: 30px 40px 40px;
  background: #f8f9fc;
}

.footer-divider {
  height: 1px;
  background: linear-gradient(to right, transparent, #e2e8f0, transparent);
  margin-bottom: 30px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author-avatar {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.author-details {
  flex: 1;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 4px;
}

.author-meta {
  font-size: 14px;
  color: #718096;
}

.article-actions {
  display: flex;
  gap: 10px;
}

.back-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 25px;
  padding: 10px 25px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

/* Responsive Design */
@media (max-width: 992px) {
  .container {
    width: 90%;
  }

  .header-content {
    padding: 30px 20px;
  }

  .article-title {
    font-size: 28px;
  }

  .content-wrapper {
    padding: 30px 20px;
  }

  .article-footer {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px 0;
  }

  .container {
    width: 95%;
  }

  .article-title {
    font-size: 24px;
  }

  .article-meta {
    flex-direction: column;
    gap: 10px;
  }

  .meta-divider {
    display: none;
  }

  .footer-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .back-btn {
    width: 100%;
    max-width: 200px;
  }
}

@media (max-width: 576px) {
  .header-content {
    padding: 25px 15px;
  }

  .content-wrapper {
    padding: 25px 15px;
  }

  .article-footer {
    padding: 15px;
  }

  .article-title {
    font-size: 20px;
  }

  .meta-item {
    font-size: 12px;
  }
}
</style>

<style>
/* Global styles for rich text content */
.experience-content p {
  color: #4a5568 !important;
  margin-bottom: 18px;
  line-height: 1.8;
  font-size: 16px;
}

.experience-content h1 {
  color: #2d3748 !important;
  font-size: 28px;
  font-weight: 700;
  margin: 32px 0 20px 0;
  line-height: 1.3;
  border-bottom: 3px solid #6366f1;
  padding-bottom: 10px;
}

.experience-content h2 {
  color: #2d3748 !important;
  font-size: 24px;
  font-weight: 600;
  margin: 28px 0 16px 0;
  line-height: 1.4;
}

.experience-content h3 {
  color: #2d3748 !important;
  font-size: 20px;
  font-weight: 600;
  margin: 24px 0 14px 0;
  line-height: 1.4;
}

.experience-content h4,
.experience-content h5,
.experience-content h6 {
  color: #2d3748 !important;
  font-weight: 600;
  margin: 20px 0 12px 0;
  line-height: 1.4;
}

.experience-content ul,
.experience-content ol {
  margin: 18px 0;
  padding-left: 30px;
}

.experience-content li {
  margin-bottom: 10px;
  color: #4a5568;
  line-height: 1.6;
}

.experience-content img {
  max-width: 100%;
  height: auto;
  border-radius: 12px;
  margin: 24px 0;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.experience-content img:hover {
  transform: scale(1.02);
}

.experience-content blockquote {
  border-left: 4px solid #6366f1;
  background: linear-gradient(135deg, #f8f9fc 0%, #e6f3ff 100%);
  padding: 20px 25px;
  margin: 24px 0;
  border-radius: 0 12px 12px 0;
  font-style: italic;
  position: relative;
}

.experience-content blockquote::before {
  content: '"';
  font-size: 60px;
  color: #6366f1;
  position: absolute;
  top: -10px;
  left: 15px;
  opacity: 0.3;
}

.experience-content code {
  background: #f1f3f4;
  padding: 3px 8px;
  border-radius: 6px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  color: #e53e3e;
  font-size: 14px;
}

.experience-content pre {
  background: #2d3748;
  color: #e2e8f0;
  padding: 24px;
  border-radius: 12px;
  overflow-x: auto;
  margin: 24px 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.experience-content pre code {
  background: transparent;
  color: #e2e8f0;
  padding: 0;
}

.experience-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 24px 0;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.experience-content th,
.experience-content td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #e2e8f0;
}

.experience-content th {
  background: #f8f9fc;
  font-weight: 600;
  color: #2d3748;
}

.experience-content a {
  color: #6366f1;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.experience-content a:hover {
  color: #8b5cf6;
  text-decoration: underline;
}

.experience-content strong {
  color: #2d3748;
  font-weight: 700;
}

.experience-content em {
  color: #718096;
  font-style: italic;
}

/* Responsive text */
@media (max-width: 768px) {
  .experience-content h1 {
    font-size: 24px;
  }

  .experience-content h2 {
    font-size: 20px;
  }

  .experience-content h3 {
    font-size: 18px;
  }

  .experience-content p {
    font-size: 15px;
  }
}
</style>