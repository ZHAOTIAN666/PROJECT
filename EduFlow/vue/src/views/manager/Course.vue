<template>
  <div class="course-container">
    <div class="page-header">
      <h2>Course Management</h2>
      <p>Manage online courses and their details</p>
    </div>

    <div class="search-section">
      <div class="search-wrapper">
        <el-input
            placeholder="Enter course name to search"
            v-model="name"
            class="search-input"
            prefix-icon="el-icon-search"
            clearable>
        </el-input>
        <el-button type="primary" @click="load(1)" class="search-btn">
          <i class="el-icon-search"></i> Search
        </el-button>
        <el-button type="info" @click="reset" class="reset-btn">
          <i class="el-icon-refresh"></i> Reset
        </el-button>
      </div>
    </div>

    <div class="operation-section">
      <div class="operation-left">
        <el-button type="primary" @click="handleAdd" class="add-btn">
          <i class="el-icon-plus"></i> Add New Course
        </el-button>
        <el-button type="danger" @click="delBatch" class="delete-btn">
          <i class="el-icon-delete"></i> Batch Delete
        </el-button>
      </div>
    </div>

    <div class="table-section">
      <el-table
          :data="tableData"
          stripe
          @selection-change="handleSelectionChange"
          class="custom-table"
          empty-text="No data available"
          header-row-class-name="table-header">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" align="center" sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">#{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="img" label="Course Cover" width="100" align="center">
          <template v-slot="scope">
            <div class="cover-cell">
              <el-image
                  v-if="scope.row.img"
                  :src="scope.row.img"
                  :preview-src-list="[scope.row.img]"
                  class="course-cover"
                  fit="cover">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div v-else class="no-image">
                <i class="el-icon-picture-outline"></i>
                <span>No Image</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Course Name" min-width="150" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="course-name-cell">
              <i class="el-icon-reading"></i>
              <span class="course-name">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="Duration" min-width="120" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="duration-cell">
              <i class="el-icon-time"></i>
              <span>{{ scope.row.time }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="Price" min-width="100" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="price-cell">
              <i class="el-icon-price-tag"></i>
              <span class="price">{{ scope.row.price }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="coachName" label="Teacher" min-width="120" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="teacher-cell" v-if="scope.row.coachName">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.coachName }}</span>
            </div>
            <span v-else class="empty-text">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="Description" min-width="120" show-overflow-tooltip>
          <template v-slot="scope">
            <el-button
                type="text"
                @click="viewEditor(scope.row.content)"
                class="view-content-btn">
              <i class="el-icon-view"></i> View Details
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="180" align="center" fixed="right">
          <template v-slot="scope">
            <el-button
                type="primary"
                @click="handleEdit(scope.row)"
                size="mini"
                icon="el-icon-edit"
                class="action-btn">
              Edit
            </el-button>
            <el-button
                type="danger"
                size="mini"
                @click="del(scope.row.id)"
                icon="el-icon-delete"
                class="action-btn">
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20, 50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            class="custom-pagination">
        </el-pagination>
      </div>
    </div>

    <!-- Edit/Add Course Dialog -->
    <el-dialog
        title="Course Information"
        :visible.sync="fromVisible"
        width="70%"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form
          label-width="120px"
          :model="form"
          :rules="rules"
          ref="formRef"
          class="custom-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item prop="img" label="Course Cover">
              <div class="cover-upload-section">
                <el-upload
                    class="cover-uploader"
                    :action="$baseUrl + '/files/upload'"
                    :headers="{ token: user.token }"
                    :show-file-list="false"
                    :on-success="handleImgSuccess"
                    :before-upload="beforeImageUpload">
                  <div class="upload-area">
                    <img v-if="form.img" :src="form.img" class="cover-preview" />
                    <div v-else class="upload-placeholder">
                      <i class="el-icon-plus"></i>
                      <p>Upload Cover Image</p>
                    </div>
                  </div>
                </el-upload>
                <div class="upload-tips">
                  <p class="tip-title">Image Guidelines</p>
                  <ul class="tip-list">
                    <li>Recommended size: 300x200px</li>
                    <li>Format: JPG, PNG</li>
                    <li>Max size: 5MB</li>
                  </ul>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="name" label="Course Name">
              <el-input
                  v-model="form.name"
                  placeholder="Enter course name"
                  prefix-icon="el-icon-reading">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="time" label="Duration">
              <el-input
                  v-model="form.time"
                  placeholder="e.g., 8 weeks, 3 months"
                  prefix-icon="el-icon-time">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item prop="price" label="Price">
              <el-input
                  v-model="form.price"
                  placeholder="Enter course price"
                  prefix-icon="el-icon-price-tag">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="coachId" label="Teacher">
              <el-select
                  v-model="form.coachId"
                  placeholder="Select a teacher"
                  style="width: 100%"
                  prefix-icon="el-icon-user">
                <el-option
                    v-for="item in coachData"
                    :label="item.name"
                    :value="item.id"
                    :key="item.id">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item prop="content" label="Description">
          <div class="editor-wrapper">
            <div id="editor" class="editor-container"></div>
            <div class="editor-tip">
              <i class="el-icon-info"></i>
              <span>Create detailed course description with rich text formatting, images and videos.</span>
            </div>
          </div>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i> Cancel
        </el-button>
        <el-button type="primary" @click="save" class="confirm-btn">
          <i class="el-icon-check"></i> Save
        </el-button>
      </div>
    </el-dialog>

    <!-- View Content Dialog -->
    <el-dialog
        title="Course Description"
        :visible.sync="viewVisible"
        width="60%"
        :close-on-click-modal="false"
        destroy-on-close
        class="view-dialog">
      <div class="content-viewer">
        <div v-html="viewData" class="w-e-text w-e-text-container content-display"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import E from 'wangeditor'
