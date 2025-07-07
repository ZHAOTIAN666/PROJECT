<template>
  <div class="student-container">
    <!-- Page Header -->
    <div class="page-header">
      <h2>Student Management</h2>
      <p>Manage student accounts, profiles, and account balances</p>
    </div>

    <!-- Search Section -->
    <div class="search-section">
      <div class="search-header">
        <i class="el-icon-search"></i>
        <span class="header-title">Search Students</span>
      </div>
      <div class="search-content">
        <el-input
            placeholder="Enter username to search"
            v-model="username"
            class="search-input"
            prefix-icon="el-icon-user"
            clearable>
        </el-input>
        <el-button
            type="primary"
            @click="load(1)"
            class="search-btn">
          <i class="el-icon-search"></i> Search
        </el-button>
        <el-button
            type="info"
            @click="reset"
            class="reset-btn">
          <i class="el-icon-refresh"></i> Reset
        </el-button>
      </div>
    </div>

    <!-- Operation Section -->
    <div class="operation-section">
      <div class="operation-header">
        <i class="el-icon-setting"></i>
        <span class="header-title">Operations</span>
      </div>
      <div class="operation-content">
        <el-button
            type="primary"
            @click="handleAdd"
            class="add-btn">
          <i class="el-icon-plus"></i> Add Student
        </el-button>
        <el-button
            type="danger"
            @click="delBatch"
            class="delete-btn"
            :disabled="!ids.length">
          <i class="el-icon-delete"></i> Batch Delete ({{ ids.length }})
        </el-button>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-section">
      <div class="table-header">
        <i class="el-icon-user"></i>
        <span class="header-title">Student List</span>
        <el-tag
            size="small"
            type="info"
            style="margin-left: 15px">
          Total: {{ total }} students
        </el-tag>
      </div>

      <el-table
          :data="tableData"
          stripe
          @selection-change="handleSelectionChange"
          v-loading="loading"
          element-loading-text="Loading students..."
          empty-text="No students found"
          class="student-table"
          header-row-class-name="table-header-row">

        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column prop="id" label="ID" width="70" align="center" sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">
              #{{ scope.row.id }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Avatar" width="80" align="center">
          <template v-slot="scope">
            <div class="avatar-container">
              <el-avatar
                  :size="45"
                  :src="scope.row.avatar"
                  :alt="scope.row.name"
                  icon="el-icon-user-solid"
                  class="student-avatar">
              </el-avatar>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="username" label="Username" show-overflow-tooltip min-width="120">
          <template v-slot="scope">
            <div class="username-info">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.username }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="Full Name" show-overflow-tooltip min-width="100"></el-table-column>

        <el-table-column prop="phone" label="Phone" show-overflow-tooltip min-width="120">
          <template v-slot="scope">
            <div class="phone-info">
              <i class="el-icon-phone"></i>
              <span>{{ scope.row.phone || 'N/A' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="email" label="Email" show-overflow-tooltip min-width="150">
          <template v-slot="scope">
            <div class="email-info">
              <i class="el-icon-message"></i>
              <span>{{ scope.row.email || 'N/A' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="role" label="Role" width="100" align="center">
          <template v-slot="scope">
            <el-tag
                :type="getRoleType(scope.row.role)"
                size="small">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="account" label="Balance" width="100" align="center">
          <template v-slot="scope">
            <div class="balance-cell">
              <i class="el-icon-wallet"></i>
              <span class="balance-amount">¥{{ scope.row.account || '0.00' }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Actions" align="center" width="160" fixed="right">
          <template v-slot="scope">
            <el-button
                size="mini"
                type="primary"
                @click="handleEdit(scope.row)"
                icon="el-icon-edit"
                class="action-btn">
              Edit
            </el-button>
            <el-button
                size="mini"
                type="danger"
                @click="del(scope.row.id)"
                icon="el-icon-delete"
                class="action-btn">
              Delete
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-section">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20, 50]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            class="custom-pagination">
        </el-pagination>
      </div>
    </div>

    <!-- Student Form Dialog -->
    <el-dialog
        :title="form.id ? 'Edit Student' : 'Add New Student'"
        :visible.sync="fromVisible"
        width="45%"
        :close-on-click-modal="false"
        destroy-on-close
        class="student-dialog">

      <el-form
          :model="form"
          label-width="120px"
          style="padding-right: 30px"
          :rules="rules"
          ref="formRef"
          @submit.native.prevent>

        <el-form-item label="Avatar">
          <div class="avatar-upload-section">
            <el-avatar
                :size="80"
                :src="form.avatar"
                icon="el-icon-user-solid"
                class="preview-avatar">
            </el-avatar>
            <el-upload
                class="avatar-uploader"
                :action="$baseUrl + '/files/upload'"
                :headers="{ token: user.token }"
                list-type="picture"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload">
              <el-button type="primary" size="small" icon="el-icon-upload">
                Upload Avatar
              </el-button>
            </el-upload>
          </div>
        </el-form-item>

        <el-form-item label="Username" prop="username">
          <el-input
              v-model="form.username"
              placeholder="Enter username"
              prefix-icon="el-icon-user">
          </el-input>
        </el-form-item>

        <el-form-item label="Full Name" prop="name">
          <el-input
              v-model="form.name"
              placeholder="Enter full name"
              prefix-icon="el-icon-user-solid">
          </el-input>
        </el-form-item>

        <el-form-item label="Phone" prop="phone">
          <el-input
              v-model="form.phone"
              placeholder="Enter phone number"
              prefix-icon="el-icon-phone">
          </el-input>
        </el-form-item>

        <el-form-item label="Email" prop="email">
          <el-input
              v-model="form.email"
              placeholder="Enter email address"
              prefix-icon="el-icon-message">
          </el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false" class="cancel-btn">
          <i class="el-icon-close"></i> Cancel
        </el-button>
        <el-button
            type="primary"
            @click="save"
            :loading="saving"
            class="confirm-btn">
          <i class="el-icon-check"></i> {{ saving ? 'Saving...' : 'Save' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "User",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      saving: false,
      username: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        username: [
          { required: true, message: 'Please enter username', trigger: 'blur' },
          { min: 3, message: 'Username must be at least 3 characters', trigger: 'blur' }
        ],
        name: [
          { required: true, message: 'Please enter full name', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: 'Please enter a valid email address', trigger: 'blur' }
        ]
      },
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    // Add new student
    handleAdd() {
      this.form = {}
      this.fromVisible = true
    },

    // Edit student
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.fromVisible = true
    },

    // Save student (add or update)
    save() {
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.saving = true
          this.$request({
            url: this.form.id ? '/user/update' : '/user/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {
              this.$message.success(this.form.id ? 'Student updated successfully' : 'Student added successfully')
              this.load(1)
              this.fromVisible = false
            } else {
              this.$message.error(res.msg || 'Operation failed')
            }
          }).catch(err => {
            this.$message.error('Network error, please try again')
            console.error('Save student error:', err)
          }).finally(() => {
            this.saving = false
          })
        }
      })
    },

    // Delete single student
    del(id) {
      this.$confirm('Are you sure you want to delete this student?', 'Confirm Delete', {
        type: "warning",
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel'
      }).then(response => {
        this.$request.delete('/user/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('Student deleted successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg || 'Delete failed')
          }
        })
      }).catch(() => {
        this.$message.info('Delete cancelled')
      })
    },

    // Handle selection change
    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id)
    },

    // Batch delete
    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('Please select students to delete')
        return
      }
      this.$confirm(`Are you sure you want to delete ${this.ids.length} selected students?`, 'Confirm Batch Delete', {
        type: "warning",
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel'
      }).then(response => {
        this.$request.delete('/user/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('Students deleted successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg || 'Batch delete failed')
          }
        })
      }).catch(() => {
        this.$message.info('Batch delete cancelled')
      })
    },

    // Load data with pagination
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.loading = true

      this.$request.get('/user/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.username,
        }
      }).then(res => {
        this.tableData = res.data?.list || []
        this.total = res.data?.total || 0
      }).catch(err => {
        this.$message.error('Failed to load students')
        console.error('Load students error:', err)
      }).finally(() => {
        this.loading = false
      })
    },

    // Reset search
    reset() {
      this.username = null
      this.load(1)
      this.$message.success('Search has been reset')
    },

    // Handle page change
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },

    // Handle page size change
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load(1)
    },

    // Handle avatar upload success
    handleAvatarSuccess(response, file, fileList) {
      this.form.avatar = response.data
      this.$message.success('Avatar uploaded successfully')
    },

    // Before avatar upload
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('Avatar must be JPG or PNG format!')
      }
      if (!isLt2M) {
        this.$message.error('Avatar size cannot exceed 2MB!')
      }
      return isJPG && isLt2M
    },

    // Get role tag type
    getRoleType(role) {
      const roleMap = {
        'ADMIN': 'danger',
        'COACH': 'warning',
        'USER': 'info'
      }
      return roleMap[role] || 'info'
    },

    // Get role display text
    getRoleText(role) {
      const roleMap = {
        'ADMIN': 'Admin',
        'COACH': 'Teacher',
        'USER': 'Student'
      }
      return roleMap[role] || 'Student'
    }
  }
}
</script>

