<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getPendingMemberships, activateMembership } from '../../api/admin'
import { Search, CheckCircle, X } from 'lucide-vue-next'

const rows = ref<any[]>([])
const searchId = ref('')
const modal = ref<{ membershipId: number; memberName: string; planName: string } | null>(null)
const startDate = ref('')
const activating = ref(false)
const activateError = ref('')

async function load(memberId?: number) { rows.value = await getPendingMemberships(memberId) }

onMounted(() => load())

function search() { const id = Number(searchId.value); load(id || undefined) }

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
  } finally { activating.value = false }
}
</script>

<template>
  <div class="p-6 lg:p-8">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight">Pending Activations</h1>
      <p class="text-sm text-slate-500 mt-1">Members waiting for payment and activation.</p>
    </div>

    <div class="flex gap-3 mb-6">
      <div class="relative">
        <Search :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
        <input v-model="searchId" type="number" placeholder="Filter by Member ID"
          class="pl-9 pr-4 py-2.5 border border-slate-200 rounded-lg text-sm w-52 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
          @keyup.enter="search" />
      </div>
      <button @click="search" class="inline-flex items-center gap-2 bg-sky-500 hover:bg-sky-600 text-white px-4 py-2.5 rounded-lg text-sm font-semibold transition-colors">
        <Search :size="14" /> Search
      </button>
      <button v-if="searchId" @click="searchId = ''; load()"
        class="inline-flex items-center gap-1.5 text-sm text-slate-500 hover:text-slate-700 font-medium transition-colors">
        <X :size="13" /> Clear
      </button>
    </div>

    <div v-if="rows.length" class="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-slate-100">
            <th class="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Member ID</th>
            <th class="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Name</th>
            <th class="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Plan</th>
            <th class="text-left px-5 py-3 text-xs font-semibold text-slate-500 uppercase tracking-wide">Requested</th>
            <th class="px-5 py-3"></th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50">
          <tr v-for="row in rows" :key="row.membershipId" class="hover:bg-slate-50 transition-colors">
            <td class="px-5 py-3.5 font-mono text-xs font-semibold text-sky-600">#{{ row.userId }}</td>
            <td class="px-5 py-3.5 font-semibold text-slate-900">{{ row.memberName }}</td>
            <td class="px-5 py-3.5 text-slate-600">{{ row.membershipTypeName }}</td>
            <td class="px-5 py-3.5 text-slate-400 text-xs">{{ new Date(row.createdAt).toLocaleDateString() }}</td>
            <td class="px-5 py-3.5 text-right">
              <button @click="openModal(row)"
                class="inline-flex items-center gap-1.5 bg-emerald-500 hover:bg-emerald-600 text-white px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors">
                <CheckCircle :size="12" /> Activate
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="flex flex-col items-center justify-center py-20 text-slate-400">
      <CheckCircle :size="40" class="mb-3 text-emerald-400 opacity-60" />
      <p class="font-semibold text-slate-600">No pending activations</p>
      <p class="text-sm mt-1">All memberships are up to date.</p>
    </div>

    <div v-if="modal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50">
      <div class="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-sm">
        <h2 class="text-lg font-bold text-slate-900 mb-1">Activate Membership</h2>
        <p class="text-sm text-slate-500 mb-5">
          <strong class="text-slate-700">{{ modal.memberName }}</strong> — <em>{{ modal.planName }}</em>
        </p>
        <div class="mb-4">
          <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Start Date</label>
          <input v-model="startDate" type="date" required class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
        </div>
        <p v-if="activateError" class="text-rose-500 text-sm mb-3">{{ activateError }}</p>
        <div class="flex gap-3">
          <button @click="modal = null" class="flex-1 border border-slate-200 text-slate-700 py-2.5 rounded-lg text-sm font-medium hover:bg-slate-50 transition-colors">Cancel</button>
          <button @click="confirmActivation" :disabled="!startDate || activating"
            class="flex-1 bg-emerald-500 hover:bg-emerald-600 disabled:opacity-50 text-white py-2.5 rounded-lg text-sm font-semibold transition-colors">
            {{ activating ? 'Activating...' : 'Confirm' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
