<template>
  <div class="main-content">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">My Learning Resource Reservations</h2>
        <div class="total-count">Total: {{total}} records</div>
      </div>

      <!-- 表格容器 -->
      <div class="table-container">
        <el-table :data="tableData" stripe class="custom-table" empty-text="No reservation records found">
          <el-table-column prop="id" label="ID" width="80" align="center" sortable>
            <template slot-scope="scope">
              <span class="id-badge">#{{scope.row.id}}</span>
            </template>
          </el-table-column>

          <el-table-column prop="equipmentImg" label="Resource Image" width="120" align="center">
            <template slot-scope="scope">
              <div class="image-container">
                <el-image
                    class="resource-image"
                    v-if="scope.row.equipmentImg"
                    :src="scope.row.equipmentImg"
                    :preview-src-list="[scope.row.equipmentImg]"
                    fit="cover">
                  <div slot="error" class="image-slot">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <div v-else class="no-image">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="equipmentName" label="Resource Name" show-overflow-tooltip min-width="150">
            <template slot-scope="scope">
              <span class="resource-name">{{scope.row.equipmentName}}</span>
            </template>
          </el-table-column>

          <el-table-column prop="start" label="Start Time" show-overflow-tooltip min-width="130">
            <template slot-scope="scope">
              <div class="time-info">
                <i class="el-icon-time"></i>
                <span>{{scope.row.start}}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="end" label="End Time" show-overflow-tooltip min-width="130">
            <template slot-scope="scope">
              <div class="time-info">
                <i class="el-icon-time"></i>
                <span>{{scope.row.end}}</span>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="status" label="Status" width="120" align="center">
            <template slot-scope="scope">
              <el-tag
                  :type="getStatusType(scope.row.status)"
                  size="small"
                  class="status-tag">
                {{getStatusText(scope.row.status)}}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="Actions" width="150" align="center" v-if="user.role === 'USER'">
            <template slot-scope="scope">
              <el-button
                  type="danger"
                  size="mini"
                  icon="el-icon-delete"
                  @click="del(scope.row.id)"
                  :disabled="scope.row.status !== '待审核'"
                  class="action-btn">
                Cancel
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
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
  name: 'ResourceReservations',
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
    // 初始化用户信息
    initUser() {
      try {
        const userStr = localStorage.getItem('xm-user')
        this.user = userStr ? JSON.parse(userStr) : {}
      } catch (error) {
        console.error('Failed to parse user data from localStorage:', error)
        this.user = {}
      }
    },

    load(pageNum) {
      if (pageNum) this.pageNum = pageNum

      // 检查是否有用户ID
      if (!this.user.id) {
        this.$message.warning('User information not found, please login again')
        return
      }

      this.$request.get('/eqreserve/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          userId: this.user.id
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list || []
          this.total = res.data?.total || 0
        } else {
          this.$message.error(res.msg || 'Failed to load data')
        }
      }).catch(error => {
        console.error('Failed to load reservations:', error)
        this.$message.error('Failed to load reservation data')
      })
    },

    del(id) {
      this.$confirm('Are you sure you want to cancel this reservation?', 'Confirm Cancellation', {
        type: "warning",
        confirmButtonText: 'Yes, Cancel',
        cancelButtonText: 'No, Keep'
      }).then(() => {
        this.$request.delete('/eqreserve/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('Reservation cancelled successfully')
            this.load(1)
          } else {
            this.$message.error(res.msg || 'Failed to cancel reservation')
          }
        }).catch(error => {
          console.error('Failed to delete reservation:', error)
          this.$message.error('Failed to cancel reservation')
        })
      }).catch(() => {
        // 用户取消操作
      })
    },

    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },

    // 获取状态对应的标签类型
    getStatusType(status) {
      const statusMap = {
        '待审核': 'warning',
        '已通过': 'success',
        '已拒绝': 'danger',
        '已完成': 'info'
      }
      return statusMap[status] || 'info'
    },

    // 获取状态的英文文本
    getStatusText(status) {
      const statusMap = {
        '待审核': 'Pending',
        '已通过': 'Approved',
        '已拒绝': 'Rejected',
        '已完成': 'Completed'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style scoped>
.main-content {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  padding: 20px 0;
}

.container {
  width: 90%;
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.page-header {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.total-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  backdrop-filter: blur(10px);
}

.table-container {
  padding: 30px;
}

.custom-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.custom-table /deep/ .el-table__header {
  background: #f8f9fc;
}

.custom-table /deep/ .el-table__header th {
  background: #f8f9fc !important;
  color: #2d3748;
  font-weight: 600;
  border-bottom: 2px solid #e2e8f0;
  padding: 16px 12px;
}

.custom-table /deep/ .el-table__row:hover {
  background: #f7fafc !important;
}

.custom-table /deep/ .el-table__row td {
  padding: 16px 12px;
  border-bottom: 1px solid #e2e8f0;
}

.id-badge {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.image-container {
  display: flex;
  justify-content: center;
}

.resource-image {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  border: 2px solid #e2e8f0;
  transition: all 0.3s ease;
}

.resource-image:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.no-image {
  width: 50px;
  height: 50px;
  background: #f7fafc;
  border: 2px dashed #cbd5e0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #a0aec0;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  color: #909399;
}

.resource-name {
  font-weight: 500;
  color: #2d3748;
}

.time-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4a5568;
}

.time-info i {
  color: #6366f1;
}

.status-tag {
  font-weight: 500;
  border: none;
  padding: 6px 12px;
}

.action-btn {
  transition: all 0.3s ease;
}

.action-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 101, 101, 0.3);
}

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
  border-radius: 6px;
  margin: 0 2px;
  transition: all 0.3s ease;
}

.custom-pagination /deep/ .el-pager li.active {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
}

.custom-pagination /deep/ .btn-prev,
.custom-pagination /deep/ .btn-next {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.custom-pagination /deep/ .btn-prev:hover,
.custom-pagination /deep/ .btn-next:hover {
  background: #6366f1;
  color: white;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    width: 95%;
    margin: 10px auto;
  }

  .page-header {
    padding: 20px;
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }

  .page-title {
    font-size: 24px;
  }

  .table-container {
    padding: 20px;
  }
}
</style>