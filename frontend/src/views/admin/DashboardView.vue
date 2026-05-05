<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '../../api/admin'
import { Users, TrendingUp, Clock, CheckSquare } from 'lucide-vue-next'
import dayjs from 'dayjs'

const stats = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  try { stats.value = await getDashboard() }
  finally { loading.value = false }
})
</script>

<template>
  <div class="p-6 lg:p-8">
    <div class="mb-8">
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight">Dashboard</h1>
      <p class="text-sm text-slate-500 mt-1">{{ dayjs().format('dddd, D MMMM YYYY') }}</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else-if="stats">
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-white rounded-xl border border-slate-200 border-t-2 border-t-sky-500 p-5 shadow-sm">
          <div class="flex items-center justify-between mb-3">
            <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Total Members</p>
            <Users :size="15" class="text-slate-400" />
          </div>
          <p class="text-3xl font-black text-slate-900">{{ stats.totalMembers }}</p>
        </div>
        <div class="bg-white rounded-xl border border-slate-200 border-t-2 border-t-emerald-500 p-5 shadow-sm">
          <div class="flex items-center justify-between mb-3">
            <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Active</p>
            <TrendingUp :size="15" class="text-emerald-400" />
          </div>
          <p class="text-3xl font-black text-emerald-600">{{ stats.activeMemberships }}</p>
        </div>
        <div class="bg-white rounded-xl border border-slate-200 border-t-2 border-t-amber-500 p-5 shadow-sm">
          <div class="flex items-center justify-between mb-3">
            <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Pending</p>
            <Clock :size="15" class="text-amber-400" />
          </div>
          <p class="text-3xl font-black text-amber-600">{{ stats.pendingMemberships }}</p>
        </div>
        <div class="bg-white rounded-xl border border-slate-200 border-t-2 border-t-violet-500 p-5 shadow-sm">
          <div class="flex items-center justify-between mb-3">
            <p class="text-xs font-semibold text-slate-500 uppercase tracking-wide">Check-ins Today</p>
            <CheckSquare :size="15" class="text-violet-400" />
          </div>
          <p class="text-3xl font-black text-violet-600">{{ stats.checkInsToday }}</p>
        </div>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 shadow-sm">
        <div class="px-6 py-4 border-b border-slate-100">
          <h3 class="text-sm font-semibold text-slate-900">Recent Check-ins</h3>
        </div>
        <div v-if="stats.recentCheckIns.length === 0" class="flex flex-col items-center justify-center py-12 text-slate-400">
          <CheckSquare :size="28" class="mb-2 opacity-40" />
          <p class="text-sm">No check-ins yet today.</p>
        </div>
        <div v-else class="divide-y divide-slate-50">
          <div v-for="c in stats.recentCheckIns" :key="c.id"
            class="flex items-center justify-between px-6 py-3.5">
            <div>
              <p class="text-sm font-semibold text-slate-900">{{ c.userName }}</p>
              <p class="text-xs text-slate-500 mt-0.5">{{ c.membershipType }}</p>
            </div>
            <p class="text-xs font-medium text-slate-400 tabular-nums">{{ dayjs(c.checkedInAt).format('HH:mm') }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
