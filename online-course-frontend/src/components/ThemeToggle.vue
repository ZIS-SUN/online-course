<template>
  <div class="theme-toggle">
    <button
      class="theme-toggle-btn"
      @click="toggleTheme"
      :title="isDark ? '切换到亮色模式' : '切换到深色模式'"
    >
      <transition name="icon-fade" mode="out-in">
        <el-icon v-if="!isDark" key="sun" class="theme-icon sun">
          <Sunny />
        </el-icon>
        <el-icon v-else key="moon" class="theme-icon moon">
          <Moon />
        </el-icon>
      </transition>
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Sunny, Moon } from '@element-plus/icons-vue'

const isDark = ref(false)

const toggleTheme = () => {
  isDark.value = !isDark.value
  updateTheme(isDark.value)
}

const updateTheme = (dark) => {
  const root = document.documentElement
  const metaThemeColor = document.querySelector('meta[name="theme-color"]')

  if (dark) {
    root.setAttribute('data-theme', 'dark')
    localStorage.setItem('theme', 'dark')
    if (metaThemeColor) {
      metaThemeColor.setAttribute('content', '#18181b')
    }
  } else {
    root.removeAttribute('data-theme')
    localStorage.setItem('theme', 'light')
    if (metaThemeColor) {
      metaThemeColor.setAttribute('content', '#ffffff')
    }
  }
}

onMounted(() => {
  const savedTheme = localStorage.getItem('theme')
  const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches

  isDark.value = savedTheme === 'dark' || (!savedTheme && prefersDark)
  updateTheme(isDark.value)

  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
    if (!localStorage.getItem('theme')) {
      isDark.value = e.matches
      updateTheme(isDark.value)
    }
  })
})

watch(isDark, (newValue) => {
  document.body.style.transition = 'background-color 0.3s ease, color 0.3s ease'
})
</script>

<style scoped>
.theme-toggle {
  position: fixed;
  bottom: var(--spacing-6);
  right: var(--spacing-6);
  z-index: var(--z-sticky);
}

.theme-toggle-btn {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-full);
  background: var(--color-bg-elevated);
  border: 2px solid var(--color-border-primary);
  box-shadow: var(--shadow-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-base);
  backdrop-filter: blur(10px);
}

.theme-toggle-btn:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-xl);
  border-color: var(--color-primary);
}

.theme-toggle-btn:active {
  transform: scale(0.95);
}

.theme-icon {
  font-size: 24px;
  transition: transform var(--transition-base);
}

.theme-icon.sun {
  color: var(--color-warning);
}

.theme-icon.moon {
  color: var(--color-primary);
}

.theme-toggle-btn:hover .theme-icon {
  transform: rotate(180deg);
}

.icon-fade-enter-active,
.icon-fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.icon-fade-enter-from {
  opacity: 0;
  transform: scale(0.8) rotate(-90deg);
}

.icon-fade-leave-to {
  opacity: 0;
  transform: scale(0.8) rotate(90deg);
}

/* Pulse animation */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(99, 102, 241, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(99, 102, 241, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(99, 102, 241, 0);
  }
}

.theme-toggle-btn:focus {
  animation: pulse 1.5s;
  outline: none;
}

/* Responsive */
@media (max-width: 768px) {
  .theme-toggle {
    bottom: var(--spacing-4);
    right: var(--spacing-4);
  }

  .theme-toggle-btn {
    width: 48px;
    height: 48px;
  }

  .theme-icon {
    font-size: 20px;
  }
}
</style>