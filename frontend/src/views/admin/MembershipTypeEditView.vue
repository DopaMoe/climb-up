<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMembershipType, updateMembershipType, createMembershipType } from '../../api/admin'

const route = useRoute()
const router = useRouter()
const isNew = route.params.id === 'new'
const id = isNew ? null : Number(route.params.id)

const form = ref({
  name: '', description: '', basePrice: 0,
  durationDays: null as number | null,
  entriesCount: null as number | null,
  entriesValidityDays: null as number | null,
  discountPct: 0, isActive: true,
})
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
  const payload = {
    ...form.value,
    durationDays: typeMode.value === 'time' ? form.value.durationDays : null,
    entriesCount: typeMode.value === 'entries' ? form.value.entriesCount : null,
    entriesValidityDays: typeMode.value === 'entries' ? form.value.entriesValidityDays : null,
  }
  try {
    if (isNew) {
      await createMembershipType(payload)
    } else {
      await updateMembershipType(id!, payload)
    }
    router.push('/admin/membership-types')
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Failed to save'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="p-8 max-w-xl">
    <button @click="router.back()" class="text-gray-500 hover:text-gray-700 text-sm mb-4 flex items-center gap-1">← Back</button>
    <h2 class="text-2xl font-bold text-gray-900 mb-6">{{ isNew ? 'New Membership Type' : 'Edit Membership Type' }}</h2>

    <div v-if="loading" class="text-gray-400">Loading...</div>

    <form v-else @submit.prevent="save" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 space-y-5">
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Name *</label>
        <input v-model="form.name" required class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Description</label>
        <textarea v-model="form.description" rows="2" class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"></textarea>
      </div>
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Base Price (SAR) *</label>
        <input v-model.number="form.basePrice" type="number" min="0" step="0.01" required
          class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      <!-- Type mode -->
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-2">Membership Type</label>
        <div class="flex gap-3">
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="radio" v-model="typeMode" value="time" class="text-blue-600" />
            <span class="text-sm">Time-based (days)</span>
          </label>
          <label class="flex items-center gap-2 cursor-pointer">
            <input type="radio" v-model="typeMode" value="entries" class="text-blue-600" />
            <span class="text-sm">Entry-based</span>
          </label>
        </div>
      </div>

      <div v-if="typeMode === 'time'">
        <label class="text-sm font-medium text-gray-700 block mb-1">Duration (days) *</label>
        <input v-model.number="form.durationDays" type="number" min="1" required
          class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      <div v-else class="grid grid-cols-2 gap-4">
        <div>
          <label class="text-sm font-medium text-gray-700 block mb-1">Number of Entries *</label>
          <input v-model.number="form.entriesCount" type="number" min="1" required
            class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
        <div>
          <label class="text-sm font-medium text-gray-700 block mb-1">Validity (days, optional)</label>
          <input v-model.number="form.entriesValidityDays" type="number" min="1"
            class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>
      </div>

      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Per-type Discount % (0 = use global)</label>
        <input v-model.number="form.discountPct" type="number" min="0" max="100" step="0.1"
          class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

      <button type="submit" :disabled="saving"
        class="w-full bg-blue-600 text-white py-2 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50">
        {{ saving ? 'Saving...' : isNew ? 'Create Type' : 'Save Changes' }}
      </button>
    </form>
  </div>
</template>
