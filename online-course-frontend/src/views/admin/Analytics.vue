<template>
  <div class="analytics-dashboard">
    <!-- Welcome Section -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-header">
          <div class="welcome-icon">
            <el-icon :size="32"><DataAnalysis /></el-icon>
          </div>
          <div>
            <h1>数据分析</h1>
            <p>深入了解平台运营数据和用户行为</p>
          </div>
        </div>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" size="large" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button size="large" @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- Time Range Selector -->
    <div class="time-range-section">
      <div class="time-range-card">
        <h3>时间范围</h3>
        <div class="time-range-buttons">
          <el-button
            v-for="range in timeRanges"
            :key="range.value"
            :type="selectedRange === range.value ? 'primary' : ''"
            @click="selectTimeRange(range.value)"
          >
            {{ range.label }}
          </el-button>
        </div>
        <div class="custom-date-range">
          <el-date-picker
            v-model="customDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleCustomDateChange"
          />
        </div>
      </div>
    </div>

    <!-- Key Metrics -->
    <div class="metrics-grid">
      <div class="metric-card">
        <div class="metric-icon users">
          <el-icon><User /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-number">{{ analytics.totalUsers }}</div>
          <div class="metric-label">总用户数</div>
          <div class="metric-change" :class="analytics.userGrowthRate >= 0 ? 'positive' : 'negative'">
            <el-icon>
              <component :is="analytics.userGrowthRate >= 0 ? 'CaretTop' : 'CaretBottom'" />
            </el-icon>
            {{ Math.abs(analytics.userGrowthRate) }}%
          </div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon courses">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-number">{{ analytics.totalCourses }}</div>
          <div class="metric-label">总课程数</div>
          <div class="metric-change" :class="analytics.courseGrowthRate >= 0 ? 'positive' : 'negative'">
            <el-icon>
              <component :is="analytics.courseGrowthRate >= 0 ? 'CaretTop' : 'CaretBottom'" />
            </el-icon>
            {{ Math.abs(analytics.courseGrowthRate) }}%
          </div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon enrollments">
          <el-icon><Trophy /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-number">{{ analytics.activeUsers }}</div>
          <div class="metric-label">活跃用户</div>
          <div class="metric-change" :class="analytics.activeUserGrowthRate >= 0 ? 'positive' : 'negative'">
            <el-icon>
              <component :is="analytics.activeUserGrowthRate >= 0 ? 'CaretTop' : 'CaretBottom'" />
            </el-icon>
            {{ Math.abs(analytics.activeUserGrowthRate) }}%
          </div>
        </div>
      </div>

      <div class="metric-card">
        <div class="metric-icon revenue">
          <el-icon><Coin /></el-icon>
        </div>
        <div class="metric-content">
          <div class="metric-number">￥{{ analytics.totalRevenue }}</div>
          <div class="metric-label">总收入</div>
          <div class="metric-change" :class="analytics.revenueGrowthRate >= 0 ? 'positive' : 'negative'">
            <el-icon>
              <component :is="analytics.revenueGrowthRate >= 0 ? 'CaretTop' : 'CaretBottom'" />
            </el-icon>
            {{ Math.abs(analytics.revenueGrowthRate) }}%
          </div>
        </div>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-section">
      <!-- User Growth Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>用户增长趋势</h3>
          <div class="chart-actions">
            <el-button size="small" text type="primary">查看详情</el-button>
          </div>
        </div>
        <div class="chart-container" ref="userGrowthChart"></div>
      </div>

      <!-- Course Popularity Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>课程热度分析</h3>
          <div class="chart-actions">
            <el-button size="small" text type="primary">查看详情</el-button>
          </div>
        </div>
        <div class="chart-container" ref="coursePopularityChart"></div>
      </div>
    </div>

    <!-- Additional Charts -->
    <div class="charts-section">
      <!-- Revenue Chart -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>收入分析</h3>
          <div class="chart-actions">
            <el-button size="small" text type="primary">查看详情</el-button>
          </div>
        </div>
        <div class="chart-container" ref="revenueChart"></div>
      </div>

      <!-- Category Distribution -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>分类分布</h3>
          <div class="chart-actions">
            <el-button size="small" text type="primary">查看详情</el-button>
          </div>
        </div>
        <div class="chart-container" ref="categoryChart"></div>
      </div>
    </div>

    <!-- Data Tables -->
    <div class="tables-section">
      <!-- Top Courses Table -->
      <div class="table-card">
        <div class="table-header">
          <h3>热门课程排行</h3>
          <el-button size="small" text type="primary">查看全部</el-button>
        </div>
        <el-table :data="topCourses" class="modern-table">
          <el-table-column prop="rank" label="排名" width="80" align="center">
            <template #default="{ row }">
              <div class="rank-badge" :class="getRankClass(row.rank)">
                {{ row.rank }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="课程名称" min-width="200" />
          <el-table-column prop="enrollments" label="报名人数" width="120" align="center">
            <template #default="{ row }">
              <span class="enrollment-count">{{ row.enrollments }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="completion" label="完成率" width="120" align="center">
            <template #default="{ row }">
              <el-progress :percentage="row.completion" :show-text="false" />
              <span class="completion-text">{{ row.completion }}%</span>
            </template>
          </el-table-column>
          <el-table-column prop="rating" label="评分" width="120" align="center">
            <template #default="{ row }">
              <el-rate v-model="row.rating" disabled size="small" />
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- User Activity Table -->
      <div class="table-card">
        <div class="table-header">
          <h3>用户活跃度</h3>
          <el-button size="small" text type="primary">查看全部</el-button>
        </div>
        <el-table :data="userActivity" class="modern-table">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="activeUsers" label="活跃用户" width="120" align="center" />
          <el-table-column prop="newUsers" label="新增用户" width="120" align="center" />
          <el-table-column prop="sessionDuration" label="平均在线时长" width="150" align="center" />
          <el-table-column prop="pageViews" label="页面浏览量" width="120" align="center" />
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  DataAnalysis,
  Refresh,
  Download,
  User,
  Reading,
  Trophy,
  Coin,
  CaretTop,
  CaretBottom
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import request from '../../api/request'

const loading = ref(false)
const selectedRange = ref('7d')
const customDateRange = ref([])

const timeRanges = [
  { label: '最近7天', value: '7d' },
  { label: '最近30天', value: '30d' },
  { label: '最近90天', value: '90d' },
  { label: '最近1年', value: '1y' }
]

// Analytics data
const analytics = ref({
  totalUsers: 0,
  userGrowthRate: 0,
  totalCourses: 0,
  courseGrowthRate: 0,
  activeUsers: 0,
  activeUserGrowthRate: 0,
  totalRevenue: 0,
  revenueGrowthRate: 0
})

// Top courses data
const topCourses = ref([])

// Category distribution data
const categoryData = ref([])

// API loading functions
const loadOverviewStats = async () => {
  try {
    const res = await request({
      url: '/admin/analytics/overview',
      method: 'get'
    })
    analytics.value = res.data
  } catch (error) {
    console.error('Failed to load overview stats:', error)
  }
}

const loadPopularCourses = async () => {
  try {
    const res = await request({
      url: '/admin/analytics/popular-courses',
      method: 'get'
    })
    topCourses.value = res.data.map((course, index) => ({
      rank: index + 1,
      title: course.name,
      enrollments: course.enrollments,
      completion: Math.floor(Math.random() * 30 + 70), // 70-100%
      rating: course.rating
    }))
  } catch (error) {
    console.error('Failed to load popular courses:', error)
  }
}

const loadCategoryDistribution = async () => {
  try {
    const res = await request({
      url: '/admin/analytics/category-distribution',
      method: 'get'
    })
    categoryData.value = res.data
  } catch (error) {
    console.error('Failed to load category distribution:', error)
  }
}

// User activity data
const userActivity = ref([
  {
    date: '2024-01-15',
    activeUsers: 1250,
    newUsers: 45,
    sessionDuration: '25分钟',
    pageViews: 8750
  },
  {
    date: '2024-01-14',
    activeUsers: 1180,
    newUsers: 38,
    sessionDuration: '23分钟',
    pageViews: 8200
  },
  {
    date: '2024-01-13',
    activeUsers: 1320,
    newUsers: 52,
    sessionDuration: '28分钟',
    pageViews: 9100
  },
  {
    date: '2024-01-12',
    activeUsers: 1090,
    newUsers: 41,
    sessionDuration: '22分钟',
    pageViews: 7800
  },
  {
    date: '2024-01-11',
    activeUsers: 1210,
    newUsers: 47,
    sessionDuration: '26分钟',
    pageViews: 8450
  }
])

const userGrowthChart = ref(null)
const coursePopularityChart = ref(null)
const revenueChart = ref(null)
const categoryChart = ref(null)

onMounted(async () => {
  await loadOverviewStats()
  await loadPopularCourses()
  await loadCategoryDistribution()
  nextTick(() => {
    initCharts()
  })
})

const selectTimeRange = (range) => {
  selectedRange.value = range
  customDateRange.value = []
  refreshData()
}

const handleCustomDateChange = (dates) => {
  if (dates && dates.length === 2) {
    selectedRange.value = 'custom'
    refreshData()
  }
}

const refreshData = () => {
  loading.value = true
  // Simulate API call
  setTimeout(() => {
    ElMessage.success('数据已刷新')
    loading.value = false
  }, 1000)
}

const exportReport = () => {
  ElMessage.success('报告导出功能开发中...')
}

const getRankClass = (rank) => {
  if (rank === 1) return 'gold'
  if (rank === 2) return 'silver'
  if (rank === 3) return 'bronze'
  return 'default'
}

const initCharts = () => {
  // User Growth Chart
  if (userGrowthChart.value) {
    const userChart = echarts.init(userGrowthChart.value)
    const userOption = {
      title: {
        text: '用户增长趋势',
        left: 'center',
        textStyle: { fontSize: 16, fontWeight: 'bold' }
      },
      tooltip: { trigger: 'axis' },
      legend: { data: ['新增用户', '活跃用户'], bottom: 0 },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
      },
      yAxis: { type: 'value' },
      series: [
        {
          name: '新增用户',
          type: 'line',
          data: [120, 200, 150, 80, 170, 110, 200],
          smooth: true,
          lineStyle: { color: '#0056d3' },
          itemStyle: { color: '#0056d3' }
        },
        {
          name: '活跃用户',
          type: 'line',
          data: [320, 450, 380, 290, 410, 350, 480],
          smooth: true,
          lineStyle: { color: '#10b981' },
          itemStyle: { color: '#10b981' }
        }
      ]
    }
    userChart.setOption(userOption)
  }

  // Course Popularity Chart
  if (coursePopularityChart.value) {
    const courseChart = echarts.init(coursePopularityChart.value)
    const courseOption = {
      title: {
        text: '课程热度排行',
        left: 'center',
        textStyle: { fontSize: 16, fontWeight: 'bold' }
      },
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: {
        type: 'value',
        boundaryGap: [0, 0.01]
      },
      yAxis: {
        type: 'category',
        data: ['TypeScript', 'Node.js', 'React', 'Vue.js', 'Python']
      },
      series: [
        {
          type: 'bar',
          data: [543, 756, 987, 1234, 432],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#0056d3' },
              { offset: 1, color: '#1976d2' }
            ])
          }
        }
      ]
    }
    courseChart.setOption(courseOption)
  }

  // Revenue Chart
  if (revenueChart.value) {
    const revenueChartInstance = echarts.init(revenueChart.value)
    const revenueOption = {
      title: {
        text: '月度收入趋势',
        left: 'center',
        textStyle: { fontSize: 16, fontWeight: 'bold' }
      },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月']
      },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'bar',
          data: [8500, 12300, 10200, 15600, 13400, 18900, 16700],
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#ff6b35' },
              { offset: 1, color: '#ea580c' }
            ])
          }
        }
      ]
    }
    revenueChartInstance.setOption(revenueOption)
  }

  // Category Distribution Chart
  if (categoryChart.value) {
    const categoryChartInstance = echarts.init(categoryChart.value)
    const categoryOption = {
      title: {
        text: '课程分类分布',
        left: 'center',
        textStyle: { fontSize: 16, fontWeight: 'bold' }
      },
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [
        {
          type: 'pie',
          radius: '50%',
          data: [
            { value: 35, name: '编程开发' },
            { value: 25, name: '设计创意' },
            { value: 20, name: '数据科学' },
            { value: 12, name: '商业管理' },
            { value: 8, name: '语言学习' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    }
    categoryChartInstance.setOption(categoryOption)
  }
}
</script>

<style scoped>
.analytics-dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--space-8);
}

/* Welcome Section */
.welcome-section {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border-radius: var(--radius-2xl);
  padding: var(--space-8);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-lg);
}

