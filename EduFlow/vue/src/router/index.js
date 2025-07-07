import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

// Fix for NavigationDuplicated error in Vue Router >3.0
const originalPush = VueRouter.prototype.push
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

const routes = [
  {
    path: '/',
    name: 'Manager',
    component: () => import('../views/Manager.vue'),
    redirect: '/login',  // 修改：重定向到登录页
    children: [
      { path: '403', name: 'NoAuth', meta: { name: 'No Permission' }, component: () => import('../views/manager/403') },
      { path: 'home', name: 'Home', meta: { name: 'Dashboard' }, component: () => import('../views/manager/Home') },
      { path: 'admin', name: 'Admin', meta: { name: 'Administrator Info' }, component: () => import('../views/manager/Admin') },
      { path: 'adminPerson', name: 'AdminPerson', meta: { name: 'Profile' }, component: () => import('../views/manager/AdminPerson') },
      { path: 'password', name: 'Password', meta: { name: 'Change Password' }, component: () => import('../views/manager/Password') },
      { path: 'notice', name: 'Notice', meta: { name: 'Announcements' }, component: () => import('../views/manager/Notice') },
      { path: 'user', name: 'User', meta: { name: 'Member Info' }, component: () => import('../views/manager/User') },
      { path: 'coach', name: 'Coach', meta: { name: 'Coach Info' }, component: () => import('../views/manager/Coach') },
      { path: 'reserve', name: 'Reserve', meta: { name: 'Coach Reservations' }, component: () => import('../views/manager/Reserve') },
      { path: 'course', name: 'Course', meta: { name: 'Fitness Courses' }, component: () => import('../views/manager/Course') },
      { path: 'orders', name: 'Orders', meta: { name: 'Course Orders' }, component: () => import('../views/manager/Orders') },
      { path: 'equipment', name: 'Equipment', meta: { name: 'Equipment' }, component: () => import('../views/manager/Equipment') },
      { path: 'eqReserve', name: 'EqReserve', meta: { name: 'Equipment Reservations' }, component: () => import('../views/manager/EqReserve') },
      { path: 'menu', name: 'Menu', meta: { name: 'Meal Plans' }, component: () => import('../views/manager/Menu') },
      { path: 'experience', name: 'Experience', meta: { name: 'Fitness Experience' }, component: () => import('../views/manager/Experience') },
    ]
  },
  {
    path: '/front',
    name: 'Front',
    component: () => import('../views/Front.vue'),
    children: [
      { path: 'home', name: 'FrontHome', meta: { name: 'Home' }, component: () => import('../views/front/Home') },
      { path: 'person', name: 'Person', meta: { name: 'Profile' }, component: () => import('../views/front/Person') },
      { path: 'coachPerson', name: 'CoachPerson', meta: { name: 'Coach Profile' }, component: () => import('../views/front/CoachPerson') },
      { path: 'coach', name: 'FrontCoach', meta: { name: 'Top Coaches' }, component: () => import('../views/front/Coach') },
      { path: 'coachDetail', name: 'CoachDetail', meta: { name: 'Coach Details' }, component: () => import('../views/front/CoachDetail') },
      { path: 'reserve', name: 'FrontReserve', meta: { name: 'My Reservations' }, component: () => import('../views/front/Reserve') },
      { path: 'course', name: 'FrontCourse', meta: { name: 'Fitness Courses' }, component: () => import('../views/front/Course') },
      { path: 'courseDetail', name: 'CourseDetail', meta: { name: 'Course Details' }, component: () => import('../views/front/CourseDetail') },
      { path: 'orders', name: 'FrontOrders', meta: { name: 'Course Orders' }, component: () => import('../views/front/Orders') },
      { path: 'equipment', name: 'FrontEquipment', meta: { name: 'Equipment' }, component: () => import('../views/front/Equipment') },
      { path: 'eqReserve', name: 'FrontEqReserve', meta: { name: 'Equipment Reservations' }, component: () => import('../views/front/EqReserve') },
      { path: 'menu', name: 'FrontMenu', meta: { name: 'Meal Plans' }, component: () => import('../views/front/Menu') },
      { path: 'myExperience', name: 'MyExperience', meta: { name: 'My Experience' }, component: () => import('../views/front/MyExperience') },
      { path: 'experience', name: 'FrontExperience', meta: { name: 'Experience Sharing' }, component: () => import('../views/front/Experience') },
      { path: 'experienceDetail', name: 'ExperienceDetail', meta: { name: 'Experience Details' }, component: () => import('../views/front/ExperienceDetail') },
    ]
  },
  { path: '/login', name: 'Login', meta: { name: 'Login' }, component: () => import('../views/Login.vue') },
  { path: '/register', name: 'Register', meta: { name: 'Register' }, component: () => import('../views/Register.vue') },
  { path: '*', name: 'NotFound', meta: { name: 'Page Not Found' }, component: () => import('../views/404.vue') },
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 可选：启用路由守卫（如果需要的话）
// router.beforeEach((to, from, next) => {
//   let user = JSON.parse(localStorage.getItem("xm-user") || '{}');
//   if (to.path === '/') {
//     if (user.role) {
//       if (user.role === 'USER') {
//         next('/front/home')
//       } else {
//         next('/home')
//       }
//     } else {
//       next('/login')
//     }
//   } else {
//     next()
//   }
// })

export default router