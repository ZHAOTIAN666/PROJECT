<template>
  <div class="reserve-container">
    <div class="page-header">
      <h2>Resource Reservations</h2>
      <p>Manage learning resource booking requests and approvals</p>
    </div>

    <div class="table-section">
      <el-table
          :data="tableData"
          stripe
          class="custom-table"
          empty-text="No reservations available"
          header-row-class-name="table-header">
        <el-table-column prop="id" label="ID" width="80" align="center" sortable>
          <template v-slot="scope">
            <el-tag size="mini" type="info">#{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="equipmentImg" label="Resource" width="100" align="center">
          <template v-slot="scope">
            <div class="resource-cell">
              <el-image
                  v-if="scope.row.equipmentImg"
                  :src="scope.row.equipmentImg"
                  :preview-src-list="[scope.row.equipmentImg]"
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
        <el-table-column prop="equipmentName" label="Resource Name" min-width="150">
          <template v-slot="scope">
            <div class="name-cell">
              <i class="el-icon-files"></i>
              <span>{{ scope.row.equipmentName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Reserved By" min-width="120">
          <template v-slot="scope">
            <div class="user-cell">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.userName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="start" label="Start Time" min-width="140">
          <template v-slot="scope">
            <div class="time-cell">
              <i class="el-icon-time"></i>
              <span>{{ scope.row.start }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="end" label="End Time" min-width="140">
          <template v-slot="scope">
            <div class="time-cell">
              <i class="el-icon-time"></i>
              <span>{{ scope.row.end }}</span>
            </div>
          </template>
        </el-table-column>
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
                icon="el-icon-edit">
              Review
            </el-button>
            <el-button
                type="danger"
                size="mini"
                @click="del(scope.row.id)"
                icon="el-icon-delete">
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
        title="Review Reservation"
        :visible.sync="fromVisible"
        width="450px"
        :close-on-click-modal="false"
        destroy-on-close
        class="custom-dialog">
      <el-form
          label-width="120px"
          :model="form"
          :rules="rules"
          ref="formRef"
          class="custom-form">
        <el-form-item prop="status" label="Status">
          <el-select
              v-model="form.status"
              placeholder="Select status"
              style="width: 100%">
            <el-option label="Pending Review" value="待审核"></el-option>
            <el-option label="Approved" value="审核通过"></el-option>
            <el-option label="Rejected" value="审核不通过"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">Cancel</el-button>
        <el-button type="primary" @click="save">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "EqReserve",
  data() {
    return {
      tableData: [],  // All data
      pageNum: 1,   // Current page number
      pageSize: 10,  // Items per page
      total: 0,
      fromVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {},
    }
  },
  created() {
    this.load(1)
  },
  methods: {
    handleEdit(row) {   // Edit data
      this.form = JSON.parse(JSON.stringify(row))  // Assign value to form object with deep copy
      this.fromVisible = true   // Open dialog
    },
    save() {   // Save button logic - triggers add or update
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.$request({
            url: this.form.id ? '/eqreserve/update' : '/eqreserve/add',
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
        this.$request.delete('/eqreserve/delete/' + id).then(res => {
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
      this.$request.get('/eqreserve/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    getStatusType(status) {
      const statusMap = {
        '待审核': 'warning',
        '审核通过': 'success',
        '审核不通过': 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        '待审核': 'Pending',
        '审核通过': 'Approved',
        '审核不通过': 'Rejected'
      }
      return statusMap[status] || status
    }
  }
}
</script>

<style scoped>
.reserve-container {
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

.resource-cell {
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

.name-cell, .user-cell, .time-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.name-cell i {
  color: #409eff;
}

.user-cell i {
  color: #67c23a;
}

.time-cell i {
  color: #e6a23c;
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
  .reserve-container {
    padding: 16px;
  }
}
</style>