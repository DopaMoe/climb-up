<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { Mountain, LayoutGrid, Users, CreditCard, CheckSquare, Tag, Clock, LogOut } from 'lucide-vue-next'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen flex bg-slate-50">
    <aside class="w-60 bg-slate-900 flex flex-col fixed inset-y-0 left-0 z-10">
      <div class="px-5 py-5 border-b border-slate-800">
        <div class="flex items-center gap-3">
          <div class="w-8 h-8 bg-gradient-to-br from-sky-400 to-sky-600 rounded-lg flex items-center justify-center shadow-lg shadow-sky-500/20 flex-shrink-0">
            <Mountain :size="15" class="text-white" :stroke-width="2.5" />
          </div>
          <div>
            <p class="text-sm font-bold text-slate-100 leading-none">ClimbUp</p>
            <p class="text-xs text-slate-500 mt-0.5 font-medium">Admin Panel</p>
          </div>
        </div>
      </div>

      <nav class="flex-1 px-3 py-4 space-y-0.5">
        <RouterLink to="/admin/dashboard"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition-colors text-sm font-medium"
          active-class="bg-slate-800 !text-sky-400 border-l-2 border-sky-500 !pl-[10px]">
          <LayoutGrid :size="16" class="flex-shrink-0" />
          Dashboard
        </RouterLink>
        <RouterLink to="/admin/members"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition-colors text-sm font-medium"
          active-class="bg-slate-800 !text-sky-400 border-l-2 border-sky-500 !pl-[10px]">
          <Users :size="16" class="flex-shrink-0" />
          Members
        </RouterLink>
        <RouterLink to="/admin/membership-types"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition-colors text-sm font-medium"
          active-class="bg-slate-800 !text-sky-400 border-l-2 border-sky-500 !pl-[10px]">
          <CreditCard :size="16" class="flex-shrink-0" />
          Membership Types
        </RouterLink>
        <RouterLink to="/admin/check-in"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition-colors text-sm font-medium"
          active-class="bg-slate-800 !text-sky-400 border-l-2 border-sky-500 !pl-[10px]">
          <CheckSquare :size="16" class="flex-shrink-0" />
          Check In
        </RouterLink>
        <RouterLink to="/admin/discounts"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition-colors text-sm font-medium"
          active-class="bg-slate-800 !text-sky-400 border-l-2 border-sky-500 !pl-[10px]">
          <Tag :size="16" class="flex-shrink-0" />
          Discounts
        </RouterLink>
        <RouterLink to="/admin/pending-activations"
          class="flex items-center gap-3 px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition-colors text-sm font-medium"
          active-class="bg-slate-800 !text-sky-400 border-l-2 border-sky-500 !pl-[10px]">
          <Clock :size="16" class="flex-shrink-0" />
          Pending
        </RouterLink>
      </nav>

      <div class="px-3 py-4 border-t border-slate-800">
        <button @click="logout"
          class="flex items-center gap-3 w-full px-3 py-2.5 rounded-lg text-slate-400 hover:bg-slate-800 hover:text-rose-400 transition-colors text-sm font-medium">
          <LogOut :size="16" class="flex-shrink-0" />
          Logout
        </button>
      </div>
    </aside>

    <main class="flex-1 ml-60 min-h-screen">
      <RouterView />
    </main>
  </div>
</template>
