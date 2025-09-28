<template>
  <div class="stats-dashboard">
    <!-- 概览卡片 -->
    <el-row :gutter="20" class="stats-overview">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background-color: #409EFF">
              <el-icon size="30"><Document /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ stats.totalCourses }}</div>
              <div class="stats-label">课程总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background-color: #67C23A">
              <el-icon size="30"><User /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ stats.totalUsers }}</div>
              <div class="stats-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background-color: #E6A23C">
              <el-icon size="30"><School /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ stats.totalEnrollments }}</div>
              <div class="stats-label">选课总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stats-card">
          <div class="stats-item">
            <div class="stats-icon" style="background-color: #F56C6C">
              <el-icon size="30"><TrendCharts /></el-icon>
            </div>
            <div class="stats-content">
              <div class="stats-number">{{ stats.activeUsers }}</div>
              <div class="stats-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-section">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>课程学习趋势</span>
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                size="small"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                @change="loadTrends"
              />
            </div>
          </template>
          <div class="chart-container" id="trend-chart">
            <!-- 这里可以集成 ECharts 等图表库 -->
            <div class="chart-placeholder">
              <el-icon size="50"><DataAnalysis /></el-icon>
              <p>学习趋势图表</p>
              <p class="chart-info">显示最近30天的学习数据</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <template #header>
            <span>热门课程TOP10</span>
          </template>
          <el-table :data="hotCourses" style="width: 100%">
            <el-table-column type="index" label="排名" width="60" />
            <el-table-column prop="title" label="课程名称" />
            <el-table-column prop="enrollmentCount" label="选课人数" width="100">
              <template #default="{ row }">
                <el-tag type="success">{{ row.enrollmentCount }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="completionRate" label="完成率" width="100">
              <template #default="{ row }">
                <el-progress
                  :percentage="row.completionRate"
                  :width="60"
                  type="circle"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细统计 -->
    <el-row :gutter="20" class="detail-section">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>学习进度统计</span>
              <el-button type="primary" size="small" @click="exportData">导出数据</el-button>
            </div>
          </template>

          <el-table :data="learningStats" style="width: 100%">
            <el-table-column prop="courseName" label="课程名称" />
            <el-table-column prop="totalVideos" label="视频总数" width="100" />
            <el-table-column prop="totalDuration" label="总时长(分钟)" width="120" />
            <el-table-column prop="enrolledUsers" label="选课人数" width="100" />
            <el-table-column prop="activeUsers" label="活跃学员" width="100" />
            <el-table-column prop="avgProgress" label="平均进度" width="120">
              <template #default="{ row }">
                <el-progress :percentage="row.avgProgress" />
              </template>
            </el-table-column>
            <el-table-column prop="completionRate" label="完成率" width="100">
              <template #default="{ row }">
                {{ row.completionRate }}%
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../../api/request'

const stats = ref({
  totalCourses: 0,
  totalUsers: 0,
  totalEnrollments: 0,
  activeUsers: 0
})

const dateRange = ref([])
const hotCourses = ref([])
const learningStats = ref([])

onMounted(() => {
  loadOverviewStats()
  loadHotCourses()
  loadLearningStats()
})

const loadOverviewStats = async () => {
  try {
    const res = await request({
      url: '/admin/stats/overview',
      method: 'get'
    })
    stats.value = res.data
  } catch (error) {
    console.error('Failed to load stats:', error)
  }
}

const loadHotCourses = async () => {
  // Mock data for demonstration
  hotCourses.value = [
    { title: 'Vue3 入门教程', enrollmentCount: 256, completionRate: 78 },
    { title: 'Spring Boot 实战', enrollmentCount: 198, completionRate: 65 },
    { title: 'MySQL 数据库设计', enrollmentCount: 167, completionRate: 82 },
    { title: 'React 进阶开发', enrollmentCount: 145, completionRate: 71 },
    { title: 'Python 数据分析', enrollmentCount: 132, completionRate: 69 }
  ]
}

const loadLearningStats = async () => {
  // Mock data for demonstration
  learningStats.value = [
    {
      courseName: 'Vue3 入门教程',
      totalVideos: 45,
      totalDuration: 320,
      enrolledUsers: 256,
      activeUsers: 198,
      avgProgress: 67,
      completionRate: 45
    },
    {
      courseName: 'Spring Boot 实战',
      totalVideos: 38,
      totalDuration: 280,
      enrolledUsers: 198,
      activeUsers: 156,
      avgProgress: 54,
      completionRate: 38
    },
    {
      courseName: 'MySQL 数据库设计',
      totalVideos: 32,
      totalDuration: 240,
      enrolledUsers: 167,
      activeUsers: 145,
      avgProgress: 72,
      completionRate: 52
    }
  ]
}

const loadTrends = () => {
  ElMessage.info('加载趋势数据...')
}

const exportData = () => {
  ElMessage.success('数据导出功能待实现')
}
</script>

<style scoped>
.stats-dashboard {
  padding: 0;
  min-height: 100vh;
  background: var(--gray-50);
}

.stats-overview {
  margin-bottom: var(--space-6);
}

:deep(.el-card) {
  border: none !important;
  border-radius: var(--radius-lg) !important;
  box-shadow: var(--shadow-md) !important;
  overflow: hidden !important;
  transition: var(--transition) !important;
}

:deep(.el-card:hover) {
  transform: translateY(-2px) !important;
  box-shadow: var(--shadow-lg) !important;
}

.stats-card {
  margin-bottom: var(--space-5);
  background: white;
}

.stats-item {
  display: flex;
  align-items: center;
  padding: var(--space-4);
}

.stats-icon {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: var(--space-5);
  background: var(--gradient-primary) !important;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.stats-icon[style*="#409EFF"] {
  background: linear-gradient(135deg, #409EFF, #337ecc) !important;
}

.stats-icon[style*="#67C23A"] {
  background: linear-gradient(135deg, #67C23A, #529b2e) !important;
}

.stats-icon[style*="#E6A23C"] {
  background: linear-gradient(135deg, #E6A23C, #b88230) !important;
}

.stats-icon[style*="#F56C6C"] {
  background: linear-gradient(135deg, #F56C6C, #c45656) !important;
}

.stats-content {
  flex: 1;
}

.stats-number {
  font-size: 2rem;
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin-bottom: var(--space-1);
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats-label {
  font-size: var(--text-sm);
  color: var(--gray-600);
  font-weight: var(--font-medium);
}

.charts-section {
  margin-bottom: var(--space-6);
}

:deep(.el-card__header) {
  background: linear-gradient(135deg, var(--gray-50) 0%, var(--gray-100) 100%) !important;
  border-bottom: 1px solid var(--gray-200) !important;
  padding: var(--space-4) var(--space-5) !important;
}

:deep(.el-card__body) {
  padding: var(--space-5) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: var(--font-semibold);
  color: var(--gray-900);
}

.chart-container {
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--gray-50), var(--gray-100));
  border-radius: var(--radius-md);
  border: 1px solid var(--gray-200);
}

.chart-placeholder {
  text-align: center;
  color: var(--gray-500);
}

.chart-placeholder p {
  margin-top: var(--space-3);
  font-weight: var(--font-medium);
}

.chart-info {
  font-size: var(--text-xs);
  color: var(--gray-400);
  margin-top: var(--space-2);
}

.detail-section {
  margin-bottom: var(--space-6);
}

:deep(.el-table) {
  border: none !important;
  border-radius: var(--radius-md) !important;
  overflow: hidden !important;
}

:deep(.el-table__header) {
  background: var(--gray-50) !important;
}

:deep(.el-table th) {
  background: var(--gray-50) !important;
  border-bottom: 1px solid var(--gray-200) !important;
  color: var(--gray-700) !important;
  font-weight: var(--font-semibold) !important;
  font-size: var(--text-sm) !important;
  padding: var(--space-4) var(--space-3) !important;
}

:deep(.el-table td) {
  border-bottom: 1px solid var(--gray-100) !important;
  padding: var(--space-4) var(--space-3) !important;
  font-size: var(--text-sm) !important;
}

:deep(.el-table__row) {
  transition: var(--transition) !important;
}

:deep(.el-table__row:hover) {
  background: var(--gray-50) !important;
}

:deep(.el-tag) {
  border-radius: var(--radius-full) !important;
  padding: var(--space-1) var(--space-3) !important;
  font-size: var(--text-xs) !important;
  font-weight: var(--font-semibold) !important;
  border: none !important;
}

:deep(.el-progress-circle) {
  margin: 0 auto;
}

:deep(.el-progress__text) {
  font-size: var(--text-xs) !important;
  font-weight: var(--font-semibold) !important;
}

:deep(.el-button--small) {
  border-radius: var(--radius-md) !important;
  padding: var(--space-2) var(--space-4) !important;
  font-size: var(--text-xs) !important;
  font-weight: var(--font-semibold) !important;
  transition: var(--transition) !important;
  background: var(--gradient-primary) !important;
  border: none !important;
  color: white !important;
}

:deep(.el-button--small:hover) {
  transform: translateY(-1px) !important;
  box-shadow: var(--shadow-md) !important;
}

:deep(.el-date-editor) {
  border-radius: var(--radius-md) !important;
}

:deep(.el-date-editor .el-input__wrapper) {
  border-radius: var(--radius-md) !important;
  border: 1px solid var(--gray-300) !important;
  transition: var(--transition) !important;
}

:deep(.el-date-editor .el-input__wrapper:hover) {
  border-color: var(--primary) !important;
}

/* Responsive Design */
@media (max-width: 768px) {
  .stats-dashboard {
    padding: var(--space-4);
  }

  .stats-item {
    padding: var(--space-3);
  }

  .stats-icon {
    width: 48px;
    height: 48px;
    margin-right: var(--space-3);
  }

  .stats-number {
    font-size: 1.5rem;
  }

  .chart-container {
    height: 240px;
  }

  :deep(.el-card__body) {
    padding: var(--space-3) !important;
  }

  :deep(.el-card__header) {
    padding: var(--space-3) !important;
  }

  .card-header {
    flex-direction: column;
    gap: var(--space-2);
    align-items: stretch;
  }
}
</style>