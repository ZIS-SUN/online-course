<template>
  <div class="settings-dashboard">
    <!-- Welcome Section -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-header">
          <div class="welcome-icon">
            <el-icon :size="32"><Setting /></el-icon>
          </div>
          <div>
            <h1>系统设置</h1>
            <p>管理系统配置和平台参数</p>
          </div>
        </div>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" size="large" @click="saveAllSettings" :loading="saving">
          <el-icon><Select /></el-icon>
          保存所有设置
        </el-button>
      </div>
    </div>

    <!-- Settings Content -->
    <div class="settings-content">
      <!-- Settings Navigation -->
      <div class="settings-nav">
        <div class="nav-section">
          <div class="nav-title">基础设置</div>
          <div
            v-for="item in basicSettings"
            :key="item.key"
            class="nav-item"
            :class="{ active: activeTab === item.key }"
            @click="switchTab(item.key)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </div>

        <div class="nav-section">
          <div class="nav-title">高级设置</div>
          <div
            v-for="item in advancedSettings"
            :key="item.key"
            class="nav-item"
            :class="{ active: activeTab === item.key }"
            @click="switchTab(item.key)"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </div>
        </div>
      </div>

      <!-- Settings Panel -->
      <div class="settings-panel">
        <!-- General Settings -->
        <div v-if="activeTab === 'general'" class="setting-section">
          <div class="section-header">
            <h3>基本信息</h3>
            <p>配置网站的基本信息和显示内容</p>
          </div>

          <div class="setting-form">
            <el-form label-width="120px">
              <el-form-item label="网站名称">
                <el-input v-model="settings.general.siteName" placeholder="输入网站名称" />
              </el-form-item>

              <el-form-item label="网站描述">
                <el-input
                  v-model="settings.general.siteDescription"
                  type="textarea"
                  :rows="3"
                  placeholder="输入网站描述"
                />
              </el-form-item>

              <el-form-item label="网站关键词">
                <el-input v-model="settings.general.siteKeywords" placeholder="用逗号分隔多个关键词" />
              </el-form-item>

              <el-form-item label="网站Logo">
                <div class="logo-upload">
                  <el-upload
                    class="logo-uploader"
                    action="#"
                    :show-file-list="false"
                    :before-upload="beforeLogoUpload"
                    :on-success="handleLogoSuccess"
                  >
                    <img v-if="settings.general.logoUrl" :src="settings.general.logoUrl" class="logo-preview" />
                    <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
                  </el-upload>
                  <div class="logo-tips">
                    <p>建议尺寸：200x60像素</p>
                    <p>支持格式：JPG、PNG、SVG</p>
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="联系邮箱">
                <el-input v-model="settings.general.contactEmail" placeholder="输入联系邮箱" />
              </el-form-item>

              <el-form-item label="客服电话">
                <el-input v-model="settings.general.contactPhone" placeholder="输入客服电话" />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- Course Settings -->
        <div v-if="activeTab === 'course'" class="setting-section">
          <div class="section-header">
            <h3>课程设置</h3>
            <p>配置课程相关的参数和规则</p>
          </div>

          <div class="setting-form">
            <el-form label-width="140px">
              <el-form-item label="课程审核">
                <el-switch v-model="settings.course.requireReview" />
                <span class="setting-desc">新发布的课程是否需要管理员审核</span>
              </el-form-item>

              <el-form-item label="自动发布">
                <el-switch v-model="settings.course.autoPublish" />
                <span class="setting-desc">审核通过后自动发布课程</span>
              </el-form-item>

              <el-form-item label="允许免费课程">
                <el-switch v-model="settings.course.allowFree" />
                <span class="setting-desc">是否允许发布免费课程</span>
              </el-form-item>

              <el-form-item label="最大课程时长">
                <el-input-number
                  v-model="settings.course.maxDuration"
                  :min="1"
                  :max="1000"
                  suffix="小时"
                />
                <span class="setting-desc">单个课程的最大时长限制</span>
              </el-form-item>

              <el-form-item label="最大章节数">
                <el-input-number
                  v-model="settings.course.maxChapters"
                  :min="1"
                  :max="100"
                />
                <span class="setting-desc">单个课程的最大章节数量</span>
              </el-form-item>

              <el-form-item label="默认价格">
                <el-input-number
                  v-model="settings.course.defaultPrice"
                  :min="0"
                  :precision="2"
                  prefix="￥"
                />
                <span class="setting-desc">新课程的默认价格</span>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- User Settings -->
        <div v-if="activeTab === 'user'" class="setting-section">
          <div class="section-header">
            <h3>用户设置</h3>
            <p>配置用户注册和账户管理相关设置</p>
          </div>

          <div class="setting-form">
            <el-form label-width="140px">
              <el-form-item label="允许注册">
                <el-switch v-model="settings.user.allowRegister" />
                <span class="setting-desc">是否允许新用户注册</span>
              </el-form-item>

              <el-form-item label="邮箱验证">
                <el-switch v-model="settings.user.requireEmailVerify" />
                <span class="setting-desc">注册时是否需要邮箱验证</span>
              </el-form-item>

              <el-form-item label="手机验证">
                <el-switch v-model="settings.user.requirePhoneVerify" />
                <span class="setting-desc">注册时是否需要手机验证</span>
              </el-form-item>

              <el-form-item label="密码强度">
                <el-radio-group v-model="settings.user.passwordStrength">
                  <el-radio value="weak">弱（6位以上）</el-radio>
                  <el-radio value="medium">中（8位以上，包含字母数字）</el-radio>
                  <el-radio value="strong">强（8位以上，包含字母数字特殊字符）</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="默认角色">
                <el-select v-model="settings.user.defaultRole">
                  <el-option label="学员" value="STUDENT" />
                  <el-option label="教师" value="TEACHER" />
                </el-select>
                <span class="setting-desc">新注册用户的默认角色</span>
              </el-form-item>

              <el-form-item label="登录有效期">
                <el-input-number
                  v-model="settings.user.sessionTimeout"
                  :min="1"
                  :max="168"
                  suffix="小时"
                />
                <span class="setting-desc">用户登录状态的有效期</span>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- Payment Settings -->
        <div v-if="activeTab === 'payment'" class="setting-section">
          <div class="section-header">
            <h3>支付设置</h3>
            <p>配置支付方式和相关参数</p>
          </div>

          <div class="setting-form">
            <el-form label-width="140px">
              <el-form-item label="支付宝支付">
                <el-switch v-model="settings.payment.alipayEnabled" />
                <span class="setting-desc">是否启用支付宝支付</span>
              </el-form-item>

              <el-form-item label="微信支付">
                <el-switch v-model="settings.payment.wechatEnabled" />
                <span class="setting-desc">是否启用微信支付</span>
              </el-form-item>

              <el-form-item label="平台佣金">
                <el-input-number
                  v-model="settings.payment.platformCommission"
                  :min="0"
                  :max="50"
                  :precision="1"
                  suffix="%"
                />
                <span class="setting-desc">平台从课程销售中抽取的佣金比例</span>
              </el-form-item>

              <el-form-item label="最低提现金额">
                <el-input-number
                  v-model="settings.payment.minWithdraw"
                  :min="1"
                  :precision="2"
                  prefix="￥"
                />
                <span class="setting-desc">教师提现的最低金额</span>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- Email Settings -->
        <div v-if="activeTab === 'email'" class="setting-section">
          <div class="section-header">
            <h3>邮件设置</h3>
            <p>配置邮件服务器和发送参数</p>
          </div>

          <div class="setting-form">
            <el-form label-width="140px">
              <el-form-item label="SMTP服务器">
                <el-input v-model="settings.email.smtpServer" placeholder="如：smtp.gmail.com" />
              </el-form-item>

              <el-form-item label="SMTP端口">
                <el-input-number v-model="settings.email.smtpPort" :min="1" :max="65535" />
              </el-form-item>

              <el-form-item label="发送邮箱">
                <el-input v-model="settings.email.fromEmail" placeholder="发送邮件的邮箱地址" />
              </el-form-item>

              <el-form-item label="邮箱密码">
                <el-input v-model="settings.email.password" type="password" show-password />
              </el-form-item>

              <el-form-item label="启用SSL">
                <el-switch v-model="settings.email.sslEnabled" />
              </el-form-item>

              <el-form-item>
                <el-button @click="testEmail" :loading="testingEmail">测试邮件发送</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- Storage Settings -->
        <div v-if="activeTab === 'storage'" class="setting-section">
          <div class="section-header">
            <h3>存储设置</h3>
            <p>配置文件存储和CDN相关设置</p>
          </div>

          <div class="setting-form">
            <el-form label-width="140px">
              <el-form-item label="存储方式">
                <el-radio-group v-model="settings.storage.type">
                  <el-radio value="local">本地存储</el-radio>
                  <el-radio value="oss">阿里云OSS</el-radio>
                  <el-radio value="cos">腾讯云COS</el-radio>
                  <el-radio value="qiniu">七牛云</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="最大文件大小">
                <el-input-number
                  v-model="settings.storage.maxFileSize"
                  :min="1"
                  :max="1000"
                  suffix="MB"
                />
              </el-form-item>

              <el-form-item label="允许的文件类型">
                <el-input
                  v-model="settings.storage.allowedTypes"
                  type="textarea"
                  :rows="2"
                  placeholder="如：jpg,png,pdf,mp4,webm"
                />
              </el-form-item>

              <el-form-item label="CDN域名">
                <el-input v-model="settings.storage.cdnDomain" placeholder="如：https://cdn.example.com" />
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- Security Settings -->
        <div v-if="activeTab === 'security'" class="setting-section">
          <div class="section-header">
            <h3>安全设置</h3>
            <p>配置系统安全相关参数</p>
          </div>

          <div class="setting-form">
            <el-form label-width="140px">
              <el-form-item label="登录失败限制">
                <el-input-number
                  v-model="settings.security.maxLoginAttempts"
                  :min="3"
                  :max="10"
                />
                <span class="setting-desc">连续登录失败次数限制</span>
              </el-form-item>

              <el-form-item label="锁定时间">
                <el-input-number
                  v-model="settings.security.lockoutDuration"
                  :min="5"
                  :max="1440"
                  suffix="分钟"
                />
                <span class="setting-desc">账户锁定时间</span>
              </el-form-item>

              <el-form-item label="启用验证码">
                <el-switch v-model="settings.security.captchaEnabled" />
                <span class="setting-desc">登录时是否启用验证码</span>
              </el-form-item>

              <el-form-item label="API访问限制">
                <el-input-number
                  v-model="settings.security.apiRateLimit"
                  :min="100"
                  :max="10000"
                  suffix="次/小时"
                />
                <span class="setting-desc">单个IP每小时API访问限制</span>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Setting,
  Select,
  Plus,
  Monitor,
  Reading,
  User,
  CreditCard,
  Message,
  FolderOpened,
  Lock
} from '@element-plus/icons-vue'

