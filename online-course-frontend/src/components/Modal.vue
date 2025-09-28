<template>
  <teleport to="body">
    <transition name="modal" appear>
      <div
        v-if="visible"
        class="modal-overlay"
        @click="handleOverlayClick"
      >
        <div
          class="modal-container"
          :class="[size, { 'full-width': fullWidth }]"
          @click.stop
        >
          <!-- Header -->
          <div v-if="showHeader" class="modal-header">
            <div class="modal-title-section">
              <div v-if="icon" class="modal-icon">
                <el-icon :size="20"><component :is="icon" /></el-icon>
              </div>
              <h3 class="modal-title">{{ title }}</h3>
            </div>
            <button
              v-if="closable"
              class="modal-close"
              @click="handleClose"
            >
              <el-icon><Close /></el-icon>
            </button>
          </div>

          <!-- Body -->
          <div class="modal-body" :class="{ 'no-header': !showHeader, 'no-footer': !showFooter }">
            <slot />
          </div>

          <!-- Footer -->
          <div v-if="showFooter" class="modal-footer">
            <slot name="footer">
              <div class="modal-actions">
                <el-button
                  v-if="showCancel"
                  size="large"
                  @click="handleCancel"
                  :disabled="loading"
                >
                  {{ cancelText }}
                </el-button>
                <el-button
                  v-if="showConfirm"
                  type="primary"
                  size="large"
                  @click="handleConfirm"
                  :loading="loading"
                >
                  {{ confirmText }}
                </el-button>
              </div>
            </slot>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { computed, watch, nextTick } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: ''
  },
  icon: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large', 'extra-large'].includes(value)
  },
  fullWidth: {
    type: Boolean,
    default: false
  },
  closable: {
    type: Boolean,
    default: true
  },
  maskClosable: {
    type: Boolean,
    default: true
  },
  showHeader: {
    type: Boolean,
    default: true
  },
  showFooter: {
    type: Boolean,
    default: false
  },
  showCancel: {
    type: Boolean,
    default: true
  },
  showConfirm: {
    type: Boolean,
    default: true
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  confirmText: {
    type: String,
    default: '确定'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'close', 'cancel', 'confirm'])

const handleOverlayClick = () => {
  if (props.maskClosable && props.closable) {
    handleClose()
  }
}

const handleClose = () => {
  emit('update:visible', false)
  emit('close')
}

const handleCancel = () => {
  emit('update:visible', false)
  emit('cancel')
}

const handleConfirm = () => {
  emit('confirm')
}

// Handle escape key
watch(() => props.visible, (visible) => {
  if (visible) {
    document.addEventListener('keydown', handleEscape)
    document.body.style.overflow = 'hidden'
  } else {
    document.removeEventListener('keydown', handleEscape)
    document.body.style.overflow = ''
  }
})

const handleEscape = (e) => {
  if (e.key === 'Escape' && props.closable) {
    handleClose()
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--space-4);
}

.modal-container {
  background: white;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-2xl);
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  border: 1px solid var(--gray-200);
}

/* Sizes */
.modal-container.small {
  width: 100%;
  max-width: 400px;
}

.modal-container.medium {
  width: 100%;
  max-width: 600px;
}

.modal-container.large {
  width: 100%;
  max-width: 800px;
}

.modal-container.extra-large {
  width: 100%;
  max-width: 1200px;
}

.modal-container.full-width {
  width: 100%;
  max-width: none;
  margin: var(--space-4);
}

/* Header */
.modal-header {
  padding: var(--space-6) var(--space-8);
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--gray-50);
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

.modal-title-section {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.modal-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  background: var(--primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-title {
  font-size: var(--text-xl);
  font-weight: var(--font-bold);
  color: var(--gray-900);
  margin: 0;
}

.modal-close {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-lg);
  background: var(--gray-100);
  border: none;
  color: var(--gray-500);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition);
}

.modal-close:hover {
  background: var(--gray-200);
  color: var(--gray-700);
}

/* Body */
.modal-body {
  padding: var(--space-8);
  overflow-y: auto;
  flex: 1;
}

.modal-body.no-header {
  border-radius: var(--radius-xl) var(--radius-xl) 0 0;
}

.modal-body.no-footer {
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

.modal-body.no-header.no-footer {
  border-radius: var(--radius-xl);
}

/* Footer */
.modal-footer {
  padding: var(--space-6) var(--space-8);
  border-top: 1px solid var(--gray-200);
  background: var(--gray-50);
  border-radius: 0 0 var(--radius-xl) var(--radius-xl);
}

.modal-actions {
  display: flex;
  gap: var(--space-3);
  justify-content: flex-end;
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  transform: scale(0.95) translateY(20px);
}

.modal-enter-to .modal-container,
.modal-leave-from .modal-container {
  transform: scale(1) translateY(0);
}

/* Responsive */
@media (max-width: 768px) {
  .modal-overlay {
    padding: var(--space-2);
  }

  .modal-container {
    max-height: 95vh;
  }

  .modal-header {
    padding: var(--space-4) var(--space-6);
  }

  .modal-body {
    padding: var(--space-6);
  }

  .modal-footer {
    padding: var(--space-4) var(--space-6);
  }

  .modal-title {
    font-size: var(--text-lg);
  }

  .modal-actions {
    flex-direction: column-reverse;
  }

  .modal-actions .el-button {
    width: 100%;
  }
}
</style>