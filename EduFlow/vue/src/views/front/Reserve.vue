<template>
  <div class="main-content">
    <div class="container">
      <!-- Header Section -->
      <div class="header-section">
        <h1 class="page-title">My Reservations</h1>
        <p class="page-subtitle">Total: {{ total }} reservations</p>
      </div>

      <!-- Table Section -->
      <div class="table-section">
        <el-table :data="tableData" stripe class="custom-table">
          <el-table-column prop="id" label="ID" width="80" align="center" sortable>
            <template slot-scope="scope">
              <span class="id-badge">#{{ scope.row.id }}</span>
            </template>
          </el-table-column>

          <el-table-column
              prop="userName"
              label="Client"
              show-overflow-tooltip
              v-if="user.role === 'COACH'">
            <template slot-scope="scope">
              <div class="user-info">
                <i class="el-icon-user"></i>
                <span>{{ scope.row.userName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="coachName" label="Coach" show-overflow-tooltip>
            <template slot-scope="scope">
              <div class="coach-info">
                <i class="el-icon-user-solid"></i>
                <span>{{ scope.row.coachName }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="content" label="Description" show-overflow-tooltip>
            <template slot-scope="scope">
              <div class="content-info">
                <i class="el-icon-document"></i>
                <span>{{ scope.row.content }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="time" label="Created Time" width="180">
            <template slot-scope="scope">
              <div class="time-info">
                <i class="el-icon-time"></i>
                <span>{{ scope.row.time }}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column
              label="Actions"
              width="150"
              align="center"
              v-if="user.role === 'USER'">
            <template slot-scope="scope">
              <el-button
                  plain
                  type="danger"
                  size="mini"
                  @click="del(scope.row.id)"
                  icon="el-icon-delete"
                  class="cancel-btn">
                Cancel
              </el-button>
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
</template>

<script>
export default {
  name: 'ReservationRecords',
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
        const userStr = localStorage.getItem('xm-user')
        this.user = userStr ? JSON.parse(userStr) : {}
      } catch (error) {
        this.user = {}
      }
    },
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/reserve/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        this.tableData = res.data?.list || []
        this.total = res.data?.total || 0
      }).catch(() => {
        this.$message.error('Failed to load reservations')
      })
    },
    del(id) {
      this.$confirm('Are you sure you want to cancel this reservation?', 'Confirm Cancellation', {
        type: "warning",
        confirmButtonText: 'Yes, Cancel',
        cancelButtonText: 'No, Keep'
      }).then(() => {
        this.$request.delete('/reserve/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('Reservation cancelled successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg || 'Cancellation failed')
          }
        }).catch(() => {
          this.$message.error('Cancellation failed')
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
  background: linear-gradient(135deg, #f3f0ff 0%, #e5d3f7 100%);
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
  background: linear-gradient(135deg, #a855f7 0%, #c084fc 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
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
  background: #faf5ff !important;
  color: #374151;
  font-weight: 600;
  border-bottom: 2px solid #e5e7eb;
  padding: 16px 12px;
}

.custom-table /deep/ .el-table__row:hover {
  background: #faf5ff !important;
}

.custom-table /deep/ .el-table__row td {
  padding: 16px 12px;
}

.id-badge {
  background: linear-gradient(135deg, #a855f7 0%, #c084fc 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.user-info, .coach-info, .content-info, .time-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #4b5563;
}

.user-info i {
  color: #3b82f6;
  font-size: 16px;
}

.coach-info i {
  color: #a855f7;
  font-size: 16px;
}

.content-info i {
  color: #10b981;
  font-size: 16px;
}

.time-info i {
  color: #f59e0b;
  font-size: 16px;
}

.cancel-btn {
  border-radius: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
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
  background: linear-gradient(135deg, #a855f7 0%, #c084fc 100%);
  color: white;
}

.custom-pagination /deep/ .btn-prev,
.custom-pagination /deep/ .btn-next {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.custom-pagination /deep/ .btn-prev:hover,
.custom-pagination /deep/ .btn-next:hover {
  background: #a855f7;
  color: white;
}

/* Responsive Design */
@media (max-width: 768px) {
  .main-content {
    padding: 20px 0;
  }

  .container {
    width: 95%;
  }

  .header-section,
  .table-section {
    padding: 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .custom-table /deep/ .el-table__row td {
    padding: 12px 8px;
  }

  .user-info, .coach-info, .content-info, .time-info {
    font-size: 14px;
  }
}

@media (max-width: 576px) {
  .custom-table /deep/ .el-table__row td {
    padding: 10px 6px;
  }

  .cancel-btn {
    padding: 8px 12px;
    font-size: 12px;
  }
}
</style>