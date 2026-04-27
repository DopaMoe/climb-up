<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMembershipTypes, deactivateMembershipType } from '../../api/admin'
const types = ref<any[]>([])
const loading = ref(true)

async function load() {
  loading.value = true
  try { types.value = await getMembershipTypes() }
  finally { loading.value = false }
}

onMounted(load)

async function handleDeactivate(id: number, name: string) {
  if (!confirm(`Deactivate "${name}"?`)) return
  await deactivateMembershipType(id)
  load()
}

function typeLabel(t: any) {
  if (t.durationDays) return `${t.durationDays} days`
  if (t.entriesCount) return `${t.entriesCount} ${t.entriesCount === 1 ? 'entry' : 'entries'}${t.entriesValidityDays ? ` / ${t.entriesValidityDays}d` : ''}`
  return '-'
}
</script>

<template>
  <div class="p-8">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-2xl font-bold text-gray-900">Membership Types</h2>
      <RouterLink to="/admin/membership-types/new"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">
        + New Type
      </RouterLink>
    </div>

    <div v-if="loading" class="text-gray-400">Loading...</div>

    <div v-else class="bg-white rounded-xl shadow-sm border border-gray-100">
      <table class="w-full text-sm">
        <thead>
          <tr class="text-left text-gray-500 border-b border-gray-100">
            <th class="px-5 py-3 font-medium">Name</th>
            <th class="px-5 py-3 font-medium">Duration / Entries</th>
            <th class="px-5 py-3 font-medium">Base Price</th>
            <th class="px-5 py-3 font-medium">Discount</th>
            <th class="px-5 py-3 font-medium">Status</th>
            <th class="px-5 py-3 font-medium">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-50">
          <tr v-for="t in types" :key="t.id" class="hover:bg-gray-50">
            <td class="px-5 py-3 font-medium text-gray-900">{{ t.name }}</td>
            <td class="px-5 py-3 text-gray-600">{{ typeLabel(t) }}</td>
            <td class="px-5 py-3 text-gray-700">{{ t.basePrice }} SAR</td>
            <td class="px-5 py-3 text-gray-600">{{ t.discountPct > 0 ? `${t.discountPct}%` : '—' }}</td>
            <td class="px-5 py-3">
              <span :class="t.isActive ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-500'"
                class="text-xs px-2 py-0.5 rounded-full font-medium">
                {{ t.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-5 py-3">
              <div class="flex gap-3">
                <RouterLink :to="`/admin/membership-types/${t.id}`"
                  class="text-blue-600 hover:text-blue-800 font-medium">Edit</RouterLink>
                <button v-if="t.isActive" @click="handleDeactivate(t.id, t.name)"
                  class="text-red-500 hover:text-red-700">Deactivate</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
