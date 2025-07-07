<template>
  <div class="main-content">
    <div class="container">
      <!-- Header Section -->
      <div class="page-header">
        <h1 class="page-title">Learning Experience Sharing</h1>
        <p class="page-subtitle">Discover and share learning experiences with our educational community</p>
      </div>

      <!-- Search Section -->
      <div class="search-section">
        <div class="search-container">
          <el-input
              placeholder="Enter learning experience title to search"
              class="search-input"
              v-model="name"
              clearable>
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <el-button
              type="primary"
              class="search-btn"
              @click="load(1)"
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

      <!-- Table Section -->
      <div class="table-section">
        <div class="table-container">
          <el-table
              :data="tableData"
              stripe
              class="custom-table"
              empty-text="No learning experience records found">

            <el-table-column prop="name" label="Learning Experience Title" show-overflow-tooltip min-width="300">
              <template slot-scope="scope">
                <a
                    :href="'/front/experienceDetail?id=' + scope.row.id"
                    target="_blank"
                    class="experience-link">
                  <i class="el-icon-document"></i>
                  {{ scope.row.name }}
                </a>
              </template>
            </el-table-column>

            <el-table-column prop="userName" label="Author" show-overflow-tooltip min-width="120">
              <template slot-scope="scope">
                <div class="author-info">
                  <i class="el-icon-user"></i>
                  <span>{{ scope.row.userName }}</span>
                </div>
              </template>
            </el-table-column>

            <el-table-column prop="role" label="Role" show-overflow-tooltip width="120" align="center">
              <template slot-scope="scope">
                <el-tag
                    :type="scope.row.role === 'COACH' ? 'success' : 'info'"
                    size="small"
                    class="role-tag">
                  <i :class="scope.row.role === 'COACH' ? 'el-icon-star-on' : 'el-icon-user-solid'"></i>
                  {{ getRoleText(scope.row.role) }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="time" label="Published Time" width="180" align="center">
              <template slot-scope="scope">
                <div class="time-info">
                  <i class="el-icon-time"></i>
                  <span>{{ scope.row.time }}</span>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- Pagination -->
          <div class="pagination-container">
            <el-pagination
                background
                @current-change="handleCurrentChange"
                :current-page="pageNum"
                :page-sizes="[5, 10, 20]"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total"
                class="custom-pagination">
            </el-pagination>
          </div>
        </div>
      </div>
    </div>

    <!-- Learning Experience Form Dialog -->
    <el-dialog
        title="Learning Experience Information"
        :visible.sync="fromVisible"
        width="70%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form
          label-width="140px"
          class="experience-form"
          :model="form"
          :rules="rules"
          ref="formRef">
        <el-form-item prop="name" label="Experience Title" class="form-item">
          <el-input
              v-model="form.name"
              autocomplete="off"
              placeholder="Enter learning experience title"
              class="custom-input">
          </el-input>
        </el-form-item>
        <el-form-item prop="content" label="Experience Details" class="form-item">
          <div id="editor" class="editor-container"></div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="cancel-btn">Cancel</el-button>
        <el-button type="primary" @click="save" class="submit-btn" icon="el-icon-check">Save</el-button>
      </div>
    </el-dialog>

    <!-- Learning Experience Detail Dialog -->
    <el-dialog
        title="Learning Experience Details"
        :visible.sync="viewVisible"
        width="70%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <div class="content-viewer">
        <div v-html="viewData" class="experience-content w-e-text w-e-text-container"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import E from 'wangeditor'

export default {
  name: 'LearningExperienceList',
  data() {
    return {
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      name: null,
      form: {},
      fromVisible: false,
      editor: null,
      viewData: null,
      viewVisible: false,
      rules: {
        name: [
          { required: true, message: 'Please enter learning experience title', trigger: 'blur' }
        ],
        content: [
          { required: true, message: 'Please enter learning experience details', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.initUser()
    this.load(1)
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

    viewEditor(content) {
      this.viewData = content
      this.viewVisible = true
    },

    initWangEditor(content) {
      this.$nextTick(() => {
        this.editor = new E('#editor')
        this.editor.config.placeholder = 'Please enter content'
        this.editor.config.uploadFileName = 'file'
        this.editor.config.uploadImgServer = 'http://localhost:9090/files/wang/upload'
        this.editor.create()
        setTimeout(() => {
          this.editor.txt.html(content)
        })
      })
    },

    handleAdd() {
      this.form = {
        userId: this.user.id,
        role: this.user.role
      }
      this.initWangEditor('')
      this.fromVisible = true
    },

    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.initWangEditor(this.form.content)
      this.fromVisible = true
    },

    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/experience/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg || 'Failed to load data')
        }
      }).catch(error => {
        console.error('Failed to load learning experiences:', error)
        this.$message.error('Failed to load learning experience data')
      })
    },

    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.content = this.editor.txt.html()
          this.$request({
            url: this.form.id ? '/experience/update' : '/experience/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {
              this.$message.success('Saved successfully')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg || 'Save failed')
            }
          }).catch(error => {
            console.error('Save failed:', error)
            this.$message.error('Save failed, please try again')
          })
        }
      })
    },

    del(id) {
      this.$confirm('Are you sure you want to delete this learning experience?', 'Confirm Delete', {
        type: "warning",
        confirmButtonText: 'Yes, Delete',
        cancelButtonText: 'Cancel'
      }).then(() => {
        this.$request.delete('/experience/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('Deleted successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg || 'Delete failed')
          }
        }).catch(error => {
          console.error('Delete failed:', error)
          this.$message.error('Delete failed, please try again')
        })
      }).catch(() => {})
    },

    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },

    reset() {
      this.name = null
      this.load(1)
    },

    // 获取角色的英文文本
    getRoleText(role) {
      const roleMap = {
        'COACH': 'Teacher',
        'USER': 'Student'
      }
      return roleMap[role] || role
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

/* Page Header */
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
  color: #718096;
  margin: 0;
  line-height: 1.6;
}

