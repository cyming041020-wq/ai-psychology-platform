import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/auth'
import './styles/main.css'

const pinia = createPinia()
const app = createApp(App)

app.use(pinia).use(router).use(ElementPlus)

window.addEventListener('psychology:unauthorized', () => {
  const auth = useAuthStore(pinia)
  auth.logout()
  if (router.currentRoute.value.name !== 'login') {
    void router.push({ name: 'login', query: { expired: '1' } })
  }
})

app.mount('#app')
