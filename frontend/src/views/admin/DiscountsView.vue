<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDiscounts, updateDiscounts } from '../../api/admin'

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
    await updateDiscounts({
      globalDiscount: data.value.globalDiscount,
      typeDiscounts: data.value.types.map((t: any) => ({
        typeId: t.id,
        discountPct: t.discountPct,
      })),
    })
    success.value = true
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="p-8 max-w-2xl">
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Discount Management</h2>

    <div v-if="loading" class="text-gray-400">Loading...</div>

    <div v-else>
      <div v-if="success" class="mb-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm">
        Discounts saved successfully.
      </div>

      <!-- Global discount -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
        <h3 class="font-semibold text-gray-900 mb-1">Global Discount</h3>
        <p class="text-sm text-gray-500 mb-4">Applied to all membership types that don't have a per-type override.</p>
        <div class="flex items-center gap-3">
          <input v-model.number="data.globalDiscount" type="number" min="0" max="100" step="0.1"
            class="w-32 border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          <span class="text-gray-500">%</span>
          <span v-if="data.globalDiscount > 0" class="text-sm text-green-600 font-medium">Active</span>
          <span v-else class="text-sm text-gray-400">No global discount</span>
        </div>
      </div>

      <!-- Per-type discounts -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100">
        <div class="p-5 border-b border-gray-100">
          <h3 class="font-semibold text-gray-900">Per-type Discounts</h3>
          <p class="text-sm text-gray-500 mt-0.5">Set to 0 to fall back to global discount.</p>
        </div>
        <div class="divide-y divide-gray-50">
          <div v-for="t in data.types" :key="t.id"
            class="flex items-center justify-between px-5 py-4">
            <span class="font-medium text-gray-900">{{ t.name }}</span>
            <div class="flex items-center gap-2">
              <input v-model.number="t.discountPct" type="number" min="0" max="100" step="0.1"
                class="w-24 border border-gray-200 rounded-lg px-3 py-1.5 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <span class="text-gray-500 text-sm">%</span>
              <span v-if="t.discountPct > 0" class="text-xs text-blue-600 font-medium">override</span>
              <span v-else class="text-xs text-gray-400">→ global</span>
            </div>
          </div>
        </div>
      </div>

      <button @click="save" :disabled="saving"
        class="mt-6 bg-blue-600 text-white px-6 py-2.5 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50">
        {{ saving ? 'Saving...' : 'Save All Discounts' }}
      </button>
    </div>
  </div>
</template>
