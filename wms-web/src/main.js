import Vue from 'vue'
import App from './App.vue'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import './assets/global.css';
import axios from "axios";
import VueRouter from 'vue-router';
import router from './router';
import store from './store';

Vue.prototype.$axios=axios;
Vue.config.productionTip=false
Vue.config.productionTip = false
Vue.prototype.$httpUrl='http://localhost:8091'
Vue.use(ElementUI,{size:'small'});
Vue.use(ElementUI);
Vue.use(VueRouter);
new Vue({
    store,
    router,
  render: h => h(App),
}).$mount('#app')
