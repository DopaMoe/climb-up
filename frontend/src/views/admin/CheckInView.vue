<script setup lang="ts">
import { ref } from 'vue'
import { getMembers, checkIn } from '../../api/admin'

const search = ref('')
const results = ref<any[]>([])
const searching = ref(false)
const feedback = ref<{ type: 'success' | 'error'; msg: string } | null>(null)
const checkingIn = ref<number | null>(null)

async function doSearch() {
  if (!search.value.trim()) return
  searching.value = true
  feedback.value = null
  try {
    const data = await getMembers(search.value, 0)
    results.value = data.content
  } finally {
    searching.value = false
  }
}

async function handleCheckIn(userId: number, name: string) {
  checkingIn.value = userId
  feedback.value = null
  try {
    await checkIn(userId)
    feedback.value = { type: 'success', msg: `✅ ${name} checked in successfully!` }
  } catch (e: any) {
    feedback.value = { type: 'error', msg: e.response?.data?.message || 'Check-in failed' }
  } finally {
    checkingIn.value = null
  }
}
</script>

<template>
  <div class="p-8 max-w-2xl">
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Check In Member</h2>

    <div class="flex gap-3 mb-4">
      <input v-model="search" type="text" placeholder="Search by name or email..."
        class="flex-1 border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        @keyup.enter="doSearch" />
      <button @click="doSearch" :disabled="searching"
        class="bg-blue-600 text-white px-5 py-2 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50">
        {{ searching ? 'Searching...' : 'Search' }}
      </button>
    </div>

    <div v-if="feedback"
      :class="['mb-4 px-4 py-3 rounded-lg text-sm font-medium', feedback.type === 'success' ? 'bg-green-50 text-green-700 border border-green-200' : 'bg-red-50 text-red-700 border border-red-200']">
      {{ feedback.msg }}
    </div>

    <div class="space-y-3">
      <div v-for="m in results" :key="m.id"
        class="bg-white rounded-xl border border-gray-100 shadow-sm p-4 flex items-center justify-between">
        <div>
          <p class="font-medium text-gray-900">{{ m.name }}</p>
          <p class="text-sm text-gray-500">{{ m.email }} · #{{ m.id }}</p>
        </div>
        <button @click="handleCheckIn(m.id, m.name)"
          :disabled="checkingIn === m.id"
          class="bg-green-500 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-green-600 disabled:opacity-50 transition-colors">
          {{ checkingIn === m.id ? 'Checking in...' : 'Check In' }}
        </button>
      </div>
      <p v-if="results.length === 0 && !searching && search" class="text-gray-400 text-sm text-center py-4">
        No members found.
      </p>
    </div>
  </div>
</template>
