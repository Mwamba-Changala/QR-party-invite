import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import fs from 'fs';

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },  
  server: {
    https: {
      key: fs.readFileSync('./certs/localhost+2-key.pem'), // Adjust the path as needed
      cert: fs.readFileSync('./certs/localhost+2.pem'),    // Adjust the path as needed
    },
    port: 8021, // Matches the port from vue.config.js
    host: '0.0.0.0'
  },
})
