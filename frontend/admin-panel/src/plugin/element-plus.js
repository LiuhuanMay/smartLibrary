import 'element-plus/dist/index.css'
//图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
export const ElPlugin=(app)=>{
    for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
        app.component(key, component)
    }
}
