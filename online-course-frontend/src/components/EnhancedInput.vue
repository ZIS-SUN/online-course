<template>
  <div class="enhanced-input-container" :class="containerClass">
    <label v-if="label" class="input-label" :for="inputId">
      {{ label }}
      <span v-if="required" class="required-mark">*</span>
    </label>

    <div class="input-wrapper" :class="wrapperClass">
      <div v-if="prefixIcon" class="input-prefix">
        <el-icon><component :is="prefixIcon" /></el-icon>
      </div>

      <input
        :id="inputId"
        ref="inputRef"
        :type="inputType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :readonly="readonly"
        :maxlength="maxlength"
        :class="inputClass"
        class="form-input"
        @input="handleInput"
        @blur="handleBlur"
        @focus="handleFocus"
        @keyup.enter="$emit('enter')"
      />

      <div v-if="suffixIcon || showPassword || showClear" class="input-suffix">
        <button
          v-if="showClear && modelValue && !disabled"
          type="button"
          class="clear-btn"
          @click="clearInput"
        >
          <el-icon><Close /></el-icon>
        </button>

        <button
          v-if="showPassword && (type === 'password' || inputType === 'password')"
          type="button"
          class="password-toggle"
          @click="togglePassword"
        >
          <el-icon v-if="inputType === 'password'"><View /></el-icon>
          <el-icon v-else><Hide /></el-icon>
        </button>

        <div v-if="suffixIcon" class="suffix-icon">
          <el-icon><component :is="suffixIcon" /></el-icon>
        </div>
      </div>

      <div v-if="loading" class="input-loading">
        <div class="loading-spinner"></div>
      </div>
    </div>

    <!-- 验证反馈 -->
    <Transition name="feedback">
      <div v-if="showFeedback" class="input-feedback" :class="feedbackClass">
        <el-icon class="feedback-icon">
          <component :is="feedbackIcon" />
        </el-icon>
        <span class="feedback-text">{{ feedbackMessage }}</span>
      </div>
    </Transition>

    <!-- 字符计数 -->
    <div v-if="showCount && maxlength" class="input-count" :class="{ 'count-warning': isCountWarning }">
      {{ modelValue?.length || 0 }} / {{ maxlength }}
    </div>

    <!-- 帮助文本 -->
    <div v-if="helpText" class="input-help">
      {{ helpText }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted } from 'vue'
import { generateId } from '../utils/helpers'

const props = defineProps({
  modelValue: {
    type: [String, Number],
    default: ''
  },
  type: {
    type: String,
    default: 'text'
  },
  label: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: ''
  },
  required: {
    type: Boolean,
    default: false
  },
  disabled: {
    type: Boolean,
    default: false
  },
  readonly: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  maxlength: {
    type: Number,
    default: null
  },
  showCount: {
    type: Boolean,
    default: false
  },
  showClear: {
    type: Boolean,
    default: false
  },
  showPassword: {
    type: Boolean,
    default: false
  },
  prefixIcon: {
    type: String,
    default: ''
  },
  suffixIcon: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['small', 'default', 'large'].includes(value)
  },
  status: {
    type: String,
    default: '',
    validator: (value) => ['', 'success', 'warning', 'error'].includes(value)
  },
  message: {
    type: String,
    default: ''
  },
  helpText: {
    type: String,
    default: ''
  },
  validateOnBlur: {
    type: Boolean,
    default: true
  },
  validateOnInput: {
    type: Boolean,
    default: false
  },
  rules: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue', 'blur', 'focus', 'clear', 'enter', 'validate'])

// 状态管理
const inputRef = ref()
const inputId = ref(generateId('input'))
const inputType = ref(props.type)
const isFocused = ref(false)
const validationStatus = ref('')
const validationMessage = ref('')

// 计算属性
const containerClass = computed(() => ({
  [`size-${props.size}`]: true,
  'is-disabled': props.disabled,
  'is-readonly': props.readonly,
  'is-focused': isFocused.value,
  'is-loading': props.loading,
  [`status-${currentStatus.value}`]: currentStatus.value
}))

const wrapperClass = computed(() => ({
  'has-prefix': props.prefixIcon,
  'has-suffix': props.suffixIcon || props.showPassword || props.showClear,
  'is-focused': isFocused.value,
  [`status-${currentStatus.value}`]: currentStatus.value
}))

const inputClass = computed(() => ({
  'has-prefix': props.prefixIcon,
  'has-suffix': props.suffixIcon || props.showPassword || props.showClear
}))

const currentStatus = computed(() => {
  return validationStatus.value || props.status
})

const showFeedback = computed(() => {
  return (validationMessage.value || props.message) && currentStatus.value
})

const feedbackMessage = computed(() => {
  return validationMessage.value || props.message
})

const feedbackClass = computed(() => ({
  [`feedback-${currentStatus.value}`]: currentStatus.value
}))

const feedbackIcon = computed(() => {
  const icons = {
    success: 'CircleCheck',
    warning: 'Warning',
    error: 'CircleClose'
  }
  return icons[currentStatus.value] || 'InfoFilled'
})

const isCountWarning = computed(() => {
  if (!props.maxlength) return false
  const length = props.modelValue?.length || 0
  return length > props.maxlength * 0.8
})

// 方法
const handleInput = (event) => {
  const value = event.target.value
  emit('update:modelValue', value)

  if (props.validateOnInput) {
    nextTick(() => validateInput(value))
  }
}

