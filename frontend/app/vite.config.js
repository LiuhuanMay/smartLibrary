import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path';
// https://vite.dev/config/
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      script:{
        defineModel:true
      }
    }),
  ],
  resolve: {
    // 设置文件./src路径为 @
    alias: [
      {
        find: '@',
        replacement: resolve(__dirname, './src')
      }
    ]
  },
  server: {
    proxy: {
      // 代理的规则
      '/api': {
        target: 'http://localhost:8101', // 你的目标服务器地址
        changeOrigin: true, // 这一项表示是否改变请求头中的origin字段
        rewrite: (path) => path.replace(/^\/api/, '') // 重写路径，去掉/api前缀
      }
    }
  }
})