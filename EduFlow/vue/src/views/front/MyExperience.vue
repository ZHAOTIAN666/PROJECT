<template>
  <div class="main-content">
    <div class="container">
      <!-- Header Section -->
      <div class="header-section">
        <div class="header-left">
          <h1 class="page-title">My Learning Experience</h1>
          <p class="page-subtitle">Total: {{ total }} experiences</p>
        </div>
        <div class="header-right">
          <el-button type="primary" class="add-btn" @click="handleAdd" icon="el-icon-plus">
            Share Experience
          </el-button>
        </div>
      </div>

      <!-- Table Section -->
      <div class="table-section">
        <el-table :data="tableData" stripe class="custom-table">
          <el-table-column prop="id" label="ID" width="80" align="center" sortable>
            <template slot-scope="scope">
              <span class="id-badge">#{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="Learning Experience Title" show-overflow-tooltip></el-table-column>
          <el-table-column prop="content" label="Details" show-overflow-tooltip width="120">
            <template slot-scope="scope">
              <el-button type="text" @click="viewEditor(scope.row.content)" class="view-btn">
                View Details
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="Published Time" width="150"></el-table-column>
          <el-table-column label="Actions" width="160" align="center">
            <template slot-scope="scope">
              <el-button plain type="primary" size="mini" @click="handleEdit(scope.row)" icon="el-icon-edit">
                Edit
              </el-button>
              <el-button plain type="danger" size="mini" @click="del(scope.row.id)" icon="el-icon-delete">
                Delete
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
              background
              @current-change="handleCurrentChange"
              :current-page="pageNum"
              :page-sizes="[5, 10, 20]"
              :page-size="pageSize"
              layout="total, prev, pager, next"
              :total="total">
          </el-pagination>
        </div>
      </div>
    </div>

    <!-- Learning Experience Form Dialog -->
    <el-dialog
        title="Learning Experience Information"
        :visible.sync="fromVisible"
        width="60%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form label-width="120px" :model="form" :rules="rules" ref="formRef" class="experience-form">
        <el-form-item prop="name" label="Experience Title">
          <el-input v-model="form.name" autocomplete="off" placeholder="Enter learning experience title"></el-input>
        </el-form-item>
        <el-form-item prop="content" label="Experience Details">
          <div id="editor" class="editor-container"></div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save" icon="el-icon-check">Save</el-button>
      </div>
    </el-dialog>

    <!-- View Learning Experience Dialog -->
    <el-dialog
        title="Learning Experience Details"
        :visible.sync="viewVisible"
        width="60%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <div class="content-wrapper">
        <div v-html="viewData" class="experience-content w-e-text w-e-text-container"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import E from 'wangeditor'

export default {
  name: 'MyLearningExperience',
  data() {
    return {
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      form: {},
      fromVisible: false,
      editor: null,
      viewData: null,
      viewVisible: false,
      rules: {
        name: [{ required: true, message: 'Please enter learning experience title', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.initUser()
    this.load(1)
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
          userId: this.user.id,
          role: this.user.role
        }
      }).then(res => {
        this.tableData = res.data?.list || []
        this.total = res.data?.total || 0
      }).catch(() => {
        this.$message.error('Failed to load data')
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
          }).catch(() => {
            this.$message.error('Save failed')
          })
        }
      })
    },
    del(id) {
      this.$confirm('Are you sure you want to delete this learning experience?', 'Confirm Delete', {
        type: "warning",
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel'
      }).then(() => {
        this.$request.delete('/experience/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('Deleted successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg || 'Delete failed')
          }
        }).catch(() => {
          this.$message.error('Delete failed')
        })
      }).catch(() => {})
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
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

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px 0;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  color: #6b7280;
  margin: 0;
  font-size: 14px;
}

.add-btn {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  border-radius: 25px;
  padding: 12px 24px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(99, 102, 241, 0.3);
}

.table-section {
  background: white;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
}

.custom-table /deep/ .el-table__header th {
  background: #f8fafc !important;
  color: #374151;
  font-weight: 600;
  border-bottom: 2px solid #e5e7eb;
}

.custom-table /deep/ .el-table__row:hover {
  background: #f8fafc !important;
}

.id-badge {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.view-btn {
  color: #6366f1;
  font-weight: 600;
}

.view-btn:hover {
  color: #4f46e5;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
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

.experience-form /deep/ .el-input__inner {
  border-radius: 8px;
  border: 2px solid #e5e7eb;
}

.experience-form /deep/ .el-input__inner:focus {
  border-color: #6366f1;
}

.editor-container {
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  min-height: 300px;
}

.content-wrapper {
  background: #f8fafc;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #e5e7eb;
}

.experience-content {
  line-height: 1.8;
  color: #4b5563;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content { padding: 20px 0; }
  .container { width: 95%; }
  .header-section { flex-direction: column; gap: 20px; text-align: center; }
  .table-section { padding: 20px; }
  .custom-dialog /deep/ .el-dialog { width: 95% !important; }
}
</style>

<style>
.experience-content p {
  color: #4b5563 !important;
  margin-bottom: 16px;
}

.experience-content h2 {
  color: #6366f1 !important;
  font-weight: 600;
}

.experience-content strong {
  color: #6366f1 !important;
}

.experience-content ul,
.experience-content ol {
  margin: 16px 0;
  padding-left: 30px;
}

.experience-content li {
  margin-bottom: 8px;
  color: #4b5563;
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
  background: #f8fafc;
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