const activeTab = ref('general')
const saving = ref(false)
const testingEmail = ref(false)

// Navigation items
const basicSettings = [
  { key: 'general', label: '基本信息', icon: 'Monitor' },
  { key: 'course', label: '课程设置', icon: 'Reading' },
  { key: 'user', label: '用户设置', icon: 'User' },
  { key: 'payment', label: '支付设置', icon: 'CreditCard' }
]

const advancedSettings = [
  { key: 'email', label: '邮件设置', icon: 'Message' },
  { key: 'storage', label: '存储设置', icon: 'FolderOpened' },
  { key: 'security', label: '安全设置', icon: 'Lock' }
]

// Settings data
const settings = reactive({
  general: {
    siteName: 'LearnSpace 在线学习平台',
    siteDescription: '专业的在线教育平台，提供高质量的课程内容',
    siteKeywords: '在线教育,在线学习,课程平台,技能提升',
    logoUrl: '',
    contactEmail: 'contact@learnspace.com',
    contactPhone: '400-888-9999'
  },
  course: {
    requireReview: true,
    autoPublish: false,
    allowFree: true,
    maxDuration: 100,
    maxChapters: 50,
    defaultPrice: 99.00
  },
  user: {
    allowRegister: true,
    requireEmailVerify: true,
    requirePhoneVerify: false,
    passwordStrength: 'medium',
    defaultRole: 'STUDENT',
    sessionTimeout: 24
  },
  payment: {
    alipayEnabled: true,
    wechatEnabled: true,
    platformCommission: 10.0,
    minWithdraw: 100.00
  },
  email: {
    smtpServer: 'smtp.gmail.com',
    smtpPort: 587,
    fromEmail: 'noreply@learnspace.com',
    password: '',
    sslEnabled: true
  },
  storage: {
    type: 'local',
    maxFileSize: 100,
    allowedTypes: 'jpg,jpeg,png,gif,pdf,doc,docx,ppt,pptx,mp4,mp3,webm',
    cdnDomain: ''
  },
  security: {
    maxLoginAttempts: 5,
    lockoutDuration: 30,
    captchaEnabled: true,
    apiRateLimit: 1000
  }
})