/* Search Section */
.search-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
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

/* Table Section */
.table-section {
  background: white;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.table-container {
  overflow: hidden;
}

.custom-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.custom-table /deep/ .el-table__header {
  background: #f8f9fc;
}

.custom-table /deep/ .el-table__header th {
  background: #f8f9fc !important;
  color: #2d3748;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  padding: 18px 12px;
  font-size: 14px;
}

.custom-table /deep/ .el-table__row:hover {
  background: #f7fafc !important;
}

.custom-table /deep/ .el-table__row td {
  padding: 18px 12px;
  border-bottom: 1px solid #e2e8f0;
}

.experience-link {
  color: #6366f1;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.experience-link:hover {
  color: #8b5cf6;
  text-decoration: underline;
}

.experience-link i {
  font-size: 16px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4a5568;
}

.author-info i {
  color: #6366f1;
  font-size: 16px;
}

.role-tag {
  font-weight: 600;
  padding: 6px 12px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.time-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #4a5568;
  font-size: 14px;
}

.time-info i {
  color: #6366f1;
}

/* Pagination */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e2e8f0;
}

.custom-pagination /deep/ .el-pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-pagination /deep/ .el-pager li {
  border-radius: 8px;
  margin: 0 2px;
  transition: all 0.3s ease;
}

.custom-pagination /deep/ .el-pager li.active {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
}

.custom-pagination /deep/ .btn-prev,
.custom-pagination /deep/ .btn-next {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.custom-pagination /deep/ .btn-prev:hover,
.custom-pagination /deep/ .btn-next:hover {
  background: #6366f1;
  color: white;
}

/* Dialog Styles */
.custom-dialog /deep/ .el-dialog {
  border-radius: 16px;
  overflow: hidden;
}

.custom-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 25px 30px;
}

.custom-dialog /deep/ .el-dialog__title {
  color: white;
  font-weight: 600;
  font-size: 20px;
}

.custom-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.custom-dialog /deep/ .el-dialog__body {
  padding: 30px;
}

.experience-form {
  padding: 0;
}

.form-item {
  margin-bottom: 25px;
}

.custom-input /deep/ .el-input__inner {
  border-radius: 8px;
  height: 45px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.custom-input /deep/ .el-input__inner:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}

.editor-container {
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  min-height: 300px;
}

.content-viewer {
  background: #f8f9fc;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e2e8f0;
}

.experience-content {
  line-height: 1.8;
  color: #4a5568;
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

  .page-header {
    padding: 30px 20px;
  }

  .page-title {
    font-size: 28px;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 20px 0;
  }

  .page-header {
    padding: 25px 15px;
  }

  .search-section,
  .table-section {
    padding: 20px;
  }

  .custom-dialog /deep/ .el-dialog {
    width: 95% !important;
  }

  .experience-form {
    padding: 0 10px;
  }
}

@media (max-width: 576px) {
  .search-btn,
  .reset-btn {
    width: 100%;
    max-width: 200px;
  }

  .custom-table /deep/ .el-table__row td,
  .custom-table /deep/ .el-table__header th {
    padding: 12px 8px;
  }
}
</style>

<style>
/* Global styles for rich text content */
.experience-content p {
  color: #4a5568 !important;
  margin-bottom: 16px;
  line-height: 1.8;
}

.experience-content h1,
.experience-content h2,
.experience-content h3,
.experience-content h4,
.experience-content h5,
.experience-content h6 {
  color: #2d3748 !important;
  font-weight: 600;
  margin: 24px 0 16px 0;
}

.experience-content ul,
.experience-content ol {
  margin: 16px 0;
  padding-left: 30px;
}

.experience-content li {
  margin-bottom: 8px;
  color: #4a5568;
}

.experience-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.experience-content blockquote {
  border-left: 4px solid #6366f1;
  background: #f8f9fc;
  padding: 16px 20px;
  margin: 20px 0;
  border-radius: 0 8px 8px 0;
}

.experience-content code {
  background: #f1f3f4;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e53e3e;
}

.experience-content pre {
  background: #2d3748;
  color: #e2e8f0;
  padding: 20px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
}
</style>