<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMembers } from '../api/admin'

const router = useRouter()
const auth = useAuthStore()
const memberIdInput = ref('')
const memberError = ref('')
const loading = ref(false)

async function enterAsAdmin() {
  auth.enterAs('ADMIN')
  router.push('/admin/dashboard')
}

async function enterAsMember() {
  memberError.value = ''
  const id = Number(memberIdInput.value)
  if (!id) {
    memberError.value = 'Please enter your member ID'
    return
  }
  loading.value = true
  try {
    await getMembers(undefined, 0)
    auth.enterAs('MEMBER', id)
    router.push('/member/dashboard')
  } catch {
    memberError.value = 'Member not found. Please check your ID.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-blue-900 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">
      <div class="text-center mb-8">
        <div class="text-5xl mb-3">🧗</div>
        <h1 class="text-3xl font-bold text-gray-900">ClimbUp</h1>
        <p class="text-gray-500 mt-2">Gym Membership Manager</p>
      </div>

      <div class="space-y-4">
        <p class="text-center text-sm font-medium text-gray-600 uppercase tracking-wide">Enter as</p>

        <!-- Admin -->
        <button @click="enterAsAdmin"
          class="w-full flex items-center justify-center gap-3 bg-gray-900 text-white py-4 rounded-xl font-semibold text-lg hover:bg-gray-800 transition-colors shadow-lg">
          <span class="text-2xl">🔐</span>
          Admin
        </button>

        <!-- Member -->
        <div class="border-2 border-blue-100 rounded-xl p-4">
          <button disabled
            class="w-full flex items-center justify-center gap-3 bg-blue-600 text-white py-4 rounded-xl font-semibold text-lg hover:bg-blue-700 transition-colors shadow-lg mb-3">
            <span class="text-2xl">🏅</span>
            Member
          </button>
          <div class="space-y-2">
            <input v-model="memberIdInput" type="number" placeholder="Enter your Member ID"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              @keyup.enter="enterAsMember" />
            <p v-if="memberError" class="text-red-500 text-xs">{{ memberError }}</p>
            <button @click="enterAsMember" :disabled="loading"
              class="w-full bg-blue-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors">
              {{ loading ? 'Loading...' : 'Continue as Member' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
