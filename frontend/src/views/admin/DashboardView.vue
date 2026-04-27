<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboard } from '../../api/admin'
import dayjs from 'dayjs'

const stats = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    stats.value = await getDashboard()
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="p-8">
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Dashboard</h2>

    <div v-if="loading" class="text-gray-500">Loading...</div>

    <div v-else-if="stats">
      <!-- Stat cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <p class="text-gray-500 text-sm">Total Members</p>
          <p class="text-3xl font-bold text-gray-900 mt-1">{{ stats.totalMembers }}</p>
        </div>
        <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <p class="text-gray-500 text-sm">Active Memberships</p>
          <p class="text-3xl font-bold text-green-600 mt-1">{{ stats.activeMemberships }}</p>
        </div>
        <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <p class="text-gray-500 text-sm">Pending Activation</p>
          <p class="text-3xl font-bold text-yellow-600 mt-1">{{ stats.pendingMemberships }}</p>
        </div>
        <div class="bg-white rounded-xl p-5 shadow-sm border border-gray-100">
          <p class="text-gray-500 text-sm">Check-ins Today</p>
          <p class="text-3xl font-bold text-blue-600 mt-1">{{ stats.checkInsToday }}</p>
        </div>
      </div>

      <!-- Recent check-ins -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100">
        <div class="p-5 border-b border-gray-100">
          <h3 class="font-semibold text-gray-900">Recent Check-ins</h3>
        </div>
        <div v-if="stats.recentCheckIns.length === 0" class="p-5 text-gray-500 text-sm">
          No check-ins yet today.
        </div>
        <div v-else class="divide-y divide-gray-50">
          <div v-for="c in stats.recentCheckIns" :key="c.id"
            class="flex items-center justify-between px-5 py-3">
            <div>
              <p class="font-medium text-gray-900">{{ c.userName }}</p>
              <p class="text-sm text-gray-500">{{ c.membershipType }}</p>
            </div>
            <p class="text-sm text-gray-400">{{ dayjs(c.checkedInAt).format('HH:mm') }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