.welcome-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.welcome-icon {
  width: 64px;
  height: 64px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.welcome-content h1 {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  margin: 0 0 var(--space-2) 0;
  color: white;
}

.welcome-content p {
  font-size: var(--text-lg);
  opacity: 0.9;
  margin: 0;
  color: white;
}

.welcome-actions {
  display: flex;
  gap: var(--space-3);
}

.welcome-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  backdrop-filter: blur(10px);
}

.welcome-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.welcome-actions .el-button.el-button--primary {
  background: white;
  color: var(--primary);
  border: none;
}

.welcome-actions .el-button.el-button--primary:hover {
  background: var(--gray-100);
}

/* Time Range Section */
.time-range-section {
  display: flex;
  gap: var(--space-6);
}

.time-range-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
  flex: 1;
}

.time-range-card h3 {
  font-size: var(--text-lg);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0 0 var(--space-4) 0;
}

.time-range-buttons {
  display: flex;
  gap: var(--space-2);
  margin-bottom: var(--space-4);
}

.custom-date-range {
  display: flex;
  justify-content: flex-end;
}

/* Metrics Grid */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: var(--space-6);
}

.metric-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  display: flex;
  align-items: center;
  gap: var(--space-4);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
  transition: var(--transition);
}

.metric-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.metric-icon.users {
  background: linear-gradient(135deg, var(--success), #059669);
}

.metric-icon.courses {
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
}

.metric-icon.enrollments {
  background: linear-gradient(135deg, var(--warning), #d97706);
}

.metric-icon.revenue {
  background: linear-gradient(135deg, var(--accent), #ea580c);
}

.metric-content {
  flex: 1;
}

.metric-number {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  line-height: 1;
  margin-bottom: var(--space-1);
}

.metric-label {
  font-size: var(--text-sm);
  color: var(--gray-600);
  margin-bottom: var(--space-2);
}

.metric-change {
  display: flex;
  align-items: center;
  gap: var(--space-1);
  font-size: var(--text-sm);
  font-weight: var(--font-medium);
}

.metric-change.positive {
  color: var(--success);
}

.metric-change.negative {
  color: var(--error);
}

/* Charts Section */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: var(--space-6);
}

.chart-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.chart-header h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

.chart-container {
  height: 300px;
  width: 100%;
}

/* Tables Section */
.tables-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(600px, 1fr));
  gap: var(--space-6);
}

