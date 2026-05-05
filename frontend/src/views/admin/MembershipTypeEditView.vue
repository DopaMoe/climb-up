<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMembershipType, updateMembershipType, createMembershipType } from '../../api/admin'
import { ArrowLeft } from 'lucide-vue-next'

const route = useRoute()
const router = useRouter()
const isNew = route.params.id === 'new'
const id = isNew ? null : Number(route.params.id)

const form = ref({ name: '', description: '', basePrice: 0, durationDays: null as number | null, entriesCount: null as number | null, entriesValidityDays: null as number | null, discountPct: 0, isActive: true })
const typeMode = ref<'time' | 'entries'>('time')
const loading = ref(!isNew)
const saving = ref(false)
const error = ref('')

onMounted(async () => {
  if (!isNew && id) {
    const t = await getMembershipType(id)
    form.value = { ...t }
    typeMode.value = t.entriesCount ? 'entries' : 'time'
    loading.value = false
  }
})

async function save() {
  error.value = ''
  saving.value = true
  const payload = { ...form.value, durationDays: typeMode.value === 'time' ? form.value.durationDays : null, entriesCount: typeMode.value === 'entries' ? form.value.entriesCount : null, entriesValidityDays: typeMode.value === 'entries' ? form.value.entriesValidityDays : null }
  try {
    if (isNew) { await createMembershipType(payload) } else { await updateMembershipType(id!, payload) }
    router.push('/admin/membership-types')
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Failed to save'
  } finally { saving.value = false }
}
</script>

<template>
  <div class="p-6 lg:p-8 max-w-xl">
    <button @click="router.back()" class="inline-flex items-center gap-1.5 text-slate-500 hover:text-slate-900 text-sm font-medium mb-6 transition-colors">
      <ArrowLeft :size="15" /> Back
    </button>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight mb-6">{{ isNew ? 'New Membership Type' : 'Edit Membership Type' }}</h1>

    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <form v-else @submit.prevent="save" class="bg-white rounded-xl border border-slate-200 shadow-sm p-6 space-y-5">
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Name *</label>
        <input v-model="form.name" required class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
      </div>
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Description</label>
        <textarea v-model="form.description" rows="2" class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500 resize-none"></textarea>
      </div>
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Base Price (SAR) *</label>
        <input v-model.number="form.basePrice" type="number" min="0" step="0.01" required class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
      </div>
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-2">Type</label>
        <div class="flex gap-4">
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="radio" v-model="typeMode" value="time" class="accent-sky-500" />
            <span class="text-sm text-slate-700 font-medium">Time-based (days)</span>
          </label>
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="radio" v-model="typeMode" value="entries" class="accent-sky-500" />
            <span class="text-sm text-slate-700 font-medium">Entry-based</span>
          </label>
        </div>
      </div>
      <div v-if="typeMode === 'time'">
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Duration (days) *</label>
        <input v-model.number="form.durationDays" type="number" min="1" required class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
      </div>
      <div v-else class="grid grid-cols-2 gap-4">
        <div>
          <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Entries *</label>
          <input v-model.number="form.entriesCount" type="number" min="1" required class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
        </div>
        <div>
          <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Validity (days, optional)</label>
          <input v-model.number="form.entriesValidityDays" type="number" min="1" class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
        </div>
      </div>
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Per-type Discount % <span class="text-slate-400 normal-case font-normal">(0 = use global)</span></label>
        <input v-model.number="form.discountPct" type="number" min="0" max="100" step="0.1" class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
      </div>
      <p v-if="error" class="text-rose-500 text-sm">{{ error }}</p>
      <button type="submit" :disabled="saving"
        class="w-full bg-sky-500 hover:bg-sky-600 disabled:opacity-50 text-white py-2.5 rounded-lg font-semibold text-sm transition-colors">
        {{ saving ? 'Saving...' : isNew ? 'Create Type' : 'Save Changes' }}
      </button>
    </form>
  </div>
</template>
