<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { getMembershipTypes, requestMembership } from '../../api/member'

const auth = useAuthStore()
const types = ref<any[]>([])
const loading = ref(true)

const selected = ref<any>(null)
const startDate = ref(today())
const requesting = ref(false)
const reqError = ref('')
const reqSuccess = ref('')

function today() { return new Date().toISOString().split('T')[0] }

onMounted(async () => {
  try { types.value = await getMembershipTypes() }
  finally { loading.value = false }
})

function typeDesc(t: any) {
  if (t.durationDays) return `${t.durationDays} days access`
  if (t.entriesCount) return `${t.entriesCount} ${t.entriesCount === 1 ? 'entry' : 'entries'}${t.entriesValidityDays ? ` valid for ${t.entriesValidityDays} days` : ''}`
  return ''
}

function computedEnd(t: any, start: string) {
  if (!start) return null
  const d = new Date(start)
  if (t.durationDays) {
    d.setDate(d.getDate() + t.durationDays)
    return d.toISOString().split('T')[0]
  }
  if (t.entriesValidityDays) {
    d.setDate(d.getDate() + t.entriesValidityDays)
    return d.toISOString().split('T')[0]
  }
  return null
}

async function request() {
  if (!auth.userId) { reqError.value = 'No member ID. Please log in again.'; return }
  reqError.value = ''
  reqSuccess.value = ''
  requesting.value = true
  try {
    await requestMembership(auth.userId, {
      membershipTypeId: selected.value.id,
      startDate: startDate.value,
      directActivate: false,
    })
    reqSuccess.value = `Request submitted for "${selected.value.name}". Visit the gym to activate after payment.`
    selected.value = null
  } catch (e: any) {
    reqError.value = e.response?.data?.message || 'Request failed'
  } finally {
    requesting.value = false
  }
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Membership Plans</h2>

    <div v-if="reqSuccess" class="mb-6 bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-xl text-sm">
      {{ reqSuccess }}
    </div>

    <div v-if="loading" class="text-gray-400">Loading...</div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div v-for="t in types" :key="t.id"
        class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 hover:shadow-md transition-shadow">
        <h3 class="font-bold text-gray-900 text-lg mb-1">{{ t.name }}</h3>
        <p class="text-gray-500 text-sm mb-4">{{ t.description }}</p>

        <div class="flex items-end justify-between mb-4">
          <div>
            <p class="text-2xl font-bold text-blue-600">{{ t.effectivePrice }} SAR</p>
            <p v-if="t.effectivePrice !== t.basePrice" class="text-sm text-gray-400 line-through">{{ t.basePrice }} SAR</p>
          </div>
          <p class="text-sm text-gray-500">{{ typeDesc(t) }}</p>
        </div>

        <button @click="selected = t; startDate = today(); reqError = ''"
          class="w-full bg-blue-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
          Request This Plan
        </button>
      </div>
    </div>

    <!-- Request modal -->
    <div v-if="selected" class="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <h3 class="font-bold text-gray-900 text-lg mb-4">Request: {{ selected.name }}</h3>

        <div class="bg-blue-50 rounded-lg p-4 mb-5 text-sm">
          <p class="font-medium text-blue-900 mb-1">Price: {{ selected.effectivePrice }} SAR</p>
          <p class="text-blue-700">{{ typeDesc(selected) }}</p>
        </div>

        <div class="mb-4">
          <label class="text-sm font-medium text-gray-700 block mb-1">Start Date</label>
          <input v-model="startDate" type="date" :min="today()"
            class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          <p v-if="computedEnd(selected, startDate)" class="text-xs text-gray-500 mt-1">
            <template v-if="selected.durationDays">Expires: {{ computedEnd(selected, startDate) }}</template>
            <template v-else-if="selected.entriesValidityDays">Pack valid until: {{ computedEnd(selected, startDate) }}</template>
          </p>
        </div>

        <div class="bg-yellow-50 border border-yellow-100 rounded-lg px-3 py-2 text-xs text-yellow-800 mb-4">
          Your request will be saved as Pending. Visit the gym and pay in person to activate.
        </div>

        <p v-if="reqError" class="text-red-500 text-sm mb-3">{{ reqError }}</p>

        <div class="flex gap-3">
          <button @click="request" :disabled="requesting"
            class="flex-1 bg-blue-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50">
            {{ requesting ? 'Submitting...' : 'Submit Request' }}
          </button>
          <button @click="selected = null"
            class="flex-1 bg-gray-100 text-gray-700 py-2 rounded-lg text-sm font-medium hover:bg-gray-200">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
