import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: () => {
      const auth = useAuthStore()
      if (auth.isAdmin) return '/admin/dashboard'
      if (auth.isMember) return '/member/dashboard'
      return '/login'
    }},
    { path: '/login', component: () => import('../views/LoginView.vue') },
    {
      path: '/admin',
      component: () => import('../components/layout/AdminLayout.vue'),
      meta: { requiresRole: 'ADMIN' },
      children: [
        { path: 'dashboard', component: () => import('../views/admin/DashboardView.vue') },
        { path: 'members', component: () => import('../views/admin/MembersView.vue') },
        { path: 'members/new', component: () => import('../views/admin/MemberNewView.vue') },
        { path: 'members/:id', component: () => import('../views/admin/MemberDetailView.vue') },
        { path: 'membership-types', component: () => import('../views/admin/MembershipTypesView.vue') },
        { path: 'membership-types/new', component: () => import('../views/admin/MembershipTypeEditView.vue') },
        { path: 'membership-types/:id', component: () => import('../views/admin/MembershipTypeEditView.vue') },
        { path: 'check-in', component: () => import('../views/admin/CheckInView.vue') },
        { path: 'discounts', component: () => import('../views/admin/DiscountsView.vue') },
      ],
    },
    {
      path: '/member',
      component: () => import('../components/layout/MemberLayout.vue'),
      meta: { requiresRole: 'MEMBER' },
      children: [
        { path: 'dashboard', component: () => import('../views/member/DashboardView.vue') },
        { path: 'memberships', component: () => import('../views/member/MembershipsView.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  const requiredRole = to.meta.requiresRole as string | undefined
  if (requiredRole && auth.role !== requiredRole) {
    return '/login'
  }
})

export default router
