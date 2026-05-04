<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { getMe } from '../../api/member'

const auth = useAuthStore()
const data = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  if (auth.userId) {
    try {
      data.value = await getMe(auth.userId)
    } catch {}
  }
  loading.value = false
})

function statusColor(s: string) {
  return { ACTIVE: 'bg-green-100 text-green-700', PENDING: 'bg-yellow-100 text-yellow-700',
    EXPIRED: 'bg-gray-100 text-gray-500', CANCELLED: 'bg-red-100 text-red-600' }[s] || ''
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold text-gray-900 mb-6">My Membership</h2>

    <div v-if="loading" class="text-gray-400">Loading...</div>

    <div v-else-if="!auth.userId" class="bg-yellow-50 border border-yellow-200 rounded-xl p-6 text-center">
      <p class="text-yellow-800 font-medium">No member ID set. Please log in again.</p>
      <RouterLink to="/login" class="text-blue-600 hover:underline text-sm mt-2 block">Back to login</RouterLink>
    </div>

    <div v-else-if="data">
      <!-- Profile -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
        <h3 class="font-semibold text-gray-900 mb-3">Profile</h3>
        <div class="grid grid-cols-2 gap-3 text-sm">
          <div><span class="text-gray-500">Name:</span> <span class="font-medium ml-2">{{ data.user.name }}</span></div>
          <div><span class="text-gray-500">Email:</span> <span class="ml-2">{{ data.user.email }}</span></div>
          <div><span class="text-gray-500">Phone:</span> <span class="ml-2">{{ data.user.phone || '-' }}</span></div>
          <div><span class="text-gray-500">Member ID:</span> <span class="font-mono ml-2">#{{ data.user.id }}</span></div>
        </div>
      </div>

      <!-- Memberships -->
      <div class="space-y-4">
        <div v-if="data.memberships.length === 0" class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 text-center">
          <p class="text-gray-400 mb-4">No memberships yet.</p>
          <RouterLink to="/member/memberships"
            class="bg-blue-600 text-white px-5 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
            Browse Membership Plans
          </RouterLink>
        </div>

        <div v-for="m in data.memberships" :key="m.id"
          class="bg-white rounded-xl shadow-sm border border-gray-100 p-6">
          <div class="flex items-start justify-between mb-3">
            <div>
              <h4 class="font-semibold text-gray-900 text-lg">{{ m.membershipType.name }}</h4>
              <span :class="['text-xs px-2 py-0.5 rounded-full font-medium mt-1 inline-block', statusColor(m.status)]">
                {{ m.status }}
              </span>
            </div>
            <p class="text-gray-500 text-sm">{{ m.pricePaid }} SAR</p>
          </div>

          <div class="grid grid-cols-2 gap-3 text-sm">
            <div><span class="text-gray-500">Start:</span> <span class="ml-2">{{ m.startDate ?? 'To be confirmed' }}</span></div>
            <div v-if="m.endDate"><span class="text-gray-500">Expires:</span> <span class="ml-2">{{ m.endDate }}</span></div>
            <div v-if="m.entriesRemaining != null">
              <span class="text-gray-500">Entries left:</span>
              <span class="ml-2 font-semibold text-blue-600">{{ m.entriesRemaining }}</span>
            </div>
            <div v-if="m.entriesExpiryDate">
              <span class="text-gray-500">Pack expires:</span>
              <span class="ml-2">{{ m.entriesExpiryDate }}</span>
            </div>
          </div>

          <div v-if="m.status === 'PENDING'" class="mt-3 bg-yellow-50 border border-yellow-200 rounded-lg px-3 py-2 text-sm text-yellow-800">
            Pending activation — please visit the gym to complete your payment.
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
