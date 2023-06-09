import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import config from './config.json'

// https://vitejs.dev/config/
export default defineConfig({
  define: {
    'process.env': '{}',
  },
  server: {
    proxy: config.devServerProxy
  },
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
      '~FGV': path.resolve(__dirname, '../classes/FASTBALL-INF'),
      '~FCV': path.resolve(__dirname, '../../src/main/resources/FASTBALL-INF'),
    },
  },
  build: {
    chunkSizeWarningLimit: 4096
  }
})
