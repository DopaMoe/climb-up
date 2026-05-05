<script setup lang="ts">
import { ref } from 'vue'
import { getMembers, checkIn } from '../../api/admin'
import { Search, UserCheck, CheckCircle, XCircle } from 'lucide-vue-next'

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
  } finally { searching.value = false }
}

async function handleCheckIn(userId: number, name: string) {
  checkingIn.value = userId
  feedback.value = null
  try {
    await checkIn(userId)
    feedback.value = { type: 'success', msg: `${name} checked in successfully!` }
  } catch (e: any) {
    feedback.value = { type: 'error', msg: e.response?.data?.message || 'Check-in failed' }
  } finally { checkingIn.value = null }
}
</script>

<template>
  <div class="p-6 lg:p-8 max-w-2xl">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight">Check In Member</h1>
      <p class="text-sm text-slate-500 mt-1">Search for a member and record their gym visit.</p>
    </div>

    <div class="flex gap-3 mb-4">
      <div class="relative flex-1">
        <Search :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
        <input v-model="search" type="text" placeholder="Search by name or email..."
          class="w-full pl-9 pr-4 py-2.5 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
          @keyup.enter="doSearch" />
      </div>
      <button @click="doSearch" :disabled="searching"
        class="inline-flex items-center gap-2 bg-sky-500 hover:bg-sky-600 disabled:opacity-50 text-white px-5 py-2.5 rounded-lg text-sm font-semibold transition-colors">
        {{ searching ? 'Searching...' : 'Search' }}
      </button>
    </div>

    <div v-if="feedback"
      :class="['mb-4 px-4 py-3 rounded-xl text-sm font-medium flex items-center gap-2', feedback.type === 'success' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' : 'bg-rose-50 text-rose-700 border border-rose-200']">
      <CheckCircle v-if="feedback.type === 'success'" :size="15" class="text-emerald-500 flex-shrink-0" />
      <XCircle v-else :size="15" class="text-rose-500 flex-shrink-0" />
      {{ feedback.msg }}
    </div>

    <div class="space-y-2">
      <div v-for="m in results" :key="m.id"
        class="bg-white rounded-xl border border-slate-200 shadow-sm p-4 flex items-center justify-between hover:border-slate-300 transition-colors">
        <div>
          <p class="font-semibold text-slate-900 text-sm">{{ m.name }}</p>
          <p class="text-xs text-slate-500 mt-0.5">{{ m.email }} · <span class="font-mono">#{{ m.id }}</span></p>
        </div>
        <button @click="handleCheckIn(m.id, m.name)" :disabled="checkingIn === m.id"
          class="inline-flex items-center gap-2 bg-emerald-500 hover:bg-emerald-600 disabled:opacity-50 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors">
          <UserCheck :size="14" /> {{ checkingIn === m.id ? 'Checking in...' : 'Check In' }}
        </button>
      </div>
      <div v-if="results.length === 0 && !searching && search" class="text-center py-10 text-slate-400 text-sm">
        No members found for "{{ search }}".
      </div>
    </div>
  </div>
</template>
