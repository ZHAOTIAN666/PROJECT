<template>
  <div class="enrollment-container">
    <!-- Page Header -->
    <div class="page-header">
      <h2>Course Enrollments</h2>
      <p>Manage student course enrollment records and registrations</p>
    </div>

    <!-- Search Section -->
    <div class="search-section">
      <div class="search-header">
        <i class="el-icon-search"></i>
        <span class="header-title">Search Enrollments</span>
      </div>
      <div class="search-content">
        <el-input
            placeholder="Enter teacher name"
            v-model="coachName"
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

    <!-- Table Section -->
    <div class="table-section">
      <div class="table-header">
        <i class="el-icon-tickets"></i>
        <span class="header-title">Enrollment List</span>
        <el-tag
            size="small"
            type="info"
            style="margin-left: 15px">
          Total: {{ total }} records
        </el-tag>
      </div>

      <el-table
          :data="tableData"
          stripe
          v-loading="loading"
          element-loading-text="Loading enrollments..."
          element-loading-spinner="el-icon-loading"
          empty-text="No enrollments found"
          class="enrollment-table"
          header-row-class-name="table-header-row">

        <el-table-column
            prop="id"
            label="ID"
            width="80"
            align="center"
            sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">
              #{{ scope.row.id }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
            prop="userName"
            label="Student"
            show-overflow-tooltip
            min-width="120">
          <template v-slot="scope">
            <div class="student-info">
              <i class="el-icon-user-solid"></i>
              <span>{{ scope.row.userName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            prop="coachName"
            label="Teacher"
            show-overflow-tooltip
            min-width="120">
          <template v-slot="scope">
            <div class="teacher-info">
              <i class="el-icon-s-custom"></i>
              <span>{{ scope.row.coachName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column
            prop="content"
            label="Course Description"
            show-overflow-tooltip
            min-width="200">
          <template v-slot="scope">
            <div class="description-text">
              {{ scope.row.content || 'No description' }}
            </div>
          </template>
        </el-table-column>

        <el-table-column
            prop="time"
            label="Enrollment Date"
            width="180"
            sortable>
          <template v-slot="scope">
            <div class="time-info">
              <i class="el-icon-time"></i>
              <span>{{ scope.row.time }}</span>
            </div>
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

    <!-- Statistics Card -->
    <div class="stats-section" v-if="tableData.length > 0">
      <el-card class="stats-card" shadow="hover">
        <div class="stats-content">
          <div class="stat-item">
            <i class="el-icon-tickets"></i>
            <div class="stat-info">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">Total Enrollments</div>
            </div>
          </div>
          <div class="stat-item">
            <i class="el-icon-user"></i>
            <div class="stat-info">
              <div class="stat-number">{{ uniqueStudents }}</div>
              <div class="stat-label">Active Students</div>
            </div>
          </div>
          <div class="stat-item">
            <i class="el-icon-s-custom"></i>
            <div class="stat-info">
              <div class="stat-number">{{ uniqueTeachers }}</div>
              <div class="stat-label">Teaching Staff</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  name: "Reserve",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      coachName: null,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {}
    }
  },
  computed: {
    uniqueStudents() {
      const students = new Set(this.tableData.map(item => item.userName))
      return students.size
    },
    uniqueTeachers() {
      const teachers = new Set(this.tableData.map(item => item.coachName))
      return teachers.size
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    // Load data with pagination
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.loading = true

      this.$request.get('/reserve/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          coachName: this.coachName,
        }
      }).then(res => {
        this.tableData = res.data?.list || []
        this.total = res.data?.total || 0

        if (this.tableData.length === 0 && this.coachName) {
          this.$message.info('No enrollments found for this teacher')
        }
      }).catch(err => {
        this.$message.error('Failed to load enrollments')
        console.error('Load enrollments error:', err)
      }).finally(() => {
        this.loading = false
      })
    },

    // Reset search
    reset() {
      this.coachName = null
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
    }
  }
}
</script>

<style scoped>
.enrollment-container {
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
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.search-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.search-header i {
  font-size: 18px;
  color: #409eff;
  margin-right: 8px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.search-content {
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

.table-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.table-header i {
  font-size: 18px;
  color: #409eff;
  margin-right: 8px;
}

.enrollment-table {
  border-radius: 8px;
  overflow: hidden;
  width: 100%;
}

.enrollment-table /deep/ .table-header-row {
  background: #f8f9fa !important;
}

.enrollment-table /deep/ .table-header-row th {
  background: #f8f9fa !important;
  color: #606266 !important;
  font-weight: 600 !important;
  border-bottom: 2px solid #e4e7ed !important;
}

.enrollment-table /deep/ .el-table__row:hover {
  background-color: #f5f7fa;
}

.student-info,
.teacher-info,
.time-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.student-info i {
  color: #67c23a;
}

.teacher-info i {
  color: #e6a23c;
}

.time-info i {
  color: #909399;
}

.description-text {
  color: #606266;
  font-size: 14px;
  line-height: 1.4;
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

.stats-section {
  margin-top: 20px;
}

.stats-card {
  border-radius: 8px;
  overflow: hidden;
}

.stats-content {
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 20px 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  text-align: center;
}

.stat-item i {
  font-size: 24px;
  color: #409eff;
}

.stat-info {
  text-align: left;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
}

/* Button styling */
.search-btn:hover, .reset-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Responsive design */
@media (max-width: 768px) {
  .enrollment-container {
    padding: 16px;
  }

  .search-content {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
    margin-bottom: 10px;
  }

  .stats-content {
    flex-direction: column;
    gap: 20px;
  }

  .pagination-section {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .table-section {
    padding: 16px;
  }
}

/* Animation */
.enrollment-container {
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