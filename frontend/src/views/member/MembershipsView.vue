<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { getMembershipTypes, requestMembership } from '../../api/member'
import { CreditCard, Clock, CheckCircle, X, CalendarDays } from 'lucide-vue-next'

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
  if (t.durationDays) { d.setDate(d.getDate() + t.durationDays); return d.toISOString().split('T')[0] }
  if (t.entriesValidityDays) { d.setDate(d.getDate() + t.entriesValidityDays); return d.toISOString().split('T')[0] }
  return null
}

async function request() {
  if (!auth.userId) { reqError.value = 'No member ID. Please log in again.'; return }
  reqError.value = ''
  reqSuccess.value = ''
  requesting.value = true
  try {
    await requestMembership(auth.userId, { membershipTypeId: selected.value.id, startDate: startDate.value, directActivate: false })
    reqSuccess.value = `Request submitted for "${selected.value.name}". Visit the gym to activate after payment.`
    selected.value = null
  } catch (e: any) {
    reqError.value = e.response?.data?.message || 'Request failed'
  } finally { requesting.value = false }
}
</script>

<template>
  <div>
    <div class="mb-6">
      <h2 class="text-2xl font-bold text-slate-900 tracking-tight">Membership Plans</h2>
      <p class="text-sm text-slate-500 mt-1">Choose the plan that fits your training style.</p>
    </div>

    <div v-if="reqSuccess" class="mb-6 bg-emerald-50 border border-emerald-200 text-emerald-800 px-4 py-3 rounded-xl text-sm font-medium flex items-center gap-2">
      <CheckCircle :size="15" class="text-emerald-500 flex-shrink-0" /> {{ reqSuccess }}
    </div>

    <div v-if="loading" class="flex items-center justify-center py-16">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
      <div v-for="t in types" :key="t.id"
        class="bg-white rounded-xl border border-slate-200 shadow-sm p-6 hover:border-sky-300 hover:shadow-md transition-all group">
        <div class="flex items-start justify-between mb-3">
          <div class="w-10 h-10 bg-sky-50 rounded-xl flex items-center justify-center group-hover:bg-sky-100 transition-colors">
            <CreditCard :size="18" class="text-sky-500" />
          </div>
          <div class="text-right">
            <p class="text-2xl font-black text-sky-600">{{ t.effectivePrice }} <span class="text-base font-semibold text-slate-400">SAR</span></p>
            <p v-if="t.effectivePrice !== t.basePrice" class="text-sm text-slate-400 line-through">{{ t.basePrice }} SAR</p>
          </div>
        </div>
        <h3 class="text-base font-bold text-slate-900 mb-1">{{ t.name }}</h3>
        <p class="text-sm text-slate-500 mb-3 min-h-[40px]">{{ t.description }}</p>
        <div class="flex items-center gap-2 mb-4">
          <Clock :size="13" class="text-slate-400" />
          <span class="text-xs font-medium text-slate-500">{{ typeDesc(t) }}</span>
        </div>
        <button @click="selected = t; startDate = today(); reqError = ''"
          class="w-full bg-sky-500 hover:bg-sky-400 text-white py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm shadow-sky-500/10">
          Request This Plan
        </button>
      </div>
    </div>

    <!-- Request modal -->
    <div v-if="selected" class="fixed inset-0 bg-slate-900/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
      <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <div class="flex items-center justify-between mb-5">
          <h3 class="font-bold text-slate-900 text-lg">Request Plan</h3>
          <button @click="selected = null" class="w-8 h-8 flex items-center justify-center rounded-lg text-slate-400 hover:bg-slate-100 hover:text-slate-700 transition-colors">
            <X :size="16" />
          </button>
        </div>

        <div class="bg-sky-50 border border-sky-200 rounded-xl p-4 mb-5">
          <p class="font-bold text-sky-900 text-base mb-0.5">{{ selected.name }}</p>
          <p class="text-sky-700 text-sm font-semibold">{{ selected.effectivePrice }} SAR · {{ typeDesc(selected) }}</p>
        </div>

        <div class="mb-4">
          <label class="block text-xs font-semibold text-slate-600 uppercase tracking-wide mb-1.5 flex items-center gap-1.5">
            <CalendarDays :size="12" /> Start Date
          </label>
          <input v-model="startDate" type="date" :min="today()"
            class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
          <p v-if="computedEnd(selected, startDate)" class="text-xs text-slate-500 mt-1.5">
            <template v-if="selected.durationDays">Expires: <strong>{{ computedEnd(selected, startDate) }}</strong></template>
            <template v-else-if="selected.entriesValidityDays">Pack valid until: <strong>{{ computedEnd(selected, startDate) }}</strong></template>
          </p>
        </div>

        <div class="bg-amber-50 border border-amber-200 rounded-lg px-3 py-2.5 text-xs text-amber-700 font-medium mb-4">
          Your request will be saved as Pending. Visit the gym and pay in person to activate.
        </div>

        <p v-if="reqError" class="text-rose-500 text-sm mb-3">{{ reqError }}</p>

        <div class="flex gap-3">
          <button @click="request" :disabled="requesting"
            class="flex-1 bg-sky-500 hover:bg-sky-400 disabled:opacity-50 text-white py-2.5 rounded-lg text-sm font-semibold transition-colors">
            {{ requesting ? 'Submitting...' : 'Submit Request' }}
          </button>
          <button @click="selected = null"
            class="flex-1 bg-slate-100 hover:bg-slate-200 text-slate-700 py-2.5 rounded-lg text-sm font-medium transition-colors">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
