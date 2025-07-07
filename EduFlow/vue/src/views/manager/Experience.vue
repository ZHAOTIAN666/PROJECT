<template>
  <div class="reviews-container">
    <div class="page-header">
      <h2>Learning Reviews</h2>
      <p>Manage student learning reviews and course feedback</p>
    </div>

    <div class="search-section">
      <div class="search-wrapper">
        <el-input
            placeholder="Enter title to search"
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
        <el-button type="danger" @click="delBatch" class="delete-btn">
          <i class="el-icon-delete"></i> Batch Delete
        </el-button>
      </div>
      <div class="stats-info">
        <el-tag type="info" size="medium">Total: {{ total }} reviews</el-tag>
      </div>
    </div>

    <div class="table-section">
      <el-table
          :data="tableData"
          stripe
          @selection-change="handleSelectionChange"
          class="custom-table"
          empty-text="No reviews available"
          header-row-class-name="table-header">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="ID" width="80" align="center" sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">#{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Review Title" min-width="200" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="title-cell">
              <i class="el-icon-chat-dot-round"></i>
              <span class="title-text">{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="Content" min-width="150" show-overflow-tooltip>
          <template v-slot="scope">
            <el-button
                type="text"
                @click="viewEditor(scope.row.content)"
                class="view-content-btn">
              <i class="el-icon-view"></i> View Details
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Author" min-width="120" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="author-cell">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="Role" width="100" show-overflow-tooltip>
          <template v-slot="scope">
            <el-tag
                :type="scope.row.role === 'COACH' ? 'success' : 'primary'"
                size="small"
                :icon="scope.row.role === 'COACH' ? 'el-icon-star-on' : 'el-icon-user'">
              {{ getRoleText(scope.row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="Published" min-width="140">
          <template v-slot="scope">
            <div class="time-cell">
              <i class="el-icon-time"></i>
              <span>{{ formatTime(scope.row.time) }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="120" align="center" fixed="right">
          <template v-slot="scope">
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

    <!-- View Content Dialog -->
    <el-dialog
        title="Review Details"
        :visible.sync="viewVisible"
        width="70%"
        :close-on-click-modal="false"
        destroy-on-close
        class="view-dialog">
      <div class="content-viewer">
        <div class="content-header">
          <div class="content-meta">
            <i class="el-icon-chat-dot-round"></i>
            <span>Student learning review and feedback</span>
          </div>
        </div>
        <div class="content-body">
          <div v-html="viewData" class="w-e-text w-e-text-container content-display"></div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import E from 'wangeditor'
export default {
  name: "Experience",
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
  },
  methods: {
    viewEditor(content) {
      this.viewData = content
      this.viewVisible = true
    },
    del(id) {   // Single delete
      this.$confirm('Are you sure you want to delete this review?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/experience/delete/' + id).then(res => {
          if (res.code === '200') {   // Success
            this.$message.success('Review deleted successfully')
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
        this.$message.warning('Please select reviews to delete')
        return
      }
      this.$confirm('Are you sure you want to batch delete these reviews?', 'Confirm Delete', {type: "warning"}).then(response => {
        this.$request.delete('/experience/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // Success
            this.$message.success('Reviews deleted successfully')
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
      this.$request.get('/experience/selectPage', {
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
    getRoleText(role) {
      return role === 'COACH' ? 'Teacher' : 'Student'
    },
    formatTime(time) {
      if (!time) return '-'
      return time.length > 16 ? time.substring(0, 16) : time
    }
  }
}
</script>

<style scoped>
.reviews-container {
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
  width: 280px;
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.operation-left {
  display: flex;
  gap: 12px;
}

.delete-btn {
  min-width: 130px;
}

.stats-info {
  display: flex;
  align-items: center;
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

.title-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-cell i {
  color: #409eff;
  flex-shrink: 0;
}

.title-text {
  font-weight: 500;
  color: #303133;
}

.view-content-btn {
  color: #409eff;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.3s;
}

.view-content-btn:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.author-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-cell i {
  color: #67c23a;
}

.time-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-cell i {
  color: #e6a23c;
}

.action-btn {
  margin: 0;
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

.view-dialog /deep/ .el-dialog__body {
  padding: 0;
}

.content-viewer {
  background: #fafafa;
}

.content-header {
  padding: 20px 24px;
  border-bottom: 1px solid #e4e7ed;
  background: white;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 14px;
}

.content-meta i {
  color: #409eff;
}

.content-body {
  padding: 24px;
  max-height: 500px;
  overflow-y: auto;
}

.content-display {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  line-height: 1.6;
  font-size: 14px;
}

.content-display /deep/ img {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
  margin: 12px 0;
}

.content-display /deep/ p {
  margin-bottom: 16px;
}

.content-display /deep/ h1,
.content-display /deep/ h2,
.content-display /deep/ h3 {
  color: #303133;
  margin: 20px 0 16px 0;
}

/* Responsive */
@media (max-width: 768px) {
  .reviews-container {
    padding: 16px;
  }

  .search-wrapper {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .operation-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }

  .operation-left {
    justify-content: center;
  }

  .delete-btn {
    width: 100%;
  }

  .view-dialog /deep/ .el-dialog {
    width: 95% !important;
    margin: 20px auto !important;
  }
}

/* Hover Effects */
.search-btn:hover, .reset-btn:hover, .delete-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-btn:hover {
  transform: translateY(-1px);
}

/* Animation */
.reviews-container {
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

/* Custom scrollbar for content viewer */
.content-body::-webkit-scrollbar {
  width: 6px;
}

.content-body::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.content-body::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.content-body::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>