const handleFocus = (event) => {
  isFocused.value = true
  emit('focus', event)
}

const handleBlur = (event) => {
  isFocused.value = false
  emit('blur', event)

  if (props.validateOnBlur) {
    validateInput(event.target.value)
  }
}

const clearInput = () => {
  emit('update:modelValue', '')
  emit('clear')
  inputRef.value?.focus()
}

const togglePassword = () => {
  inputType.value = inputType.value === 'password' ? 'text' : 'password'
}

const validateInput = (value) => {
  if (!props.rules.length) return true

  for (const rule of props.rules) {
    const result = rule.validator ? rule.validator(value) : true

    if (!result) {
      validationStatus.value = 'error'
      validationMessage.value = rule.message || '验证失败'
      emit('validate', false, rule)
      return false
    }
  }

  validationStatus.value = 'success'
  validationMessage.value = ''
  emit('validate', true)
  return true
}

const focus = () => {
  inputRef.value?.focus()
}

const blur = () => {
  inputRef.value?.blur()
}


// 暴露方法
defineExpose({
  focus,
  blur,
  validate: () => validateInput(props.modelValue)
})
</script>

<style scoped>
.enhanced-input-container {
  width: 100%;
  margin-bottom: var(--space-4);
}

/* 标签样式 */
.input-label {
  display: block;
  font-size: var(--text-sm);
  font-weight: var(--font-semibold);
  color: var(--gray-700);
  margin-bottom: var(--space-2);
  transition: var(--transition);
}

.required-mark {
  color: var(--error);
  margin-left: var(--space-1);
}

/* 输入框容器 */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  border: 1px solid var(--gray-300);
  border-radius: var(--radius-lg);
  background: white;
  transition: var(--transition);
  overflow: hidden;
}

.input-wrapper:hover {
  border-color: var(--gray-400);
}

.input-wrapper.is-focused {
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(0, 86, 211, 0.1);
}

.input-wrapper.status-success {
  border-color: var(--success);
}

.input-wrapper.status-warning {
  border-color: var(--warning);
}

.input-wrapper.status-error {
  border-color: var(--error);
}

/* 前缀和后缀 */
.input-prefix,
.input-suffix {
  display: flex;
  align-items: center;
  padding: 0 var(--space-3);
  color: var(--gray-500);
}

.input-prefix {
  border-right: 1px solid var(--gray-200);
}

.input-suffix {
  border-left: 1px solid var(--gray-200);
  gap: var(--space-2);
}

/* 输入框 */
.form-input {
  flex: 1;
  padding: var(--space-3) var(--space-4);
  border: none;
  outline: none;
  font-size: var(--text-base);
  color: var(--gray-900);
  background: transparent;
  transition: var(--transition);
}

.form-input::placeholder {
  color: var(--gray-400);
}

.form-input:disabled {
  background: var(--gray-50);
  color: var(--gray-500);
  cursor: not-allowed;
}

.form-input.has-prefix {
  padding-left: 0;
}

.form-input.has-suffix {
  padding-right: 0;
}

/* 清除按钮和密码切换 */
.clear-btn,
.password-toggle {
  padding: var(--space-1);
  border-radius: var(--radius-base);
  background: none;
  border: none;
  color: var(--gray-400);
  cursor: pointer;
  transition: var(--transition);
  display: flex;
  align-items: center;
  justify-content: center;
}

.clear-btn:hover,
.password-toggle:hover {
  color: var(--gray-600);
  background: var(--gray-100);
}

/* 加载状态 */
.input-loading {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid var(--gray-200);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 验证反馈 */
.input-feedback {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  font-size: 0.75rem;
  font-weight: 500;
}

.feedback-success {
  color: var(--success);
}

.feedback-warning {
  color: var(--warning);
}

.feedback-error {
  color: var(--error);
}

.feedback-icon {
  font-size: 1rem;
}

/* 字符计数 */
.input-count {
  text-align: right;
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: var(--gray-500);
}

.input-count.count-warning {
  color: var(--warning);
}

/* 帮助文本 */
.input-help {
  margin-top: 0.25rem;
  font-size: 0.75rem;
  color: var(--gray-500);
  line-height: 1.4;
}

/* 尺寸变体 */
.size-small .form-input {
  padding: 0.5rem 0.75rem;
  font-size: 0.75rem;
}

.size-small .input-prefix,
.size-small .input-suffix {
  padding: 0 0.5rem;
}

.size-large .form-input {
  padding: 1rem 1.25rem;
  font-size: 1rem;
}

.size-large .input-prefix,
.size-large .input-suffix {
  padding: 0 1rem;
}

/* 禁用状态 */
.is-disabled .input-wrapper {
  background: var(--gray-50);
  border-color: var(--gray-200);
  cursor: not-allowed;
}

.is-disabled .input-label {
  color: var(--gray-500);
}

/* 只读状态 */
.is-readonly .input-wrapper {
  background: var(--gray-50);
}

/* 过渡动画 */
.feedback-enter-active,
.feedback-leave-active {
  transition: all 0.2s ease;
}

.feedback-enter-from,
.feedback-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

/* 深色模式支持 */
@media (prefers-color-scheme: dark) {
  .input-wrapper {
    background: var(--gray-800);
    border-color: var(--gray-600);
  }

  .form-input {
    color: var(--gray-100);
  }

  .input-label {
    color: var(--gray-300);
  }
}
</style>