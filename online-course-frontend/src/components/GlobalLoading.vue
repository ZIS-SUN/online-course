<template>
  <Transition name="loading-fade">
    <div v-if="visible" class="global-loading">
      <div class="loading-backdrop" @click="handleBackdropClick"></div>
      <div class="loading-content">
        <div class="loading-spinner">
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
          <div class="spinner-ring"></div>
        </div>
        <div class="loading-text">
          <h3>{{ title }}</h3>
          <p v-if="message">{{ message }}</p>
        </div>
        <div class="loading-progress" v-if="showProgress">
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progress + '%' }"></div>
          </div>
          <span class="progress-text">{{ progress }}%</span>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '加载中...'
  },
  message: {
    type: String,
    default: ''
  },
  showProgress: {
    type: Boolean,
    default: false
  },
  progress: {
    type: Number,
    default: 0
  },
  allowClose: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close'])

const handleBackdropClick = () => {
  if (props.allowClose) {
    emit('close')
  }
}
</script>

<style scoped>
.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(8px);
}

.loading-content {
  position: relative;
  background: white;
  border-radius: 20px;
  padding: 3rem 2rem;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  text-align: center;
  max-width: 400px;
  width: 90%;
  transform: scale(1);
  animation: loading-appear 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes loading-appear {
  from {
    opacity: 0;
    transform: scale(0.8) translateY(20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.loading-spinner {
  position: relative;
  width: 80px;
  height: 80px;
  margin: 0 auto 2rem;
}

.spinner-ring {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 3px solid transparent;
  border-radius: 50%;
  animation: spin 1.5s linear infinite;
}

.spinner-ring:nth-child(1) {
  border-top-color: #667eea;
  animation-delay: 0s;
}

.spinner-ring:nth-child(2) {
  border-right-color: #764ba2;
  animation-delay: -0.5s;
  width: 70%;
  height: 70%;
  top: 15%;
  left: 15%;
}

.spinner-ring:nth-child(3) {
  border-bottom-color: #f093fb;
  animation-delay: -1s;
  width: 40%;
  height: 40%;
  top: 30%;
  left: 30%;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.loading-text {
  margin-bottom: 1.5rem;
}

.loading-text h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.loading-text p {
  font-size: 0.875rem;
  color: #64748b;
  margin: 0;
}

.loading-progress {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.progress-bar {
  width: 100%;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 3px;
  transition: width 0.3s ease;
  animation: progress-shimmer 2s infinite;
}

@keyframes progress-shimmer {
  0% {
    background-position: -200% center;
  }
  100% {
    background-position: 200% center;
  }
}

.progress-text {
  font-size: 0.75rem;
  color: #64748b;
  font-weight: 500;
}

.loading-fade-enter-active,
.loading-fade-leave-active {
  transition: all 0.3s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}

.loading-fade-enter-from .loading-content,
.loading-fade-leave-to .loading-content {
  transform: scale(0.8) translateY(20px);
}

/* Dark mode support */
@media (prefers-color-scheme: dark) {
  .loading-content {
    background: #1e293b;
    color: white;
  }

  .loading-text h3 {
    color: white;
  }

  .loading-text p {
    color: #94a3b8;
  }

  .progress-text {
    color: #94a3b8;
  }
}
</style>