const switchTab = (tab) => {
  activeTab.value = tab
}

const saveAllSettings = async () => {
  saving.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500))
    ElMessage.success('设置保存成功')
  } catch (error) {
    ElMessage.error('设置保存失败')
  } finally {
    saving.value = false
  }
}

const beforeLogoUpload = (file) => {
  const isValidType = ['image/jpeg', 'image/png', 'image/svg+xml'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isValidType) {
    ElMessage.error('Logo只能是 JPG、PNG、SVG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('Logo大小不能超过 2MB!')
    return false
  }
  return true
}

const handleLogoSuccess = (response, file) => {
  settings.general.logoUrl = URL.createObjectURL(file.raw)
  ElMessage.success('Logo上传成功')
}

const testEmail = async () => {
  testingEmail.value = true
  try {
    // Simulate email test
    await new Promise(resolve => setTimeout(resolve, 2000))
    ElMessage.success('测试邮件发送成功')
  } catch (error) {
    ElMessage.error('邮件发送失败，请检查配置')
  } finally {
    testingEmail.value = false
  }
}
</script>

<style scoped>
.settings-dashboard {
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

/* Settings Content */
.settings-content {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: var(--space-8);
}

/* Settings Navigation */
.settings-nav {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-6);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
  height: fit-content;
}

.nav-section {
  margin-bottom: var(--space-6);
}

.nav-section:last-child {
  margin-bottom: 0;
}

.nav-title {
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-500);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: var(--space-3);
  padding: 0 var(--space-3);
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  padding: var(--space-3);
  border-radius: var(--radius-lg);
  color: var(--gray-700);
  cursor: pointer;
  transition: var(--transition);
  margin-bottom: var(--space-1);
}

