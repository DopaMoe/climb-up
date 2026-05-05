<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMembershipTypes, deactivateMembershipType } from '../../api/admin'
import { Plus, Pencil, PowerOff } from 'lucide-vue-next'

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
  return '—'
}
</script>

<template>
  <div class="p-6 lg:p-8">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-slate-900 tracking-tight">Membership Types</h1>
        <p class="text-sm text-slate-500 mt-0.5">{{ types.filter(t => t.isActive).length }} active types</p>
      </div>
      <RouterLink to="/admin/membership-types/new"
        class="inline-flex items-center gap-2 bg-sky-500 hover:bg-sky-600 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors shadow-sm">
        <Plus :size="15" /> New Type
      </RouterLink>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else class="bg-white rounded-xl border border-slate-200 shadow-sm">
      <table class="w-full text-sm">
        <thead>
          <tr class="border-b border-slate-100">
            <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Name</th>
            <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Duration / Entries</th>
            <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Base Price</th>
            <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Discount</th>
            <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Status</th>
            <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Actions</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50">
          <tr v-for="t in types" :key="t.id" class="hover:bg-slate-50 transition-colors">
            <td class="px-5 py-3.5 font-semibold text-slate-900">{{ t.name }}</td>
            <td class="px-5 py-3.5 text-slate-600">{{ typeLabel(t) }}</td>
            <td class="px-5 py-3.5 font-semibold text-slate-700">{{ t.basePrice }} SAR</td>
            <td class="px-5 py-3.5 text-slate-600">{{ t.discountPct > 0 ? `${t.discountPct}%` : '—' }}</td>
            <td class="px-5 py-3.5">
              <span :class="t.isActive ? 'bg-emerald-50 text-emerald-700 border-emerald-200' : 'bg-slate-100 text-slate-500 border-slate-200'"
                class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold border">
                {{ t.isActive ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td class="px-5 py-3.5">
              <div class="flex items-center gap-1">
                <RouterLink :to="`/admin/membership-types/${t.id}`"
                  class="inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium text-sky-600 hover:bg-sky-50 transition-colors">
                  <Pencil :size="12" /> Edit
                </RouterLink>
                <button v-if="t.isActive" @click="handleDeactivate(t.id, t.name)"
                  class="inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs text-slate-400 hover:bg-rose-50 hover:text-rose-500 transition-colors">
                  <PowerOff :size="12" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
