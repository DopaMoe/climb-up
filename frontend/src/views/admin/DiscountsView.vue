<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDiscounts, updateDiscounts } from '../../api/admin'
import { Save, Percent } from 'lucide-vue-next'

const data = ref<any>(null)
const loading = ref(true)
const saving = ref(false)
const success = ref(false)

onMounted(async () => {
  data.value = await getDiscounts()
  loading.value = false
})

async function save() {
  saving.value = true
  success.value = false
  try {
    await updateDiscounts({ globalDiscount: data.value.globalDiscount, typeDiscounts: data.value.types.map((t: any) => ({ typeId: t.id, discountPct: t.discountPct })) })
    success.value = true
  } finally { saving.value = false }
}
</script>

<template>
  <div class="p-6 lg:p-8 max-w-2xl">
    <div class="mb-6">
      <h1 class="text-2xl font-bold text-slate-900 tracking-tight">Discount Management</h1>
      <p class="text-sm text-slate-500 mt-1">Set global and per-type membership discounts.</p>
    </div>

    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else>
      <div v-if="success" class="mb-5 bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 rounded-xl text-sm font-medium">
        Discounts saved successfully.
      </div>

      <div class="bg-white rounded-xl border border-slate-200 shadow-sm p-6 mb-5">
        <h3 class="text-sm font-semibold text-slate-900 mb-1">Global Discount</h3>
        <p class="text-xs text-slate-500 mb-4">Applied to all types without a per-type override.</p>
        <div class="flex items-center gap-3">
          <div class="relative">
            <Percent :size="13" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
            <input v-model.number="data.globalDiscount" type="number" min="0" max="100" step="0.1"
              class="w-32 pl-9 pr-4 py-2.5 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
          </div>
          <span v-if="data.globalDiscount > 0" class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">Active</span>
          <span v-else class="text-sm text-slate-400">No global discount</span>
        </div>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 shadow-sm mb-5">
        <div class="px-6 py-4 border-b border-slate-100">
          <h3 class="text-sm font-semibold text-slate-900">Per-type Discounts</h3>
          <p class="text-xs text-slate-500 mt-0.5">Set to 0 to fall back to global discount.</p>
        </div>
        <div class="divide-y divide-slate-50">
          <div v-for="t in data.types" :key="t.id" class="flex items-center justify-between px-6 py-4">
            <span class="font-medium text-slate-900 text-sm">{{ t.name }}</span>
            <div class="flex items-center gap-2">
              <div class="relative">
                <Percent :size="12" class="absolute left-2.5 top-1/2 -translate-y-1/2 text-slate-400" />
                <input v-model.number="t.discountPct" type="number" min="0" max="100" step="0.1"
                  class="w-24 pl-7 pr-3 py-1.5 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
              </div>
              <span v-if="t.discountPct > 0" class="text-xs font-semibold text-sky-600">override</span>
              <span v-else class="text-xs text-slate-400">→ global</span>
            </div>
          </div>
        </div>
      </div>

      <button @click="save" :disabled="saving"
        class="inline-flex items-center gap-2 bg-sky-500 hover:bg-sky-600 disabled:opacity-50 text-white px-6 py-2.5 rounded-lg font-semibold text-sm transition-colors">
        <Save :size="15" /> {{ saving ? 'Saving...' : 'Save All Discounts' }}
      </button>
    </div>
  </div>
</template>
