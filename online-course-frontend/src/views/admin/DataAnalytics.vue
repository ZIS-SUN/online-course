<template>
  <div class="data-analytics">
    <!-- Header Section -->
    <div class="analytics-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">
            <el-icon><DataAnalysis /></el-icon>
            数据分析中心
          </h1>
          <p class="page-subtitle">全面掌握平台运营数据，洞察业务增长趋势</p>
        </div>
        <div class="header-actions">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
            class="date-picker"
          />
          <el-button type="primary" @click="refreshData">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <el-button @click="exportReport">
            <el-icon><Download /></el-icon>
            导出报告
          </el-button>
        </div>
      </div>
    </div>

    <!-- Quick Stats -->
    <div class="quick-stats">
      <div class="stat-card" v-for="stat in quickStats" :key="stat.key">
        <div class="stat-icon" :class="stat.type">
          <el-icon :size="24">
            <component :is="stat.icon" />
          </el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ formatNumber(stat.value) }}</div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-trend" :class="stat.trend > 0 ? 'up' : 'down'">
            <el-icon>
              <component :is="stat.trend > 0 ? 'CaretTop' : 'CaretBottom'" />
            </el-icon>
            <span>{{ Math.abs(stat.trend) }}%</span>
            <span class="trend-label">较上期</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-grid">
      <!-- User Growth Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">用户增长趋势</h3>
          <div class="chart-actions">
            <el-radio-group v-model="userChartType" size="small">
              <el-radio-button label="day">日</el-radio-button>
              <el-radio-button label="week">周</el-radio-button>
              <el-radio-button label="month">月</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="chart-body">
          <div ref="userGrowthChart" class="chart-container"></div>
        </div>
      </div>

      <!-- Course Enrollment Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">课程选修情况</h3>
          <div class="chart-actions">
            <el-select v-model="enrollmentPeriod" size="small" placeholder="选择时间">
              <el-option label="本周" value="week" />
              <el-option label="本月" value="month" />
              <el-option label="本季度" value="quarter" />
              <el-option label="本年" value="year" />
            </el-select>
          </div>
        </div>
        <div class="chart-body">
          <div ref="enrollmentChart" class="chart-container"></div>
        </div>
      </div>

      <!-- Category Distribution -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">分类分布</h3>
        </div>
        <div class="chart-body">
          <div ref="categoryChart" class="chart-container"></div>
        </div>
      </div>

      <!-- Active Users Heatmap -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">用户活跃时段</h3>
        </div>
        <div class="chart-body">
          <div ref="heatmapChart" class="chart-container"></div>
        </div>
      </div>

      <!-- Revenue Analysis -->
      <div class="chart-card chart-card-wide">
        <div class="chart-header">
          <h3 class="chart-title">收入分析</h3>
          <div class="chart-actions">
            <el-button-group size="small">
              <el-button :type="revenueView === 'total' ? 'primary' : ''" @click="revenueView = 'total'">总收入</el-button>
              <el-button :type="revenueView === 'course' ? 'primary' : ''" @click="revenueView = 'course'">课程收入</el-button>
              <el-button :type="revenueView === 'cert' ? 'primary' : ''" @click="revenueView = 'cert'">认证收入</el-button>
            </el-button-group>
          </div>
        </div>
        <div class="chart-body">
          <div ref="revenueChart" class="chart-container"></div>
        </div>
      </div>

      <!-- Learning Progress -->
      <div class="chart-card">
        <div class="chart-header">
          <h3 class="chart-title">学习进度分布</h3>
        </div>
        <div class="chart-body">
          <div ref="progressChart" class="chart-container"></div>
        </div>
      </div>
    </div>

    <!-- Data Tables -->
    <div class="data-tables">
      <!-- Top Courses -->
      <div class="table-card">
        <div class="table-header">
          <h3 class="table-title">热门课程排行</h3>
          <el-link type="primary" @click="viewAllCourses">查看全部 <el-icon><ArrowRight /></el-icon></el-link>
        </div>
        <el-table :data="topCourses" class="data-table">
          <el-table-column type="index" label="排名" width="60" />
          <el-table-column prop="name" label="课程名称" min-width="200" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="enrollments" label="选修人数" width="100" align="right">
            <template #default="{ row }">
              <span class="number-badge">{{ row.enrollments }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="completion" label="完成率" width="100" align="center">
            <template #default="{ row }">
              <el-progress :percentage="row.completion" :stroke-width="6" />
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="120" align="center">
            <template #default="{ row }">
              <div class="rating">
                <el-rate v-model="row.rating" disabled :max="5" />
                <span class="rating-text">{{ row.rating }}</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- Active Users -->
      <div class="table-card">
        <div class="table-header">
          <h3 class="table-title">活跃用户</h3>
          <el-link type="primary" @click="viewAllUsers">查看全部 <el-icon><ArrowRight /></el-icon></el-link>
        </div>
        <el-table :data="activeUsers" class="data-table">
          <el-table-column prop="avatar" label="" width="60">
            <template #default="{ row }">
              <el-avatar :src="row.avatar" :size="32">{{ row.name.charAt(0) }}</el-avatar>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="用户名" min-width="150" />
          <el-table-column prop="courses" label="学习课程" width="100" align="center">
            <template #default="{ row }">
              <el-tag size="small">{{ row.courses }} 门</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="studyTime" label="学习时长" width="120" align="right">
            <template #default="{ row }">
              {{ formatStudyTime(row.studyTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="lastActive" label="最后活跃" width="150">
            <template #default="{ row }">
              {{ formatDate(row.lastActive) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import {
  DataAnalysis, User, Reading, Trophy, TrendCharts,
  CaretTop, CaretBottom, Refresh, Download, ArrowRight
} from '@element-plus/icons-vue'
import request from '../../api/request'

// Data refs
const dateRange = ref([])
const userChartType = ref('day')
const enrollmentPeriod = ref('month')
const revenueView = ref('total')

// Chart refs
const userGrowthChart = ref(null)
const enrollmentChart = ref(null)
const categoryChart = ref(null)
const heatmapChart = ref(null)
const revenueChart = ref(null)
const progressChart = ref(null)

// Chart instances
let charts = {}

// Data
const quickStats = ref([
  { key: 'users', label: '总用户数', value: 15234, trend: 12.5, icon: 'User', type: 'primary' },
  { key: 'courses', label: '课程总数', value: 256, trend: 8.3, icon: 'Reading', type: 'success' },
  { key: 'enrollments', label: '选修总数', value: 48923, trend: 15.7, icon: 'Trophy', type: 'warning' },
  { key: 'completion', label: '平均完成率', value: 68.5, trend: -2.3, icon: 'TrendCharts', type: 'info' }
])

const topCourses = ref([
  { name: 'Vue.js 3.0 完整指南', category: '前端开发', enrollments: 3456, completion: 78, rating: 4.8 },
  { name: 'Spring Boot 企业级开发', category: '后端开发', enrollments: 2890, completion: 65, rating: 4.6 },
  { name: '机器学习入门到精通', category: '人工智能', enrollments: 2345, completion: 72, rating: 4.7 },
  { name: 'React Native 移动开发', category: '移动开发', enrollments: 2103, completion: 69, rating: 4.5 },
  { name: '微服务架构设计', category: '架构设计', enrollments: 1987, completion: 81, rating: 4.9 }
])

const activeUsers = ref([
  { avatar: '', name: '张三', courses: 12, studyTime: 3456, lastActive: new Date() },
  { avatar: '', name: '李四', courses: 8, studyTime: 2890, lastActive: new Date() },
  { avatar: '', name: '王五', courses: 15, studyTime: 4567, lastActive: new Date() },
  { avatar: '', name: '赵六', courses: 6, studyTime: 1234, lastActive: new Date() },
  { avatar: '', name: '陈七', courses: 10, studyTime: 2345, lastActive: new Date() }
])

// Initialize charts
const initCharts = () => {
  // User Growth Chart
  charts.userGrowth = echarts.init(userGrowthChart.value)
  charts.userGrowth.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: generateDateRange(30)
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(99, 102, 241, 0.3)' },
            { offset: 1, color: 'rgba(99, 102, 241, 0.05)' }
          ])
        },
        lineStyle: { color: '#6366f1', width: 3 },
        itemStyle: { color: '#6366f1' },
        data: generateRandomData(30, 100, 300)
      },
      {
        name: '活跃用户',
        type: 'line',
        smooth: true,
        lineStyle: { color: '#8b5cf6', width: 2 },
        itemStyle: { color: '#8b5cf6' },
        data: generateRandomData(30, 200, 500)
      }
    ]
  })

  // Enrollment Chart
  charts.enrollment = echarts.init(enrollmentChart.value)
  charts.enrollment.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: ['Python编程', 'Java开发', '前端开发', '数据分析', '机器学习', '云计算']
    },
    series: [
      {
        name: '选修人数',
        type: 'bar',
        barWidth: '60%',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#6366f1' },
            { offset: 1, color: '#8b5cf6' }
          ])
        },
        data: [850, 720, 680, 590, 540, 450]
      }
    ]
  })

  // Category Distribution
  charts.category = echarts.init(categoryChart.value)
  charts.category.setOption({
    tooltip: { trigger: 'item' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: { show: false },
        data: [
          { value: 1048, name: '编程开发', itemStyle: { color: '#6366f1' } },
          { value: 735, name: '人工智能', itemStyle: { color: '#8b5cf6' } },
          { value: 580, name: '数据科学', itemStyle: { color: '#06b6d4' } },
          { value: 484, name: '云计算', itemStyle: { color: '#10b981' } },
          { value: 300, name: '其他', itemStyle: { color: '#f59e0b' } }
        ]
      }
    ]
  })

  // Heatmap Chart
  charts.heatmap = echarts.init(heatmapChart.value)
  const hours = ['00', '02', '04', '06', '08', '10', '12', '14', '16', '18', '20', '22']
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const heatmapData = []
  for (let i = 0; i < days.length; i++) {
    for (let j = 0; j < hours.length; j++) {
      heatmapData.push([j, i, Math.floor(Math.random() * 100)])
    }
  }

  charts.heatmap.setOption({
    tooltip: { position: 'top' },
    grid: { height: '80%', top: '10%' },
    xAxis: { type: 'category', data: hours, splitArea: { show: true } },
    yAxis: { type: 'category', data: days, splitArea: { show: true } },
    visualMap: {
      min: 0,
      max: 100,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '0%',
      inRange: {
        color: ['#f3f4f6', '#ddd6fe', '#a78bfa', '#8b5cf6', '#6366f1']
      }
    },
    series: [{
      type: 'heatmap',
      data: heatmapData,
      label: { show: false },
      emphasis: {
        itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0, 0, 0, 0.5)' }
      }
    }]
  })

  // Revenue Chart
  charts.revenue = echarts.init(revenueChart.value)
  charts.revenue.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['课程收入', '认证收入', '会员收入'] },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
    },
    yAxis: { type: 'value', name: '金额 (万元)' },
    series: [
      {
        name: '课程收入',
        type: 'bar',
        stack: '总量',
        itemStyle: { color: '#6366f1' },
        data: generateRandomData(12, 50, 150)
      },
      {
        name: '认证收入',
        type: 'bar',
        stack: '总量',
        itemStyle: { color: '#8b5cf6' },
        data: generateRandomData(12, 30, 80)
      },
      {
        name: '会员收入',
        type: 'bar',
        stack: '总量',
        itemStyle: { color: '#06b6d4' },
        data: generateRandomData(12, 20, 60)
      }
    ]
  })

  // Progress Chart
  charts.progress = echarts.init(progressChart.value)
  charts.progress.setOption({
    tooltip: { trigger: 'item' },
    radar: {
      indicator: [
        { name: '完成率', max: 100 },
        { name: '活跃度', max: 100 },
        { name: '参与度', max: 100 },
        { name: '满意度', max: 100 },
        { name: '通过率', max: 100 }
      ]
    },
    series: [{
      type: 'radar',
      data: [
        {
          value: [85, 72, 68, 90, 78],
          name: '平均水平',
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(99, 102, 241, 0.3)' },
              { offset: 1, color: 'rgba(99, 102, 241, 0.1)' }
            ])
          },
          lineStyle: { color: '#6366f1', width: 2 },
          itemStyle: { color: '#6366f1' }
        }
      ]
    }]
  })
}

