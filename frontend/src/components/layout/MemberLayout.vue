<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { Mountain, LogOut } from 'lucide-vue-next'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen bg-slate-50">
    <header class="bg-slate-900 border-b border-slate-800 sticky top-0 z-10">
      <div class="max-w-5xl mx-auto px-6 h-14 flex items-center justify-between">
        <div class="flex items-center gap-3">
          <div class="w-7 h-7 bg-gradient-to-br from-sky-400 to-sky-600 rounded-lg flex items-center justify-center flex-shrink-0">
            <Mountain :size="13" class="text-white" :stroke-width="2.5" />
          </div>
          <span class="text-sm font-bold text-slate-100 tracking-tight">ClimbUp</span>
        </div>
        <nav class="flex items-center gap-1">
          <RouterLink to="/member/dashboard"
            class="px-4 py-2 rounded-lg text-sm font-medium text-slate-400 hover:text-slate-100 hover:bg-slate-800 transition-colors"
            active-class="!text-sky-400 bg-slate-800">
            My Membership
          </RouterLink>
          <RouterLink to="/member/memberships"
            class="px-4 py-2 rounded-lg text-sm font-medium text-slate-400 hover:text-slate-100 hover:bg-slate-800 transition-colors"
            active-class="!text-sky-400 bg-slate-800">
            Browse Plans
          </RouterLink>
          <button @click="logout"
            class="ml-2 flex items-center gap-1.5 px-3 py-2 rounded-lg text-sm font-medium text-slate-400 hover:text-rose-400 hover:bg-slate-800 transition-colors">
            <LogOut :size="14" />
            Logout
          </button>
        </nav>
      </div>
    </header>
    <main class="max-w-5xl mx-auto px-6 py-8">
      <RouterView />
    </main>
  </div>
</template>