export default {
  name: "Course",
  data() {
    return {
      tableData: [],  // All data
      pageNum: 1,   // Current page number
      pageSize: 10,  // Items per page
      total: 0,
      name: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: [],
      coachData: [],
      editor: null,
      viewData: null,
      viewVisible: false,
    }
  },
  created() {
    this.load(1)
    this.loadCoach()
  },
  methods: {
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
    loadCoach() {
      this.$request.get('/coach/selectAll').then(res => {
        if (res.code === '200') {
          this.coachData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // Add new data
      this.form = {}  // Clear form data when adding new
      this.initWangEditor('')
      this.fromVisible = true   // Open dialog
    },
    handleEdit(row) {   // Edit data
      this.form = JSON.parse(JSON.stringify(row))  // Assign value to form object with deep copy
      this.initWangEditor(this.form.content)
      this.fromVisible = true   // Open dialog
    },
    save() {   // Save button logic - triggers add or update
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.content = this.editor.txt.html()
          this.$request({
            url: this.form.id ? '/course/update' : '/course/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // Success
              this.$message.success('Saved successfully')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg)  // Show error message
            }
          })
        }
      })
    },
    del(id) {   // Single delete
      this.$confirm('Are you sure you want to delete?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/course/delete/' + id).then(res => {
          if (res.code === '200') {   // Success
            this.$message.success('Operation successful')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // Show error message
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // Currently selected rows
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // Batch delete
      if (!this.ids.length) {
        this.$message.warning('Please select data')
        return
      }
      this.$confirm('Are you sure you want to batch delete these records?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/course/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // Success
            this.$message.success('Operation successful')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // Show error message
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // Paginated query
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/course/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleImgSuccess(res) {
      this.form.img = res.data
      this.$message.success('Image uploaded successfully')
    },
    beforeImageUpload(file) {
      const isJPGorPNG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt5M = file.size / 1024 / 1024 < 5

      if (!isJPGorPNG) {
        this.$message.error('Course cover must be JPG or PNG format!')
      }
      if (!isLt5M) {
        this.$message.error('Course cover size cannot exceed 5MB!')
      }
      return isJPGorPNG && isLt5M
    }
  }
}
</script>

<style scoped>
.course-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

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
  width: 300px;
}

.search-btn, .reset-btn {
  min-width: 100px;
}

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

.add-btn, .delete-btn {
  min-width: 150px;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

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

.cover-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.course-cover {
  width: 50px;
  height: 40px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.course-cover:hover {
  transform: scale(1.1);
}

.no-image {
  width: 50px;
  height: 40px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  font-size: 12px;
}

.course-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.course-name-cell i {
  color: #409eff;
}

.course-name {
  font-weight: 500;
}

.duration-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.duration-cell i {
  color: #67c23a;
}

.price-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.price-cell i {
  color: #e6a23c;
}

.price {
  font-weight: 600;
  color: #f56c6c;
}

.teacher-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.teacher-cell i {
  color: #909399;
}

.empty-text {
  color: #c0c4cc;
  font-style: italic;
}

.view-content-btn {
  color: #409eff;
  font-size: 13px;
  padding: 4px 8px;
}

.view-content-btn:hover {
  background-color: #ecf5ff;
  border-radius: 4px;
}

.action-btn {
  margin: 0 4px;
}

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

.custom-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  margin: 0;
  padding: 20px 24px;
}

.custom-dialog /deep/ .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.custom-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.custom-dialog /deep/ .el-dialog__body {
  padding: 30px 24px;
}

.custom-form {
  padding: 0;
}

.custom-form /deep/ .el-form-item__label {
  font-weight: 600;
  color: #303133;
}

.custom-form /deep/ .el-input__inner {
  border-radius: 6px;
  border: 1px solid #dcdfe6;
  transition: all 0.3s;
}

.custom-form /deep/ .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.cover-upload-section {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.cover-uploader {
  flex-shrink: 0;
}

.upload-area {
  width: 200px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8c939d;
}

.upload-placeholder i {
  font-size: 32px;
  margin-bottom: 8px;
}

.upload-placeholder p {
  margin: 0;
  font-size: 14px;
}

.upload-tips {
  flex: 1;
}

.tip-title {
  margin: 0 0 12px 0;
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.tip-list {
  margin: 0;
  padding-left: 16px;
  color: #606266;
  font-size: 13px;
}

.tip-list li {
  margin-bottom: 6px;
}

.editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  overflow: hidden;
}

.editor-container {
  min-height: 300px;
}

.editor-tip {
  background: #f8f9fa;
  padding: 12px 16px;
  border-top: 1px solid #e4e7ed;
  color: #909399;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  background: #f8f9fa;
  border-top: 1px solid #e4e7ed;
  margin: 0 -24px -30px -24px;
}

.cancel-btn, .confirm-btn {
  min-width: 100px;
  padding: 10px 20px;
  border-radius: 6px;
}

.view-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #36d1dc 0%, #5b86e5 100%);
  color: white;
  margin: 0;
  padding: 20px 24px;
}

.view-dialog /deep/ .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.view-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.content-viewer {
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  max-height: 500px;
  overflow-y: auto;
}

.content-display {
  background: white;
  padding: 20px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  line-height: 1.6;
}

/* Responsive */
@media (max-width: 768px) {
  .course-container {
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

  .add-btn, .delete-btn {
    width: 100%;
  }

  .cover-upload-section {
    flex-direction: column;
    align-items: center;
    gap: 20px;
  }

  .upload-area {
    width: 100%;
    max-width: 300px;
  }
}

/* Hover Effects */
.search-btn:hover, .reset-btn:hover, .add-btn:hover, .delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-btn:hover {
  transform: translateY(-1px);
}

/* Animation */
.course-container {
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
</style>