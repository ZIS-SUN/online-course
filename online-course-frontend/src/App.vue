<script setup>
import { provide } from 'vue'
import GlobalLoading from './components/GlobalLoading.vue'
import PageTransition from './components/PageTransition.vue'
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
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.6;
  color: var(--gray-900);
  background-color: var(--gray-50);
}

/* Custom scrollbar */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--gray-100);
}

::-webkit-scrollbar-thumb {
  background: var(--gray-400);
  border-radius: var(--radius-full);
}

::-webkit-scrollbar-thumb:hover {
  background: var(--gray-500);
}

/* Focus styles */
*:focus {
  outline: 2px solid var(--primary);
  outline-offset: 2px;
}

*:focus:not(:focus-visible) {
  outline: none;
}

/* Selection styles */
::selection {
  background-color: rgba(0, 86, 211, 0.2);
  color: var(--gray-900);
}

/* Smooth transitions for all interactive elements */
a, button, input, textarea, select {
  transition: var(--transition);
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
