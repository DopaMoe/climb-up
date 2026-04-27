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
  <div class="min-h-screen bg-gray-50">
    <header class="bg-white shadow-sm">
      <div class="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
        <h1 class="text-xl font-bold text-gray-900">🧗 ClimbUp</h1>
        <nav class="flex items-center gap-6">
          <RouterLink to="/member/dashboard"
            class="text-gray-600 hover:text-blue-600 font-medium transition-colors"
            active-class="text-blue-600">
            My Membership
          </RouterLink>
          <RouterLink to="/member/memberships"
            class="text-gray-600 hover:text-blue-600 font-medium transition-colors"
            active-class="text-blue-600">
            Browse Plans
          </RouterLink>
          <button @click="logout"
            class="text-gray-500 hover:text-red-600 font-medium transition-colors">
            Logout
          </button>
        </nav>
      </div>
    </header>
    <main class="max-w-4xl mx-auto px-4 py-8">
      <RouterView />
    </main>
  </div>
</template>
