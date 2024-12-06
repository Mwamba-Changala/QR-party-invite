import { createApp } from 'vue'
import { createPinia } from 'pinia'
import {VueQrcodeReader} from 'vue-qrcode-reader'
import Vue3EasyDataTable from 'vue3-easy-data-table';
import 'vue3-easy-data-table/dist/style.css';

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(VueQrcodeReader)
app.component('EasyDataTable',Vue3EasyDataTable )

app.mount('#app')
