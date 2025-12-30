import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path';
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import {ElementPlusResolver} from "unplugin-vue-components/resolvers";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue({
            script:{
                defineModel:true
            }
        }),
        AutoImport({
            resolvers: [ElementPlusResolver()],
        }),
        Components({
            resolvers: [ElementPlusResolver()],
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
