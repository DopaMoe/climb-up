<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="min-h-screen flex bg-gray-100">
    <!-- Sidebar -->
    <aside class="w-64 bg-gray-900 text-white flex flex-col">
      <div class="p-6 border-b border-gray-700">
        <h1 class="text-xl font-bold text-white">🧗 ClimbUp</h1>
        <p class="text-gray-400 text-sm mt-1">Admin Panel</p>
      </div>
      <nav class="flex-1 p-4 space-y-1">
        <RouterLink to="/admin/dashboard"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
          active-class="bg-blue-600 text-white">
          <span>📊</span> Dashboard
        </RouterLink>
        <RouterLink to="/admin/members"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
          active-class="bg-blue-600 text-white">
          <span>👥</span> Members
        </RouterLink>
        <RouterLink to="/admin/membership-types"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
          active-class="bg-blue-600 text-white">
          <span>🎫</span> Membership Types
        </RouterLink>
        <RouterLink to="/admin/check-in"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
          active-class="bg-blue-600 text-white">
          <span>✅</span> Check In
        </RouterLink>
        <RouterLink to="/admin/discounts"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
          active-class="bg-blue-600 text-white">
          <span>🏷️</span> Discounts
        </RouterLink>
        <RouterLink to="/admin/pending-activations"
          class="flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-gray-700 hover:text-white transition-colors"
          active-class="bg-blue-600 text-white">
          <span>⏳</span> Pending Activations
        </RouterLink>
      </nav>
      <div class="p-4 border-t border-gray-700">
        <button @click="logout"
          class="w-full flex items-center gap-3 px-3 py-2 rounded-lg text-gray-300 hover:bg-red-700 hover:text-white transition-colors">
          <span>🚪</span> Logout
        </button>
      </div>
    </aside>

    <!-- Main content -->
    <main class="flex-1 overflow-auto">
      <RouterView />
    </main>
  </div>
</template>
