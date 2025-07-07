<template>
  <div class="resource-container">
    <div class="page-header">
      <h2>Learning Resource Management</h2>
      <p>Manage learning resources and their availability</p>
    </div>

    <div class="search-section">
      <div class="search-wrapper">
        <el-input
            placeholder="Enter resource name to search"
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
          <i class="el-icon-plus"></i> Add Resource
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
          empty-text="No resources available"
          header-row-class-name="table-header">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" align="center" sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">#{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="img" label="Image" width="100" align="center">
          <template v-slot="scope">
            <div class="image-cell">
              <el-image
                  v-if="scope.row.img"
                  :src="scope.row.img"
                  :preview-src-list="[scope.row.img]"
                  class="resource-img">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div v-else class="no-image">
                <i class="el-icon-files"></i>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Resource Name" min-width="150">
          <template v-slot="scope">
            <div class="name-cell">
              <i class="el-icon-files"></i>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="Location" min-width="120">
          <template v-slot="scope">
            <div class="location-cell">
              <i class="el-icon-location"></i>
              <span>{{ scope.row.location }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="code" label="Code" min-width="120">
          <template v-slot="scope">
            <div class="code-cell">
              <i class="el-icon-postcard"></i>
              <span>{{ scope.row.code }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="descr" label="Description" min-width="150" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="Status" width="120">
          <template v-slot="scope">
            <el-tag
                :type="getStatusType(scope.row.status)"
                size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
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
            layout="total, sizes, prev, pager, next"
            :total="total"
            class="custom-pagination">
        </el-pagination>
      </div>
    </div>

    <el-dialog
        title="Learning Resource Information"
        :visible.sync="fromVisible"
        width="500px"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form
          label-width="120px"
          :model="form"
          class="custom-form">
        <el-form-item prop="img" label="Image">
          <div class="upload-section">
            <el-upload
                class="image-uploader"
                :action="$baseUrl + '/files/upload'"
                :headers="{ token: user.token }"
                :show-file-list="false"
                :on-success="handleImgSuccess"
                :before-upload="beforeImageUpload">
              <img v-if="form.img" :src="form.img" class="image-preview" />
              <div v-else class="upload-placeholder">
                <i class="el-icon-plus"></i>
                <p>Upload Image</p>
              </div>
            </el-upload>
          </div>
        </el-form-item>
        <el-form-item prop="name" label="Name">
          <el-input
              v-model="form.name"
              placeholder="Enter resource name"
              prefix-icon="el-icon-files">
          </el-input>
        </el-form-item>
        <el-form-item prop="location" label="Location">
          <el-input
              v-model="form.location"
              placeholder="Enter location"
              prefix-icon="el-icon-location">
          </el-input>
        </el-form-item>
        <el-form-item prop="code" label="Code">
          <el-input
              v-model="form.code"
              placeholder="Enter resource code"
              prefix-icon="el-icon-postcard">
          </el-input>
        </el-form-item>
        <el-form-item prop="status" label="Status">
          <el-select
              v-model="form.status"
              placeholder="Select status"
              style="width: 100%">
            <el-option label="Available" value="Idle"></el-option>
            <el-option label="In Use" value="In Use"></el-option>
            <el-option label="Under Maintenance" value="Under Maintenance"></el-option>
            <el-option label="Unavailable" value="Broken"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="descr" label="Description">
          <el-input
              type="textarea"
              :rows="4"
              v-model="form.descr"
              placeholder="Enter usage instructions">
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Save</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Equipment",
  data() {
    return {
      tableData: [],  // All data
      pageNum: 1,   // Current page number
      pageSize: 5,  // Items per page
      total: 0,
      name: null,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
      ids: []
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleAdd() {   // Add new data
      this.form = {}  // Clear form data when adding new
      this.fromVisible = true   // Open dialog
    },

    handleEdit(row) {   // Edit data
      // 深拷贝数据
      this.form = JSON.parse(JSON.stringify(row))

      // 调试信息 - 查看原始状态
      console.log('Original resource data:', row)
      console.log('Form status after copy:', this.form.status)

      this.fromVisible = true   // Open dialog
    },

    save() {   // Save button logic - triggers add or update
      console.log('Saving resource with data:', this.form)
      console.log('Status being saved:', this.form.status)

      this.$request({
        url: this.form.id ? '/equipment/update' : '/equipment/add',
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
    },

    del(id) {   // Single delete
      this.$confirm('Are you sure you want to delete?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/equipment/delete/' + id).then(res => {
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
        this.$request.delete('/equipment/delete/batch', {data: this.ids}).then(res => {
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
      this.$request.get('/equipment/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        console.log('Loaded resource data:', res.data)
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
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isJPGorPNG) this.$message.error('Image must be JPG or PNG format!')
      if (!isLt2M) this.$message.error('Image size cannot exceed 2MB!')
      return isJPGorPNG && isLt2M
    },

    // 修复后的状态类型判断 - 基于后端返回的英文状态
    getStatusType(status) {
      console.log('Getting status type for:', status)
      const statusTypeMap = {
        'Idle': 'success',           // 空闲 - 绿色
        'In Use': 'warning',         // 使用中 - 橙色
        'Under Maintenance': 'info', // 维护中 - 蓝色
        'Broken': 'danger'          // 损坏 - 红色
      }
      return statusTypeMap[status] || 'info'
    },

    // 修复后的状态文本显示 - 基于后端返回的英文状态
    getStatusText(status) {
      console.log('Getting status text for:', status)
      const statusTextMap = {
        'Idle': 'Available',
        'In Use': 'In Use',
        'Under Maintenance': 'Under Maintenance',
        'Broken': 'Unavailable'
      }
      return statusTextMap[status] || status
    }
  }
}
</script>

<style scoped>
.resource-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
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

.search-section, .operation-section {
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

.operation-left {
  display: flex;
  gap: 12px;
}

.add-btn, .delete-btn {
  min-width: 140px;
}

.table-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.custom-table /deep/ .table-header {
  background: #f8f9fa !important;
}

.custom-table /deep/ .table-header th {
  background: #f8f9fa !important;
  color: #606266 !important;
  font-weight: 600 !important;
}

.custom-table /deep/ .el-table__row:hover {
  background-color: #f5f7fa;
}

.image-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.resource-img {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.no-image {
  width: 40px;
  height: 40px;
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.name-cell, .location-cell, .code-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name-cell i {
  color: #409eff;
}

.location-cell i {
  color: #67c23a;
}

.code-cell i {
  color: #e6a23c;
}

.action-btn {
  margin: 0 4px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 24px;
  border-top: 1px solid #e4e7ed;
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
}

.custom-dialog /deep/ .el-dialog__body {
  padding: 30px 24px;
}

.custom-form /deep/ .el-form-item__label {
  font-weight: 600;
  color: #303133;
}

.upload-section {
  display: flex;
  justify-content: center;
}

.image-uploader {
  width: 120px;
  height: 80px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s;
}

.image-uploader:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}

.image-preview {
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
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-placeholder p {
  margin: 0;
  font-size: 12px;
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

@media (max-width: 768px) {
  .resource-container {
    padding: 16px;
  }

  .search-wrapper, .operation-left {
    flex-direction: column;
  }

  .search-input {
    width: 100%;
  }

  .add-btn, .delete-btn {
    width: 100%;
  }
}
</style>