// Utility functions
const generateDateRange = (days) => {
  const dates = []
  const today = new Date()
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(today)
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)
  }
  return dates
}

const generateRandomData = (count, min, max) => {
  const data = []
  for (let i = 0; i < count; i++) {
    data.push(Math.floor(Math.random() * (max - min + 1)) + min)
  }
  return data
}

const formatNumber = (num) => {
  if (typeof num !== 'number') return num
  return num.toLocaleString()
}

const formatStudyTime = (minutes) => {
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}h ${mins}m`
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// Event handlers
const handleDateChange = () => {
  refreshData()
}

const refreshData = async () => {
  ElMessage.success('数据已刷新')
  // Refresh all charts
  Object.values(charts).forEach(chart => {
    chart.resize()
  })
}

const exportReport = () => {
  ElMessage.info('正在生成报告...')
  // Export logic here
}

const viewAllCourses = () => {
  // Navigate to courses page
}

const viewAllUsers = () => {
  // Navigate to users page
}

// Lifecycle
onMounted(() => {
  nextTick(() => {
    initCharts()

    // Handle window resize
    window.addEventListener('resize', () => {
      Object.values(charts).forEach(chart => {
        chart.resize()
      })
    })
  })
})

onUnmounted(() => {
  // Dispose charts
  Object.values(charts).forEach(chart => {
    chart.dispose()
  })
})

// Watch for data changes
watch([userChartType, enrollmentPeriod, revenueView], () => {
  refreshData()
})
</script>

<style scoped>
.data-analytics {
  padding: var(--spacing-6);
  background: var(--color-bg-secondary);
  min-height: 100vh;
}

/* Header */
.analytics-header {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: var(--spacing-8);
  margin-bottom: var(--spacing-6);
  box-shadow: var(--shadow-sm);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-4);
}

.header-info {
  flex: 1;
}

.page-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-2);
  display: flex;
  align-items: center;
  gap: var(--spacing-3);
}

.page-subtitle {
  font-size: var(--font-size-lg);
  color: var(--color-text-secondary);
}

.header-actions {
  display: flex;
  gap: var(--spacing-3);
  align-items: center;
}

.date-picker {
  width: 280px;
}

/* Quick Stats */
.quick-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.stat-card {
  background: var(--color-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-4);
  transition: all var(--transition-base);
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.primary {
  background: linear-gradient(135deg, var(--color-primary-lighter), var(--color-primary-light));
  color: var(--color-primary);
}

.stat-icon.success {
  background: linear-gradient(135deg, var(--color-success-light), var(--color-success));
  color: var(--color-white);
}

.stat-icon.warning {
  background: linear-gradient(135deg, var(--color-warning-light), var(--color-warning));
  color: var(--color-white);
}

.stat-icon.info {
  background: linear-gradient(135deg, var(--color-info-light), var(--color-info));
  color: var(--color-white);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-text-primary);
  margin-bottom: var(--spacing-1);
}

.stat-label {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  margin-bottom: var(--spacing-2);
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--spacing-1);
  font-size: var(--font-size-sm);
}

.stat-trend.up {
  color: var(--color-success);
}

.stat-trend.down {
  color: var(--color-error);
}

.trend-label {
  color: var(--color-text-tertiary);
  margin-left: var(--spacing-1);
}

/* Charts */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: var(--spacing-4);
  margin-bottom: var(--spacing-6);
}

.chart-card {
  background: var(--color-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-sm);
}

.chart-card-wide {
  grid-column: span 2;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
}

.chart-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.chart-container {
  height: 300px;
}

/* Data Tables */
.data-tables {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(600px, 1fr));
  gap: var(--spacing-4);
}

.table-card {
  background: var(--color-white);
  border-radius: var(--radius-lg);
  padding: var(--spacing-6);
  box-shadow: var(--shadow-sm);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-4);
}

.table-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--color-text-primary);
}

.data-table {
  width: 100%;
}

.number-badge {
  font-weight: var(--font-weight-semibold);
  color: var(--color-primary);
}

.rating {
  display: flex;
  align-items: center;
  gap: var(--spacing-2);
}

.rating-text {
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
}

/* Responsive */
@media (max-width: 1200px) {
  .chart-card-wide {
    grid-column: span 1;
  }
}

@media (max-width: 768px) {
  .data-analytics {
    padding: var(--spacing-4);
  }

  .analytics-header {
    padding: var(--spacing-4);
  }

  .header-content {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    flex-direction: column;
  }

  .date-picker {
    width: 100%;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }

  .data-tables {
    grid-template-columns: 1fr;
  }

  .chart-container {
    height: 250px;
  }
}
</style>