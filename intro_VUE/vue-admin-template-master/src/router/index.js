import Vue from 'vue'
import Router from 'vue-router'
import cookie from 'js-cookie'

// in development-env not use lazy-loading, because lazy-loading too many pages will cause webpack hot update too slow. so only in production use lazy-loading;
// detail: https://panjiachen.github.io/vue-element-admin-site/#/lazy-loading

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'

/**
* hidden: true                   if `hidden:true` will not show in the sidebar(default is false)
* alwaysShow: true               if set true, will always show the root menu, whatever its child routes length
*                                if not set alwaysShow, only more than one route under the children
*                                it will becomes nested mode, otherwise not show the root menu
* redirect: noredirect           if `redirect:noredirect` will no redirect in the breadcrumb
* name:'router-name'             the name is used by <keep-alive> (must set!!!)
* meta : {
    title: 'title'               the name show in submenu and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar,
  }
**/
export const constantRouterMap = [
  { path: '/login', component: () => import('@/views/login/index'), hidden: true },
  { path: '/404', component: () => import('@/views/404'), hidden: true },

  //计算机导论学习系统-首页
  {
    path: '/',
    component: Layout,
    redirect: '/introduction',
    name: '计算机导论系统首页',
    children: [{
      path: 'introduction',
      name: '计算机导论',
      component: () => import('@/views/introduction/index'),
      meta: {title:'计算机导论',icon: 'guide'}
    }]
  },

  //计算机导论学习系统-待办事项
  {
    path: '/schedule',
    component: Layout,
    redirect: '/schedule/todolist', //默认地址
    name: '计算机导论系统待办事项',
    meta: { title: '待办事项', icon: 'list' },
    hidden: cookie.get('intro_admin') === "false"? false : true, //教师端隐藏
    // roles: [],
    children: [
      {
        path: 'todolist',
        name: '待办事项',
        component: () => import('@/views/intro/schedule/todolist'),
        meta: {title:'待办事项',icon: 'nested'}
      },
      {
        path: 'urgent',
        name: '紧急事项',
        component: () => import('@/views/intro/schedule/urgent'),
        meta: {title:'紧急事项',icon: 'message'}
      },
      {
        path: 'missed',
        name: '错过的任务',
        component: () => import('@/views/intro/schedule/missed'),
        meta: {title:'已错过任务',icon: 'bug'}
      }
    ]
  },

    //计算机导论学习系统-我的练习
    {
      path: '/practice',
      component: Layout,
      redirect: '/practice/preview', //默认地址
      name: '我的练习',
      meta: { title: '我的练习', icon: 'form' },
      children: [
        {
          path: 'preview',
          name: '课前预习',
          component: () => import('@/views/intro/practice/preview/index'),
          meta: {title:'课前预习',icon: 'clipboard'},
        },
        {
          path: 'showPreview/:id',
          name: '教师端查看课前预习',
          component: () => import('@/views/intro/practice/preview/showPreview'), //路由对应列表页面
          meta: { title: '教师端查看课前预习', noCache: true },
          hidden: true
        },
        {
          path: 'homework',
          name: '课后作业',
          component: () => import('@/views/intro/practice/homework/index'),
          meta: {title:'课后作业',icon: 'clipboard'}
        },
        {
          path: 'showHomework/:id',
          name: '教师端查看课后作业',
          component: () => import('@/views/intro/practice/homework/showHomework'), //路由对应列表页面
          meta: { title: '教师端查看课后作业', noCache: true },
          hidden: true
        },
        {
          path: 'timedtest',
          name: '课堂练习',
          component: () => import('@/views/intro/practice/timedtest/index'),
          meta: {title:'课堂练习',icon: 'clipboard'}
        },
        {
          path: 'showTimedtest/:id',
          name: '教师端查看随堂测验',
          component: () => import('@/views/intro/practice/timedtest/showTimedtest'), //路由对应列表页面
          meta: { title: '教师端查看随堂测验', noCache: true },
          hidden: true
        },
        {
          path: 'experiment',
          name: '实验练习',
          component: () => import('@/views/intro/practice/experiment/index'),
          meta: {title:'实验练习',icon: 'documentation'}
        },
        {
          path: 'showExperiment/:id',
          name: '教师端查看实验练习',
          component: () => import('@/views/intro/practice/experiment/showExperiment'), //路由对应列表页面
          meta: { title: '教师端查看实验练习', noCache: true },
          hidden: true
        },
      ]
    },

    //计算机导论学习系统-每周总结
    {
      path: '/summary',
      component: Layout,
      redirect: '/summary/index', //默认地址
      name: '每周总结',
      meta: { title: '每周总结', icon: 'form' },
      children: [
        {
          path: 'index',
          name: '我的每周总结',
          component: () => import('@/views/intro/summary/index'),
          meta: {title:'我的每周总结',icon: 'clipboard'}
        },
        {
          path: 'publish',
          name: '发布本周总结',
          component: () => import('@/views/intro/summary/publish'),
          meta: {title:'发布本周总结',icon: 'edit'},
          hidden: cookie.get('intro_admin') !== "false"? false : true, //学生端隐藏
        },
        {
          path: 'showSummary/:id',
          name: '教师端查看每周总结',
          component: () => import('@/views/intro/summary/showSummary'), //路由对应列表页面
          meta: { title: '教师端查看每周总结', noCache: true },
          hidden: true
        },
      ]
    },

    //计算机导论学习系统-我的成就
    {
      path: '/achievement',
      component: Layout,
      redirect: '/achievement/index', //默认地址
      name: '我的成就',
      meta: { title: '我的成就', icon: 'chart' },
      hidden: cookie.get('intro_admin') === "false"? false : true, //教师端隐藏
      children: [
        {
          path: 'index',
          name: '我的成就',
          component: () => import('@/views/intro/achievement/index'),
          meta: {title:'我的成就',icon: 'chart'}
        },
        {
          path: 'summaryAchievement',
          name: '总结成就',
          component: () => import('@/views/intro/achievement/summaryAchievement'),
          meta: {title:'总结成就',icon: 'chart'}
        }
      ]
    },

    //计算机导论学习系统-学生管理
    {
      path: '/student',
      component: Layout,
      redirect: '/student/index', //默认地址
      name: '学生管理',
      meta: { title: '学生管理', icon: 'chart' },
      hidden: cookie.get('intro_admin') !== "false"? false : true, //学生端隐藏
      children: [
        {
          path: 'index',
          name: '学生列表',
          component: () => import('@/views/intro/student/index'),
          meta: {title:'学生管理',icon: 'chart'}
        },
      ]
    },

    //计算机导论学习系统-题目广场
    {
      path: '/topic',
      component: Layout, 
      redirect: '/topic/index',  //默认地址
      name: '题目广场',
      meta: { title: '题目广场', icon: 'table' },
      children: [
        {
          path: 'index',
          name: '题目广场',
          component: () => import('@/views/intro/topic/index'), //路由对应列表页面
          meta: { title: '题目广场', icon: 'table' }
        },
        {
          path: 'myTopic',
          name: '我的题目',
          component: () => import('@/views/intro/topic/myTopic'), //路由对应列表页面
          meta: { title: '我的题目', icon: 'table' }
        },
        {
          path: 'showTopic/:id',
          name: '查看题目',
          component: () => import('@/views/intro/topic/showTopic'), //路由对应列表页面
          meta: { title: '题目详情', noCache: true },
          hidden: true
        },
        {
          path: 'topicBank',
          name: '出题库',
          component: () => import('@/views/intro/topic/topicBank'), //路由对应列表页面
          meta: { title: '出题库', icon: 'table'},
          hidden: cookie.get('intro_admin') !== "false"? false : true, //学生端隐藏
        },
      ]
    },

    //计算机导论学习系统-知识点分类列表
    {
      path: '/knowledge',
      component: Layout, 
      redirect: '/knowledge/list',  //默认地址
      name: '知识点',
      meta: { title: '知识点', icon: 'tree' },
      children: [
        {
          path: 'list',
          name: '知识点分类列表',
          component: () => import('@/views/intro/knowledge/list'), //路由对应列表页面
          meta: { title: '知识点分类列表', icon: 'tree' }
        },
        {
          path: 'videoPlayer/:id',
          name: '查看视频',
          component: () => import('@/views/intro/knowledge/videoPlayer'), //路由对应列表页面
          meta: { title: '查看视频', noCache: true },
          hidden: true
        },
      ]
    },

    //计算机导论学习系统-个人信息
    {
      path: '/myself',
      component: Layout, 
      redirect: '/myself/index',  //默认地址
      name: '个人信息',
      meta: { title: '个人信息', icon: 'people' },
      children: [
        {
          path: 'index',
          name: '个人信息界面',
          component: () => import('@/views/intro/myself/index'), //路由对应列表页面
          meta: { title: '个人信息', icon: 'people' }
        },
      ]
    },

    //计算机导论学习系统-意见反馈
    {
      path: '/feedback',
      component: Layout, 
      redirect: '/feedback/index',  //默认地址
      name: '意见反馈',
      meta: { title: '意见反馈', icon: 'link' },
      children: [
        {
          path: 'index',
          name: '意见反馈界面',
          component: () => import('@/views/intro/feedback/index'), //路由对应列表页面
          meta: { title: '意见反馈', icon: 'link' }
        },
      ]
    },

    { path: '*', redirect: '/404', hidden: true }
]

export default new Router({
  mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  admin: cookie.get('intro_admin'),
  routes: constantRouterMap
  
})
