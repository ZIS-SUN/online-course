<template>
  <div class="categories-admin">
    <!-- Welcome Section -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-header">
          <div class="welcome-icon">
            <el-icon :size="32"><Grid /></el-icon>
          </div>
          <div>
            <h1>分类管理</h1>
            <p>管理课程分类体系，组织课程内容</p>
          </div>
        </div>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" size="large" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          新建分类
        </el-button>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon categories">
          <el-icon><Grid /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ totalCategories }}</div>
          <div class="stat-label">总分类数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon enabled">
          <el-icon><SuccessFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ enabledCategories }}</div>
          <div class="stat-label">启用分类</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon courses">
          <el-icon><Reading /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ totalCourses }}</div>
          <div class="stat-label">关联课程</div>
        </div>
      </div>
    </div>

    <!-- Categories Table -->
    <div class="table-section">
      <div class="table-header">
        <h3>分类列表</h3>
        <div class="table-actions">
          <el-button @click="loadCategories" :icon="Refresh" circle />
        </div>
      </div>

      <el-table
        :data="categories"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        class="modern-table"
      >
        <el-table-column prop="name" label="分类名称" min-width="200">
          <template #default="{ row }">
            <div class="category-info">
              <div v-if="row.icon" class="category-icon" :class="row.icon.toLowerCase()">
                <el-icon :size="18">
                  <component :is="row.icon" />
                </el-icon>
              </div>
              <span class="category-name">{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="分类描述" min-width="300">
          <template #default="{ row }">
            <span class="description-text">{{ row.description || '暂无描述' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="课程数量" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.courseCount || 0 }} 门</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="排序" width="100" align="center">
          <template #default="{ row }">
            <span class="sort-order">{{ row.sortOrder }}</span>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.enabled"
              @change="toggleStatus(row)"
              :loading="row.updating"
            />
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button size="small" type="primary" plain @click="editCategory(row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button size="small" type="danger" plain @click="deleteCategory(row)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Category Dialog -->
    <Modal
      v-model:visible="categoryDialogVisible"
      :title="isEdit ? '编辑分类' : '新建分类'"
      size="medium"
      :loading="saving"
      @confirm="saveCategory"
      @cancel="categoryDialogVisible = false"
    >
      <el-form :model="categoryForm" label-width="80px" class="category-form">
        <el-form-item label="分类名称" required>
          <el-input
            v-model="categoryForm.name"
            placeholder="请输入分类名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="父级分类">
          <el-select
            v-model="categoryForm.parentId"
            placeholder="选择父级分类"
            clearable
            class="full-width"
          >
            <el-option label="无（顶级分类）" :value="null" />
            <el-option
              v-for="cat in flatCategories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
              :disabled="isEdit && cat.id === categoryForm.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="分类描述">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="分类图标">
          <el-select v-model="categoryForm.icon" placeholder="选择图标" class="full-width">
            <el-option label="无" value="" />
            <el-option label="📚 代码" value="Code" />
            <el-option label="🎨 设计" value="Brush" />
            <el-option label="📊 数据" value="DataAnalysis" />
            <el-option label="💼 管理" value="Management" />
            <el-option label="📈 营销" value="TrendCharts" />
            <el-option label="📖 语言" value="Reading" />
            <el-option label="📱 移动" value="Cellphone" />
            <el-option label="🔧 工具" value="Tools" />
          </el-select>
        </el-form-item>

        <div class="form-row">
          <el-form-item label="排序" class="form-item-half">
            <el-input-number
              v-model="categoryForm.sortOrder"
              :min="0"
              :max="999"
              class="full-width"
            />
          </el-form-item>

          <el-form-item label="启用状态" class="form-item-half">
            <el-switch v-model="categoryForm.enabled" />
          </el-form-item>
        </div>
      </el-form>
    </Modal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Grid, SuccessFilled, Reading, Refresh } from '@element-plus/icons-vue'
import request from '../../api/request'
import Modal from '../../components/Modal.vue'

const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const categoryDialogVisible = ref(false)
const isEdit = ref(false)

const categoryForm = ref({
  name: '',
  parentId: null,
  description: '',
  icon: '',
  sortOrder: 0,
  enabled: true
})

// Stats computed
const totalCategories = computed(() => {
  const countCategories = (cats) => {
    let count = cats.length
    cats.forEach(cat => {
      if (cat.children && cat.children.length > 0) {
        count += countCategories(cat.children)
      }
    })
    return count
  }
  return countCategories(categories.value)
})

const enabledCategories = computed(() => {
  const countEnabled = (cats) => {
    let count = 0
    cats.forEach(cat => {
      if (cat.enabled) count++
      if (cat.children && cat.children.length > 0) {
        count += countEnabled(cat.children)
      }
    })
    return count
  }
  return countEnabled(categories.value)
})

const totalCourses = computed(() => {
  const countCourses = (cats) => {
    let count = 0
    cats.forEach(cat => {
      count += cat.courseCount || 0
      if (cat.children && cat.children.length > 0) {
        count += countCourses(cat.children)
      }
    })
    return count
  }
  return countCourses(categories.value)
})

