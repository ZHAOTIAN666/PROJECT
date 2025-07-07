<template>
  <div class="orders-container">
    <!-- Page Header -->
    <div class="page-header">
      <h2>Course Orders Management</h2>
      <p>Manage student course enrollments and payment orders</p>
    </div>

    <!-- Search Section -->
    <div class="search-section">
      <div class="search-wrapper">
        <el-input
            placeholder="Enter order number"
            v-model="orderNo"
            class="search-input"
            prefix-icon="el-icon-search"
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
      <div class="operation-left">
        <el-button
            type="danger"
            @click="delBatch"
            class="delete-btn"
            :disabled="!ids.length">
          <i class="el-icon-delete"></i> Batch Delete
        </el-button>
      </div>
      <div class="stats-info">
        <el-tag type="info" size="medium">Total: {{ total }} orders</el-tag>
      </div>
    </div>

    <!-- Table Section -->
    <div class="table-section">
      <el-table
          :data="tableData"
          stripe
          @selection-change="handleSelectionChange"
          class="custom-table"
          empty-text="No orders available"
          header-row-class-name="table-header"
          v-loading="loading"
          element-loading-text="Loading orders...">

        <el-table-column type="selection" width="55" align="center"></el-table-column>

        <el-table-column prop="id" label="ID" width="80" align="center" sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">#{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="courseName" label="Course Name" min-width="150" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="course-cell">
              <i class="el-icon-reading"></i>
              <span>{{ scope.row.courseName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="coachName" label="Teacher" min-width="120" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="teacher-cell">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.coachName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="userName" label="Student" min-width="120" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="student-cell">
              <i class="el-icon-user-solid"></i>
              <span>{{ scope.row.userName }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="orderNo" label="Order Number" min-width="140" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="order-cell">
              <i class="el-icon-postcard"></i>
              <span class="order-no">{{ scope.row.orderNo }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="price" label="Price" width="100" show-overflow-tooltip>
          <template v-slot="scope">
            <div class="price-cell">
              <i class="el-icon-price-tag"></i>
              <span class="price-amount">{{ scope.row.price }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="time" label="Order Time" width="160">
          <template v-slot="scope">
            <div class="time-cell">
              <i class="el-icon-time"></i>
              <span>{{ scope.row.time }}</span>
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

      <!-- Pagination -->
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
  </div>
</template>

<script>
export default {
  name: "Orders",
  data() {
    return {
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
      orderNo: null,
      loading: false,
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
    // Single delete
    del(id) {
      this.$confirm('Are you sure you want to delete this order?', 'Confirm Delete', {
        type: "warning",
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel'
      }).then(response => {
        this.$request.delete('/orders/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('Order deleted successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg)
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
        this.$message.warning('Please select orders to delete')
        return
      }
      this.$confirm(`Are you sure you want to delete ${this.ids.length} selected orders?`, 'Confirm Batch Delete', {
        type: "warning",
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel'
      }).then(response => {
        this.$request.delete('/orders/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {
            this.$message.success('Orders deleted successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg)
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

      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orderNo: this.orderNo,
        }
      }).then(res => {
        this.tableData = res.data?.list || []
        this.total = res.data?.total || 0
      }).catch(err => {
        this.$message.error('Failed to load orders')
        console.error(err)
      }).finally(() => {
        this.loading = false
      })
    },

    // Reset search
    reset() {
      this.orderNo = null
      this.load(1)
    },

    // Handle page change
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
  }
}
</script>

<style scoped>
.orders-container {
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

.course-cell, .teacher-cell, .student-cell, .order-cell, .price-cell, .time-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.course-cell i {
  color: #409eff;
}

.teacher-cell i {
  color: #67c23a;
}

.student-cell i {
  color: #909399;
}

.order-cell i {
  color: #e6a23c;
}

.order-no {
  font-family: 'Courier New', monospace;
  font-size: 12px;
}

.price-cell i {
  color: #f56c6c;
}

.price-amount {
  font-weight: 600;
  color: #f56c6c;
}

.time-cell i {
  color: #c0c4cc;
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

/* Responsive design */
@media (max-width: 768px) {
  .orders-container {
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
.orders-container {
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