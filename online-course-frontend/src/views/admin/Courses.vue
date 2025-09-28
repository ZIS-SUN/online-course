<template>
  <div class="course-management">
    <!-- 顶部操作栏 -->
    <div class="top-bar">
      <div class="page-info">
        <h1 class="page-title">课程管理</h1>
        <span class="page-desc">管理所有在线课程内容</span>
      </div>
      <el-button type="primary" @click="showCreateDialog" :icon="Plus">
        新建课程
      </el-button>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="statusFilter" placeholder="状态" clearable @change="loadCourses" style="width: 120px">
          <el-option label="全部状态" value="" />
          <el-option label="草稿" value="DRAFT" />
          <el-option label="已发布" value="PUBLISHED" />
          <el-option label="已下架" value="ARCHIVED" />
        </el-select>
        <el-select v-model="levelFilter" placeholder="难度" clearable @change="loadCourses" style="width: 120px">
          <el-option label="全部难度" value="" />
          <el-option label="入门" value="BEGINNER" />
          <el-option label="进阶" value="INTERMEDIATE" />
          <el-option label="高级" value="ADVANCED" />
        </el-select>
      </div>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索课程名称或描述"
        :prefix-icon="Search"
        clearable
        @keyup.enter="loadCourses"
        @clear="loadCourses"
        style="width: 300px"
      />
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="courses" v-loading="loading" stripe>
        <el-table-column label="课程信息" min-width="300">
          <template #default="{ row }">
            <div class="course-info">
              <img :src="row.thumbnail || '/default-course.jpg'" class="course-thumb" />
              <div class="course-details">
                <div class="course-name">{{ row.title }}</div>
                <div class="course-desc">{{ row.subtitle || '暂无描述' }}</div>
                <div class="course-tags">
                  <el-tag size="small" :type="getLevelType(row.level)">
                    {{ row.level === 'BEGINNER' ? '入门' : row.level === 'INTERMEDIATE' ? '进阶' : '高级' }}
                  </el-tag>
                  <el-tag size="small" v-if="row.isFree" type="success">免费</el-tag>
                  <el-tag size="small" v-else>¥{{ row.price || 0 }}</el-tag>
                </div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="学习人数" width="100" align="center">
          <template #default="{ row }">
            <span class="data-num">{{ row.enrollCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="章节数" width="100" align="center">
          <template #default="{ row }">
            <span class="data-num">{{ row.sectionCount || 0 }}</span>
          </template>
        </el-table-column>

        <el-table-column label="创建时间" width="180" align="center">
          <template #default="{ row }">
            <span class="time-text">{{ new Date(row.createdAt).toLocaleDateString() }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <el-button size="small" @click="editCourse(row)" :icon="Edit">编辑</el-button>
              <el-button size="small" @click="manageSections(row)" :icon="Menu">章节</el-button>
              <el-dropdown trigger="click">
                <el-button size="small" :icon="More" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="row.status === 'DRAFT'" @click="publishCourse(row)">
                      <el-icon><Check /></el-icon> 发布课程
                    </el-dropdown-item>
                    <el-dropdown-item v-if="row.status === 'PUBLISHED'" @click="archiveCourse(row)">
                      <el-icon><Files /></el-icon> 下架课程
                    </el-dropdown-item>
                    <el-dropdown-item v-if="row.status === 'ARCHIVED'" @click="republishCourse(row)">
                      <el-icon><Check /></el-icon> 重新发布
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="deleteCourse(row)" style="color: #f56c6c">
                      <el-icon><Delete /></el-icon> 删除课程
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadCourses"
          @current-change="loadCourses"
        />
      </div>
    </div>

    <!-- 创建/编辑课程对话框 -->
    <el-dialog
      v-model="courseDialogVisible"
      :title="isEdit ? '编辑课程' : '新建课程'"
      width="600"
      destroy-on-close
    >
      <el-form :model="courseForm" label-width="80px">
        <el-form-item label="课程名称" required>
          <el-input v-model="courseForm.title" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程简介">
          <el-input v-model="courseForm.subtitle" placeholder="请输入课程简介" />
        </el-form-item>
        <el-form-item label="课程分类">
          <el-select v-model="courseForm.categoryId" placeholder="请选择课程分类">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="详细描述">
          <el-input
            v-model="courseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入详细描述"
          />
        </el-form-item>
        <el-form-item label="课程难度">
          <el-radio-group v-model="courseForm.level">
            <el-radio-button value="BEGINNER">入门</el-radio-button>
            <el-radio-button value="INTERMEDIATE">进阶</el-radio-button>
            <el-radio-button value="ADVANCED">高级</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="课程价格">
          <el-input-number v-model="courseForm.price" :min="0" :precision="2" :disabled="courseForm.isFree" />
        </el-form-item>
        <el-form-item label="免费课程">
          <el-switch v-model="courseForm.isFree" @change="handleFreeChange" />
        </el-form-item>
        <el-form-item label="封面图片">
          <el-input v-model="courseForm.thumbnail" placeholder="请输入封面图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="courseDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCourse">保存</el-button>
      </template>
    </el-dialog>

    <!-- 章节管理对话框 -->
    <el-dialog
      v-model="sectionsDialogVisible"
      title="章节管理"
      width="900"
      destroy-on-close
    >
      <div class="section-header">
        <h3>{{ currentCourse?.title }}</h3>
        <el-button type="primary" size="small" @click="showCreateSectionDialog" :icon="Plus">
          添加章节
        </el-button>
      </div>

      <el-table :data="sections" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="章节标题" />
        <el-table-column label="视频数" width="80" align="center">
          <template #default="{ row }">
            <span>{{ row.videos?.length || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="manageVideos(row)" :icon="VideoPlay">视频</el-button>
            <el-button size="small" @click="editSection(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteSection(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 章节创建/编辑对话框 -->
    <el-dialog
      v-model="sectionFormDialogVisible"
      :title="isEditSection ? '编辑章节' : '新建章节'"
      width="500"
      destroy-on-close
    >
      <el-form :model="sectionForm" label-width="80px">
        <el-form-item label="章节标题" required>
          <el-input v-model="sectionForm.title" placeholder="请输入章节标题" />
        </el-form-item>
        <el-form-item label="章节描述">
          <el-input
            v-model="sectionForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入章节描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="sectionForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sectionFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSection">保存</el-button>
      </template>
    </el-dialog>

    <!-- 视频管理对话框 -->
    <el-dialog
      v-model="videosDialogVisible"
      title="视频管理"
      width="900"
      destroy-on-close
    >
      <div class="section-header">
        <h3>{{ currentSection?.title }}</h3>
        <el-button type="primary" size="small" @click="showCreateVideoDialog" :icon="Plus">
          添加视频
        </el-button>
      </div>

      <el-table :data="videos" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="title" label="视频标题" />
        <el-table-column label="时长" width="100" align="center">
          <template #default="{ row }">
            {{ formatDuration(row.durationSeconds) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getTranscodeStatusType(row.transcodeStatus)">
              {{ getTranscodeStatusText(row.transcodeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button size="small" @click="editVideo(row)" :icon="Edit">编辑</el-button>
            <el-button size="small" @click="previewVideo(row)" :icon="VideoPlay">预览</el-button>
            <el-button size="small" type="danger" @click="deleteVideo(row)" :icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 视频创建/编辑对话框 -->
    <el-dialog
      v-model="videoFormDialogVisible"
      :title="isEditVideo ? '编辑视频' : '添加视频'"
      width="600"
      destroy-on-close
    >
      <el-form :model="videoForm" label-width="100px">
        <el-form-item label="视频标题" required>
          <el-input v-model="videoForm.title" placeholder="请输入视频标题" />
        </el-form-item>
        <el-form-item label="视频描述">
          <el-input
            v-model="videoForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入视频描述"
          />
        </el-form-item>
        <el-form-item label="视频文件">
          <div v-if="videoForm.sourceUrl" class="uploaded-file">
            <span>{{ videoForm.sourceUrl }}</span>
            <el-button size="small" @click="clearVideoFile" :icon="Close">清除</el-button>
          </div>
          <el-upload
            v-else
            :before-upload="beforeVideoUpload"
            :http-request="uploadVideoFile"
            accept="video/*"
            :show-file-list="false"
            :disabled="uploadingVideo"
          >
            <el-button type="primary" :icon="Upload" :loading="uploadingVideo">
              {{ uploadingVideo ? '上传中...' : '上传视频' }}
            </el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="视频时长(秒)">
          <el-input-number v-model="videoForm.durationSeconds" :min="0" />
        </el-form-item>
        <el-form-item label="是否免费">
          <el-switch v-model="videoForm.isFree" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="videoForm.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="videoFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveVideo">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Document,
  Plus,
  Search,
  Close,
  Check,
  Edit,
  Files,
  More,
  Menu,
  Delete,
  Refresh,
  Money,
  VideoPlay,
  Calendar,
  Upload
} from '@element-plus/icons-vue'
import request from '../../api/request'

const courses = ref([])
const categories = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const levelFilter = ref('')

const courseDialogVisible = ref(false)
const isEdit = ref(false)
const courseForm = ref({
  title: '',
  subtitle: '',
  description: '',
  level: 'BEGINNER',
  price: 0,
  thumbnail: ''
})

const sectionsDialogVisible = ref(false)
const sections = ref([])
const currentCourse = ref(null)

const sectionFormDialogVisible = ref(false)
const isEditSection = ref(false)
const sectionForm = ref({
  title: '',
  description: '',
  sortOrder: 0
})

const videosDialogVisible = ref(false)
const videos = ref([])
const currentSection = ref(null)
const uploadingVideo = ref(false)

const videoFormDialogVisible = ref(false)
const isEditVideo = ref(false)
const videoForm = ref({
  title: '',
  description: '',
  sourceUrl: '',
  durationSeconds: 0,
  isFree: false,
  sortOrder: 0
})

onMounted(() => {
  loadCourses()
  loadCategories()
})

const loadCategories = async () => {
  try {
    const res = await request({
      url: '/categories',
      method: 'get'
    })
    categories.value = res.data
  } catch (error) {
    console.error('Failed to load categories:', error)
  }
}

const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1, // Spring分页从0开始，前端从1开始
      size: pageSize.value
    }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (statusFilter.value) params.status = statusFilter.value
    if (levelFilter.value) params.level = levelFilter.value

    const res = await request({
      url: '/admin/courses',
      method: 'get',
      params
    })
    courses.value = res.data.content || res.data.list || []
    total.value = res.data.totalElements || res.data.total || 0
  } catch (error) {
    ElMessage.error('加载课程失败')
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  courseForm.value = {
    title: '',
    subtitle: '',
    description: '',
    level: 'BEGINNER',
    price: 0,
    thumbnail: '',
    status: 'DRAFT',
    isFree: false
  }
  courseDialogVisible.value = true
}

const editCourse = (row) => {
  isEdit.value = true
  courseForm.value = { ...row }
  // 确保isFree有值
  if (courseForm.value.isFree === undefined) {
    courseForm.value.isFree = courseForm.value.price === 0
  }
  courseDialogVisible.value = true
}

const handleFreeChange = (value) => {
  if (value) {
    courseForm.value.price = 0
  }
}

const saveCourse = async () => {
  try {
    if (isEdit.value) {
      await request({
        url: `/admin/courses/${courseForm.value.id}`,
        method: 'put',
        data: courseForm.value
      })
      ElMessage.success('更新成功')
    } else {
      await request({
        url: '/admin/courses',
        method: 'post',
        data: courseForm.value
      })
      ElMessage.success('创建成功')
    }
    courseDialogVisible.value = false
    loadCourses()
  } catch (error) {
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  }
}

const deleteCourse = async (row) => {
  await ElMessageBox.confirm('确定要删除这个课程吗？', '确认删除', {
    type: 'warning'
  })

  try {
    await request({
      url: `/admin/courses/${row.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    loadCourses()
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const publishCourse = async (row) => {
  try {
    await request({
      url: `/admin/courses/${row.id}/publish`,
      method: 'post'
    })
    ElMessage.success('发布成功')
    loadCourses()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const archiveCourse = async (row) => {
  try {
    await request({
      url: `/admin/courses/${row.id}/archive`,
      method: 'post'
    })
    ElMessage.success('下架成功')
    loadCourses()
  } catch (error) {
    ElMessage.error('下架失败')
  }
}

const republishCourse = async (row) => {
  try {
    await request({
      url: `/admin/courses/${row.id}/republish`,
      method: 'post'
    })
    ElMessage.success('重新发布成功')
    loadCourses()
  } catch (error) {
    ElMessage.error('重新发布失败')
  }
}

const manageSections = async (course) => {
  currentCourse.value = course
  try {
    const res = await request({
      url: `/admin/courses/${course.id}/sections`,
      method: 'get'
    })
    sections.value = res.data
    sectionsDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载章节失败')
  }
}

const showCreateSectionDialog = () => {
  isEditSection.value = false
  sectionForm.value = {
    title: '',
    description: '',
    sortOrder: sections.value.length
  }
  sectionFormDialogVisible.value = true
}

const editSection = (row) => {
  isEditSection.value = true
  sectionForm.value = { ...row }
  sectionFormDialogVisible.value = true
}

const saveSection = async () => {
  try {
    if (isEditSection.value) {
      await request({
        url: `/admin/courses/sections/${sectionForm.value.id}`,
        method: 'put',
        data: sectionForm.value
      })
      ElMessage.success('更新成功')
    } else {
      await request({
        url: `/admin/courses/${currentCourse.value.id}/sections`,
        method: 'post',
        data: sectionForm.value
      })
      ElMessage.success('创建成功')
    }
    sectionFormDialogVisible.value = false
    manageSections(currentCourse.value)
  } catch (error) {
    ElMessage.error(isEditSection.value ? '更新失败' : '创建失败')
  }
}

const manageVideos = async (section) => {
  currentSection.value = section
  try {
    const res = await request({
      url: `/admin/courses/sections/${section.id}/videos`,
      method: 'get'
    })
    videos.value = res.data
    videosDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载视频失败')
  }
}

const showCreateVideoDialog = () => {
  isEditVideo.value = false
  videoForm.value = {
    title: '',
    description: '',
    sourceUrl: '',
    durationSeconds: 0,
    isFree: false,
    sortOrder: videos.value.length
  }
  videoFormDialogVisible.value = true
}

const editVideo = (row) => {
  isEditVideo.value = true
  videoForm.value = { ...row }
  videoFormDialogVisible.value = true
}

const saveVideo = async () => {
  try {
    if (isEditVideo.value) {
      await request({
        url: `/admin/courses/videos/${videoForm.value.id}`,
        method: 'put',
        data: videoForm.value
      })
      ElMessage.success('更新成功')
    } else {
      await request({
        url: `/admin/courses/sections/${currentSection.value.id}/videos`,
        method: 'post',
        data: videoForm.value
      })
      ElMessage.success('创建成功')
    }
    videoFormDialogVisible.value = false
    manageVideos(currentSection.value)
  } catch (error) {
    ElMessage.error(isEditVideo.value ? '更新失败' : '创建失败')
  }
}

const deleteVideo = async (video) => {
  await ElMessageBox.confirm('确定要删除这个视频吗？', '确认删除', {
    type: 'warning'
  })

  try {
    await request({
      url: `/admin/courses/videos/${video.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    manageVideos(currentSection.value)
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const previewVideo = (video) => {
  window.open(`/video/${video.id}`, '_blank')
}

const beforeVideoUpload = (file) => {
  const isVideo = file.type.startsWith('video/')
  const isLt100M = file.size / 1024 / 1024 < 100

  if (!isVideo) {
    ElMessage.error('只能上传视频文件!')
    return false
  }
  if (!isLt100M) {
    ElMessage.error('上传视频大小不能超过 100MB!')
    return false
  }
  return true
}

const uploadVideoFile = async ({ file }) => {
  const formData = new FormData()
  formData.append('file', file)

  uploadingVideo.value = true

  try {
    const res = await request({
      url: '/admin/courses/videos/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000 // 60秒超时
    })

    videoForm.value.sourceUrl = res.data
    if (!videoForm.value.title) {
      videoForm.value.title = file.name.replace(/\.[^/.]+$/, "")
    }

    // 尝试获取视频时长
    const video = document.createElement('video')
    video.preload = 'metadata'
    video.onloadedmetadata = () => {
      if (!videoForm.value.durationSeconds) {
        videoForm.value.durationSeconds = Math.round(video.duration)
      }
      URL.revokeObjectURL(video.src)
    }
    video.src = URL.createObjectURL(file)

    ElMessage.success('视频文件上传成功')
  } catch (error) {
    if (error.response?.status === 413) {
      ElMessage.error('视频文件太大，请选择小于100MB的文件')
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('上传超时，请检查网络连接')
    } else {
      ElMessage.error('视频文件上传失败：' + (error.response?.data?.message || error.message))
    }
    console.error('Upload error:', error)
  } finally {
    uploadingVideo.value = false
  }
}

const clearVideoFile = () => {
  videoForm.value.sourceUrl = ''
  videoForm.value.title = ''
  videoForm.value.durationSeconds = 0
}

const formatDuration = (seconds) => {
  if (!seconds) return '0:00'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const getTranscodeStatusType = (status) => {
  const types = {
    ORIGINAL: 'info',
    TRANSCODING: 'warning',
    HLS_READY: 'success',
    FAILED: 'danger'
  }
  return types[status] || 'info'
}

const getTranscodeStatusText = (status) => {
  const texts = {
    ORIGINAL: '原始',
    TRANSCODING: '转码中',
    HLS_READY: '就绪',
    FAILED: '失败'
  }
  return texts[status] || status
}

const deleteSection = async (section) => {
  await ElMessageBox.confirm('确定要删除这个章节吗？', '确认删除', {
    type: 'warning'
  })

  try {
    await request({
      url: `/admin/courses/sections/${section.id}`,
      method: 'delete'
    })
    ElMessage.success('删除成功')
    manageSections(currentCourse.value)
  } catch (error) {
    ElMessage.error('删除失败')
  }
}

const getLevelType = (level) => {
  const types = {
    BEGINNER: 'success',
    INTERMEDIATE: 'warning',
    ADVANCED: 'danger'
  }
  return types[level] || 'info'
}

const getStatusType = (status) => {
  const types = {
    DRAFT: 'info',
    PUBLISHED: 'success',
    ARCHIVED: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已下架'
  }
  return texts[status] || status
}
</script>

<style scoped>
.course-management {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.page-info {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.page-desc {
  color: #909399;
  font-size: 14px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  margin-bottom: 20px;
  gap: 16px;
}

.filter-left {
  display: flex;
  gap: 12px;
}

.table-container {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  overflow: hidden;
}

.table-container :deep(.el-table) {
  font-size: 14px;
}

.table-container :deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
  height: 56px;
}

.table-container :deep(.el-table td) {
  padding: 16px 0;
}

.course-info {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.course-thumb {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.course-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.course-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.course-desc {
  font-size: 13px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 300px;
}

.course-tags {
  display: flex;
  gap: 8px;
}

.data-num {
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.time-text {
  font-size: 13px;
  color: #909399;
}

.action-btns {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fafbfc;
  border-top: 1px solid #ebeef5;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.uploaded-file {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.uploaded-file span {
  flex: 1;
  font-size: 14px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #ebeef5;
  padding: 20px 24px;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 24px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-button) {
  border-radius: 6px;
  font-weight: 500;
}

:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-select) {
  border-radius: 6px;
}

:deep(.el-tag) {
  border-radius: 4px;
  font-weight: 500;
}

:deep(.el-dropdown-menu) {
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
  padding: 8px;
}

:deep(.el-dropdown-menu__item) {
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  transition: background 0.2s;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: #f5f7fa;
}

:deep(.el-table__empty-text) {
  padding: 40px 0;
}

:deep(.el-loading-mask) {
  background-color: rgba(255,255,255,0.9);
}
</style>