.nav-item:hover {
  background: var(--gray-50);
  color: var(--primary);
}

.nav-item.active {
  background: var(--primary);
  color: white;
}

.nav-item .el-icon {
  font-size: 18px;
}

/* Settings Panel */
.settings-panel {
  background: white;
  border-radius: var(--radius-xl);
  padding: var(--space-8);
  border: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.setting-section {
  max-width: 800px;
}

.section-header {
  margin-bottom: var(--space-8);
  padding-bottom: var(--space-6);
  border-bottom: 1px solid var(--gray-200);
}

.section-header h3 {
  font-size: var(--text-2xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0 0 var(--space-2) 0;
}

.section-header p {
  font-size: var(--text-base);
  color: var(--gray-600);
  margin: 0;
}

.setting-form {
  background: var(--gray-50);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
}

.setting-desc {
  font-size: var(--text-sm);
  color: var(--gray-500);
  margin-left: var(--space-3);
}

/* Logo Upload */
.logo-upload {
  display: flex;
  align-items: flex-start;
  gap: var(--space-4);
}

.logo-uploader {
  border: 2px dashed var(--gray-300);
  border-radius: var(--radius-lg);
  width: 120px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: var(--transition);
}

.logo-uploader:hover {
  border-color: var(--primary);
}

.logo-uploader-icon {
  font-size: 24px;
  color: var(--gray-400);
}

.logo-preview {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.logo-tips {
  flex: 1;
}

.logo-tips p {
  font-size: var(--text-sm);
  color: var(--gray-500);
  margin: 0 0 var(--space-1) 0;
}

/* Form Customizations */
:deep(.el-form-item) {
  margin-bottom: var(--space-6);
}

:deep(.el-form-item__label) {
  font-weight: var(--font-medium);
  color: var(--gray-700);
}

:deep(.el-input), :deep(.el-select), :deep(.el-textarea) {
  width: 100%;
}

:deep(.el-radio-group) {
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
}

:deep(.el-radio) {
  margin-right: 0;
}

/* Responsive */
@media (max-width: 1024px) {
  .settings-content {
    grid-template-columns: 1fr;
  }

  .settings-nav {
    order: 2;
  }

  .settings-panel {
    order: 1;
  }
}

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

  .welcome-actions .el-button {
    width: 100%;
  }

  .settings-nav {
    padding: var(--space-4);
  }

  .nav-item {
    padding: var(--space-4);
  }

  .settings-panel {
    padding: var(--space-6);
  }

  .logo-upload {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
}
</style>