<style scoped>
.student-container {
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

.search-section,
.operation-section,
.table-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-header,
.operation-header,
.table-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.search-header i,
.operation-header i,
.table-header i {
  font-size: 18px;
  color: #409eff;
  margin-right: 8px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.search-content,
.operation-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-input {
  width: 280px;
}

.search-btn, .reset-btn {
  min-width: 100px;
}

.add-btn, .delete-btn {
  min-width: 130px;
}

.student-table {
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
}

.student-table /deep/ .table-header-row {
  background: #f8f9fa !important;
}

.student-table /deep/ .table-header-row th {
  background: #f8f9fa !important;
  color: #606266 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #e4e7ed !important;
}

.student-table /deep/ .el-table__row:hover {
  background-color: #f5f7fa;
}

.avatar-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.student-avatar {
  border: 2px solid #ebeef5;
  transition: all 0.3s ease;
}

.student-avatar:hover {
  border-color: #409eff;
}

.username-info,
.phone-info,
.email-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.username-info i {
  color: #409eff;
}

.phone-info i {
  color: #67c23a;
}

.email-info i {
  color: #e6a23c;
}

.balance-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.balance-cell i {
  color: #f56c6c;
}

.balance-amount {
  font-weight: 600;
  color: #f56c6c;
}

.action-btn {
  margin: 0 4px;
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.custom-pagination {
  display: flex;
  justify-content: center;
}

.avatar-upload-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.preview-avatar {
  border: 2px solid #ebeef5;
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

/* Dialog styling */
.student-dialog /deep/ .el-dialog__header {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  color: white;
  margin: 0;
  padding: 20px 24px;
}

.student-dialog /deep/ .el-dialog__title {
  color: white;
  font-size: 18px;
  font-weight: 600;
}

.student-dialog /deep/ .el-dialog__headerbtn .el-dialog__close {
  color: white;
  font-size: 20px;
}

.student-dialog /deep/ .el-dialog__body {
  padding: 30px 24px;
}

/* Button hover effects */
.search-btn:hover, .reset-btn:hover, .add-btn:hover, .delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-btn:hover {
  transform: translateY(-1px);
}

/* Responsive design */
@media (max-width: 768px) {
  .student-container {
    padding: 16px;
  }

  .search-content,
  .operation-content {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
    margin-bottom: 10px;
  }

  .pagination-section {
    justify-content: center;
  }

  .student-dialog /deep/ .el-dialog {
    width: 90% !important;
  }
}

/* Animation */
.student-container {
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