// Flat categories for parent selection
const flatCategories = computed(() => {
  const flatten = (cats, level = 0) => {
    let result = []
    cats.forEach(cat => {
      result.push({
        ...cat,
        name: level > 0 ? `${'　'.repeat(level)}├─ ${cat.name}` : cat.name
      })
      if (cat.children && cat.children.length > 0) {
        result = result.concat(flatten(cat.children, level + 1))
      }
    })
    return result
  }
  return flatten(categories.value)
})

onMounted(() => {
  loadCategories()
})

const loadCategories = async () => {
  loading.value = true
  try {
    // 临时使用公开的categories接口，因为admin接口可能有权限问题
    const res = await request({
      url: '/admin/categories',
      method: 'get'
    })
    categories.value = res.data || []
  } catch (error) {
    console.error('API Error:', error)
    ElMessage.error('加载分类失败')
    // 添加一些模拟数据作为后备
    categories.value = [
      {
        id: 1,
        name: '编程开发',
        description: '学习各种编程语言和开发技术',
        icon: 'Code',
        sortOrder: 1,
        enabled: true,
        courseCount: 25,
        children: [
          {
            id: 2,
            name: '前端开发',
            description: '学习HTML、CSS、JavaScript等前端技术',
            icon: 'Code',
            sortOrder: 1,
            enabled: true,
            courseCount: 12,
            children: []
          }
        ]
      }
    ]
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  categoryForm.value = {
    name: '',
    parentId: null,
    description: '',
    icon: '',
    sortOrder: 0,
    enabled: true
  }
  categoryDialogVisible.value = true
}

const editCategory = (row) => {
  isEdit.value = true
  categoryForm.value = { ...row }
  categoryDialogVisible.value = true
}

const saveCategory = async () => {
  if (!categoryForm.value.name.trim()) {
    ElMessage.warning('请输入分类名称')
    return
  }

  saving.value = true
  try {
    if (isEdit.value) {
      await request({
        url: `/admin/categories/${categoryForm.value.id}`,
        method: 'put',
        data: categoryForm.value
      })
      ElMessage.success('更新成功')
    } else {
      await request({
        url: '/admin/categories',
        method: 'post',
        data: categoryForm.value
      })
      ElMessage.success('创建成功')
    }
    categoryDialogVisible.value = false
    loadCategories()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    saving.value = false
  }
}

const deleteCategory = async (row) => {
  if (row.courseCount > 0) {
    ElMessage.warning('该分类下有课程，无法删除')
    return
  }

  if (row.children && row.children.length > 0) {
    ElMessage.warning('该分类下有子分类，无法删除')
    return
  }

  await ElMessageBox.confirm('确定要删除这个分类吗？', '确认删除', {
    type: 'warning',
    confirmButtonText: '确定删除',
    cancelButtonText: '取消'
  })

  try {
    await request({
      url: `/admin/categories/${row.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    loadCategories()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const toggleStatus = async (row) => {
  row.updating = true
  try {
    await request({
      url: `/admin/categories/${row.id}/status`,
      method: 'patch',
      data: { enabled: row.enabled }
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.enabled = !row.enabled
    ElMessage.error('状态更新失败')
  } finally {
    row.updating = false
  }
}
</script>

<style scoped>
.categories-admin {
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

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--space-6);
}

.stat-card {
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

.stat-card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.stat-icon.categories {
  background: linear-gradient(135deg, var(--primary), var(--primary-light));
}

.stat-icon.enabled {
  background: linear-gradient(135deg, var(--success), #059669);
}

.stat-icon.courses {
  background: linear-gradient(135deg, var(--warning), #d97706);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: var(--text-3xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  line-height: 1;
  margin-bottom: var(--space-1);
}

.stat-label {
  font-size: var(--text-sm);
  color: var(--gray-600);
}

/* Table Section */
.table-section {
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

.table-actions {
  display: flex;
  gap: var(--space-3);
}

/* Category Info */
.category-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.category-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background: var(--primary);
}

.category-icon.code {
  background: linear-gradient(135deg, #007acc, #005a9e);
}

.category-icon.brush {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
}

.category-icon.dataanalysis {
  background: linear-gradient(135deg, #4ecdc4, #44a08d);
}

.category-name {
  font-weight: var(--font-medium);
  color: var(--gray-900);
}

.description-text {
  color: var(--gray-600);
  font-size: var(--text-sm);
}

.sort-order {
  color: var(--gray-500);
  font-weight: var(--font-medium);
}

.action-buttons {
  display: flex;
  gap: var(--space-2);
}

/* Form Styles */
.category-form {
  padding: var(--space-4) 0;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-4);
}

.form-item-half {
  margin-bottom: var(--space-4);
}

.full-width {
  width: 100%;
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
@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: var(--space-6);
  }

  .welcome-header {
    flex-direction: column;
    text-align: center;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>