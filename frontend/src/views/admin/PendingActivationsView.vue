<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPendingMemberships, activateMembership } from '../../api/admin'

const rows = ref<any[]>([])
const searchId = ref('')
const modal = ref<{ membershipId: number; memberName: string; planName: string } | null>(null)
const startDate = ref('')
const activating = ref(false)
const activateError = ref('')

async function load(memberId?: number) {
  rows.value = await getPendingMemberships(memberId)
}

onMounted(() => load())

function search() {
  const id = Number(searchId.value)
  load(id || undefined)
}

function openModal(row: any) {
  modal.value = { membershipId: row.membershipId, memberName: row.memberName, planName: row.membershipTypeName }
  startDate.value = ''
  activateError.value = ''
}

async function confirmActivation() {
  if (!modal.value || !startDate.value) return
  activating.value = true
  activateError.value = ''
  try {
    await activateMembership(modal.value.membershipId, startDate.value)
    rows.value = rows.value.filter(r => r.membershipId !== modal.value!.membershipId)
    modal.value = null
  } catch (e: any) {
    activateError.value = e.response?.data?.message ?? 'Activation failed.'
  } finally {
    activating.value = false
  }
}
</script>

<template>
  <div class="p-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">Pending Activations</h1>

    <!-- Search -->
    <div class="flex gap-3 mb-6">
      <input v-model="searchId" type="number" placeholder="Search by Member ID"
        class="border border-gray-300 rounded-lg px-4 py-2 text-sm w-56 focus:outline-none focus:ring-2 focus:ring-blue-500"
        @keyup.enter="search" />
      <button @click="search"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-blue-700 transition-colors">
        Search
      </button>
      <button v-if="searchId" @click="searchId = ''; load()"
        class="text-sm text-gray-500 hover:text-gray-700 underline">
        Clear
      </button>
    </div>

    <!-- Table -->
    <div v-if="rows.length" class="bg-white rounded-xl shadow overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 border-b">
          <tr>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Member ID</th>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Name</th>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Plan</th>
            <th class="text-left px-6 py-3 font-semibold text-gray-600">Requested</th>
            <th class="px-6 py-3"></th>
          </tr>
        </thead>
        <tbody class="divide-y">
          <tr v-for="row in rows" :key="row.membershipId" class="hover:bg-gray-50">
            <td class="px-6 py-4 font-mono text-blue-600">#{{ row.userId }}</td>
            <td class="px-6 py-4 font-medium text-gray-900">{{ row.memberName }}</td>
            <td class="px-6 py-4 text-gray-600">{{ row.membershipTypeName }}</td>
            <td class="px-6 py-4 text-gray-400 text-xs">{{ new Date(row.createdAt).toLocaleDateString() }}</td>
            <td class="px-6 py-4 text-right">
              <button @click="openModal(row)"
                class="bg-green-600 text-white px-3 py-1.5 rounded-lg text-xs font-medium hover:bg-green-700 transition-colors">
                Activate
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="text-center py-16 text-gray-400">
      <p class="text-4xl mb-3">✅</p>
      <p class="font-medium">No pending activations</p>
    </div>

    <!-- Activation modal -->
    <div v-if="modal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-sm">
        <h2 class="text-lg font-bold text-gray-900 mb-1">Activate Membership</h2>
        <p class="text-sm text-gray-500 mb-5">
          {{ modal.memberName }} — <em>{{ modal.planName }}</em>
        </p>
        <div class="mb-4">
          <label class="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
          <input v-model="startDate" type="date" required
            class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
        </div>
        <p v-if="activateError" class="text-red-500 text-sm mb-3">{{ activateError }}</p>
        <div class="flex gap-3">
          <button @click="modal = null"
            class="flex-1 border border-gray-300 text-gray-700 py-2 rounded-lg text-sm hover:bg-gray-50 transition-colors">
            Cancel
          </button>
          <button @click="confirmActivation" :disabled="!startDate || activating"
            class="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-green-700 disabled:opacity-50 transition-colors">
            {{ activating ? 'Activating...' : 'Confirm' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
