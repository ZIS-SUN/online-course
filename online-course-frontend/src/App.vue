<script setup>
import { provide } from 'vue'
import GlobalLoading from './components/GlobalLoading.vue'
import PageTransition from './components/PageTransition.vue'
import ThemeToggle from './components/ThemeToggle.vue'
import { useLoadingStore } from './stores/loading'

const loadingStore = useLoadingStore()

// 提供全局加载控制
provide('loading', loadingStore)
</script>

<template>
  <div id="app">
    <!-- 全局页面过渡 -->
    <router-view v-slot="{ Component, route }">
      <PageTransition :name="route.meta.transition || 'fade-slide'">
        <component :is="Component" :key="route.path" />
      </PageTransition>
    </router-view>

    <!-- 全局加载组件 -->
    <GlobalLoading
      :visible="loadingStore.isLoading"
      :title="loadingStore.loadingTitle"
      :message="loadingStore.loadingMessage"
      :show-progress="loadingStore.showProgress"
      :progress="loadingStore.progress"
      :allow-close="loadingStore.allowClose"
      @close="loadingStore.hideLoading()"
    />

    <!-- 主题切换按钮 -->
    <ThemeToggle />
  </div>
</template>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: var(--font-sans);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: var(--line-height-normal);
  color: var(--color-text-primary);
  background-color: var(--color-bg-secondary);
  font-size: var(--font-size-base);
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--color-gray-100);
}

::-webkit-scrollbar-thumb {
  background: var(--color-gray-400);
  border-radius: var(--radius-full);
  transition: background var(--transition-fast);
}

::-webkit-scrollbar-thumb:hover {
  background: var(--color-gray-500);
}

/* Focus styles */
*:focus {
  outline: 2px solid var(--color-primary);
  outline-offset: 2px;
}

*:focus:not(:focus-visible) {
  outline: none;
}

/* Selection styles */
::selection {
  background-color: rgba(99, 102, 241, 0.2);
  color: var(--color-text-primary);
}

/* Smooth transitions for all interactive elements */
a, button, input, textarea, select {
  transition: var(--transition-base);
}

/* Ensure images are responsive */
img {
  max-width: 100%;
  height: auto;
}

/* Remove default button styles */
button {
  border: none;
  background: none;
  cursor: pointer;
}

/* Utility classes */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
</style>
