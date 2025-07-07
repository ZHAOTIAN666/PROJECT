<template>
  <div class="main-content">
    <div class="container">
      <!-- Header Section -->
      <div class="header-section">
        <h1 class="page-title">My Course Orders</h1>
        <p class="page-subtitle">Total: {{ total }} orders</p>
      </div>

      <!-- Table Section -->
      <div class="table-section">
        <el-table :data="tableData" stripe class="custom-table">
          <el-table-column prop="id" label="ID" width="80" align="center" sortable>
            <template slot-scope="scope">
              <span class="id-badge">#{{ scope.row.id }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="courseName" label="Course Name" show-overflow-tooltip>
            <template slot-scope="scope">
              <a :href="'/front/courseDetail?id=' + scope.row.courseId" class="course-link">
                <i class="el-icon-document"></i>
                {{ scope.row.courseName }}
              </a>
            </template>
          </el-table-column>
          <el-table-column prop="coachName" label="Teacher" show-overflow-tooltip>
            <template slot-scope="scope">
              <a :href="'/front/coachDetail?id=' + scope.row.coachId" class="coach-link">
                <i class="el-icon-user"></i>
                {{ scope.row.coachName }}
              </a>
            </template>
          </el-table-column>
          <el-table-column prop="orderNo" label="Order Number" show-overflow-tooltip>
            <template slot-scope="scope">
              <span class="order-no">{{ scope.row.orderNo }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="Price" show-overflow-tooltip width="100">
            <template slot-scope="scope">
              <span class="price">RM {{ scope.row.price }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="Order Time" width="150"></el-table-column>
          <el-table-column label="Actions" width="120" align="center">
            <template slot-scope="scope">
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
  </div>
</template>

<script>
export default {
  name: 'CourseOrders',
  data() {
    return {
      user: {},
      tableData: [],
      pageNum: 1,
      pageSize: 10,
      total: 0,
    }
  },
  mounted() {
    this.initUser()
    this.load(1)
  },
  methods: {
    initUser() {
      try {
        const userStr = localStorage.getItem("xm-user")
        this.user = userStr ? JSON.parse(userStr) : {}
      } catch (error) {
        this.user = {}
      }
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          userId: this.user.id
        }
      }).then(res => {
        this.tableData = res.data?.list || []
        this.total = res.data?.total || 0
      }).catch(() => {
        this.$message.error('Failed to load data')
      })
    },
    del(id) {
      this.$confirm('Are you sure you want to delete this order?', 'Confirm Delete', {
        type: "warning",
        confirmButtonText: 'Delete',
        cancelButtonText: 'Cancel'
      }).then(() => {
        this.$request.delete('/orders/delete/' + id).then(res => {
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
      this.pageNum = pageNum
      this.load(this.pageNum)
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
  background: white;
  border-radius: 16px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  text-align: center;
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
  padding: 16px 12px;
}

.custom-table /deep/ .el-table__row:hover {
  background: #f8fafc !important;
}

.custom-table /deep/ .el-table__row td {
  padding: 16px 12px;
}

.id-badge {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.course-link, .coach-link {
  color: #6366f1;
  text-decoration: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: color 0.3s ease;
}

.course-link:hover, .coach-link:hover {
  color: #4f46e5;
  text-decoration: underline;
}

.order-no {
  font-family: 'Courier New', monospace;
  background: #e2e8f0;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  color: #374151;
}

.price {
  font-weight: 700;
  color: #10b981;
  font-size: 14px;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.pagination-container /deep/ .el-pager li.active {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border-radius: 6px;
}

.pagination-container /deep/ .btn-prev:hover,
.pagination-container /deep/ .btn-next:hover {
  background: #6366f1;
  color: white;
  border-radius: 6px;
}

/* Responsive */
@media (max-width: 768px) {
  .main-content { padding: 20px 0; }
  .container { width: 95%; }
  .header-section, .table-section { padding: 20px; }
  .page-title { font-size: 24px; }
  .custom-table /deep/ .el-table__row td { padding: 12px 8px; }
}
</style>