<template>
  <div class="loading-spinner" :class="{ 'fullscreen': fullscreen }" v-if="visible">
    <div class="spinner-container">
      <div class="spinner" :class="size">
        <div class="dot1"></div>
        <div class="dot2"></div>
        <div class="dot3"></div>
        <div class="dot4"></div>
        <div class="dot5"></div>
        <div class="dot6"></div>
        <div class="dot7"></div>
        <div class="dot8"></div>
      </div>
      <div v-if="text" class="loading-text">{{ text }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: true
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  text: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.loading-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}

.loading-spinner.fullscreen {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(8px);
  z-index: 9999;
  min-height: 100vh;
}

.spinner-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-4);
}

.spinner {
  position: relative;
  display: inline-block;
  animation: spin 1.2s linear infinite;
}

.spinner.small {
  width: 32px;
  height: 32px;
}

.spinner.medium {
  width: 48px;
  height: 48px;
}

.spinner.large {
  width: 64px;
  height: 64px;
}

.spinner div {
  position: absolute;
  width: 6px;
  height: 6px;
  background: var(--primary);
  border-radius: 50%;
  animation: pulse 1.2s ease-in-out infinite;
}

.spinner.small div {
  width: 4px;
  height: 4px;
}

.spinner.large div {
  width: 8px;
  height: 8px;
}

.dot1 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(0deg) translateY(-20px); animation-delay: -1.1s; }
.dot2 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(45deg) translateY(-20px); animation-delay: -1.0s; }
.dot3 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(90deg) translateY(-20px); animation-delay: -0.9s; }
.dot4 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(135deg) translateY(-20px); animation-delay: -0.8s; }
.dot5 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(180deg) translateY(-20px); animation-delay: -0.7s; }
.dot6 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(225deg) translateY(-20px); animation-delay: -0.6s; }
.dot7 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(270deg) translateY(-20px); animation-delay: -0.5s; }
.dot8 { top: 50%; left: 50%; transform: translate(-50%, -50%) rotate(315deg) translateY(-20px); animation-delay: -0.4s; }

.spinner.small .dot1, .spinner.small .dot2, .spinner.small .dot3, .spinner.small .dot4,
.spinner.small .dot5, .spinner.small .dot6, .spinner.small .dot7, .spinner.small .dot8 {
  transform: translate(-50%, -50%) rotate(var(--rotation, 0deg)) translateY(-12px);
}

.spinner.large .dot1, .spinner.large .dot2, .spinner.large .dot3, .spinner.large .dot4,
.spinner.large .dot5, .spinner.large .dot6, .spinner.large .dot7, .spinner.large .dot8 {
  transform: translate(-50%, -50%) rotate(var(--rotation, 0deg)) translateY(-28px);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes pulse {
  0%, 39%, 100% {
    opacity: 0.3;
    transform: scale(0.6);
  }
  40% {
    opacity: 1;
    transform: scale(1);
  }
}

.loading-text {
  font-size: var(--text-sm);
  color: var(--gray-600);
  font-weight: var(--font-medium);
  animation: fadeInOut 2s ease-in-out infinite;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0.6; }
  50% { opacity: 1; }
}
</style>