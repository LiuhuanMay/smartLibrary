import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
const app = createApp(App)
const pinia = createPinia()
import {ElPlugin} from "@/plugin/element-plus.js";
ElPlugin(app)
app.use(router)
app.use(pinia)
app.mount('#app')