<template>
  <div class="main-content">
    <div class="container">
      <div class="teacher-title">{{ coachData.name }} - Detailed Introduction</div>
      <div class="teacher-content" v-html="coachData.content"></div>
      <div class="section-title">Please share your review about this teacher:</div>
      <div class="comment-form">
        <el-input type="textarea" :rows="5" v-model="comment" placeholder="Please enter your review about this teacher..."></el-input>
        <div class="submit-container">
          <el-button type="primary" @click="submit">Submit Review</el-button>
        </div>
      </div>
      <div class="section-title">See what others say about this teacher ({{commentData.length}})</div>
      <div class="reviews-container">
        <el-row :gutter="10" v-for="item in commentData" :key="item.id" class="review-row">
          <el-col :span="4">
            <div class="user-info">
              <img :src="item.userAvatar" alt="" class="user-avatar">
              <div class="user-name">{{item.userName}}</div>
            </div>
          </el-col>
          <el-col :span="15">
            <div class="review-content">{{item.content}}</div>
          </el-col>
          <el-col :span="5">
            <div class="review-time">{{item.time}}</div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script>
import E from 'wangeditor'
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      coachId: this.$route.query.id,
      coachData: {},
      comment: null,
      commentData: []
    }
  },
  mounted() {
    this.loadCoach()
    this.loadComment()
  },
  methods: {
    loadComment() {
      this.$request.get('/comment/selectAll', {
        params: {
          coachId: this.coachId
        }
      }).then(res => {
        if (res.code === '200') {
          this.commentData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadCoach() {
      this.$request.get('/coach/selectById/' + this.coachId).then(res => {
        if (res.code === '200') {
          this.coachData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    submit() {
      if (this.user.role !== 'USER') {
        this.$message.warning('Only students can submit reviews')
        return
      }
      if (!this.comment) {
        this.$message.warning('Please enter review content')
        return
      }
      let data = {
        userId: this.user.id,
        coachId: this.coachId,
        content: this.comment
      }
      this.$request.post('/comment/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('Teacher review submitted successfully')
          this.comment = null
          this.loadComment()
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>

<style scoped>
.main-content {
  background: #f5f7fa;
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  width: 65%;
  margin: 20px auto;
  background: white;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.teacher-title {
  margin-top: 20px;
  font-size: 18px;
  font-weight: bold;
  color: #666666;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.teacher-content {
  margin-top: 20px;
  line-height: 1.6;
}

.section-title {
  font-size: 17px;
  font-weight: bold;
  margin: 30px 0 20px 0;
  color: #6366f1;
}

.comment-form {
  margin-bottom: 20px;
}

.submit-container {
  margin-top: 10px;
  text-align: right;
}

.reviews-container {
  margin: 40px 0 100px 0;
}

.review-row {
  margin-bottom: 30px;
  padding: 15px;
  background: #fafafa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.review-row:hover {
  background: #f0f0f0;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.user-name {
  margin-left: 10px;
  font-weight: 500;
}

.review-content {
  line-height: 40px;
  color: #333;
}

.review-time {
  line-height: 40px;
  text-align: right;
  color: #999;
  font-size: 14px;
}

/* 保持原有的编辑器样式 */
.w-e-text-container {
  line-height: 1.6;
}

p {
  color: #333333;
}
</style>