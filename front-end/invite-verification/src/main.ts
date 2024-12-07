import { createApp } from 'vue'
import { createPinia } from 'pinia'
import {VueQrcodeReader} from 'vue-qrcode-reader'


import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueQrcodeReader)


app.mount('#app')