.table-card {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.table-header h3 {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

/* Rank Badge */
.rank-badge {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: var(--font-bold);
  font-size: var(--text-sm);
}

.rank-badge.gold {
  background: linear-gradient(135deg, #ffd700, #ffb347);
}

.rank-badge.silver {
  background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
}

.rank-badge.bronze {
  background: linear-gradient(135deg, #cd7f32, #b8860b);
}

.rank-badge.default {
  background: var(--gray-400);
}

.enrollment-count {
  font-weight: var(--font-medium);
  color: var(--gray-900);
}

.completion-text {
  font-size: var(--text-xs);
  color: var(--gray-600);
  margin-left: var(--space-2);
}

/* Table Styles */
:deep(.modern-table) {
  border-radius: var(--radius-lg);
  overflow: hidden;
}

:deep(.modern-table .el-table__header) {
  background: var(--gray-50);
}

:deep(.modern-table .el-table__header th) {
  background: var(--gray-50);
  color: var(--gray-700);
  font-weight: var(--font-semibold);
  border: none;
}

:deep(.modern-table .el-table__body tr:hover) {
  background: var(--gray-50);
}

:deep(.modern-table .el-table__row td) {
  border: none;
  border-bottom: 1px solid var(--gray-100);
}

/* Responsive */
@media (max-width: 1024px) {
  .charts-section {
    grid-template-columns: 1fr;
  }

  .tables-section {
    grid-template-columns: 1fr;
  }

  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: var(--space-6);
  }

  .welcome-header {
    flex-direction: column;
    text-align: center;
  }

  .welcome-actions {
    flex-direction: column;
    width: 100%;
  }

  .welcome-actions .el-button {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .time-range-buttons {
    flex-direction: column;
  }

  .time-range-buttons .el-button {
    width: 100%;
  }
}
</style>