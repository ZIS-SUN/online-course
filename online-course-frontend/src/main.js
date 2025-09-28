import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './style.css'
import './styles/design-system.css'
import './styles/animations.css'

// Global Components
import LoadingSpinner from './components/LoadingSpinner.vue'
import PageTransition from './components/PageTransition.vue'
import Modal from './components/Modal.vue'

const app = createApp(App)
const pinia = createPinia()

// Register all Element Plus icons
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// Register global components
app.component('LoadingSpinner', LoadingSpinner)
app.component('PageTransition', PageTransition)
app.component('Modal', Modal)

app.use(pinia)
app.use(router)
app.use(ElementPlus)

app.mount('#app')
