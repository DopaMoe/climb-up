<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getMembers, deleteMember } from '../../api/admin'
import { Search, Plus, Trash2, Eye, ChevronLeft, ChevronRight } from 'lucide-vue-next'

const search = ref('')
const page = ref(0)
const data = ref<any>(null)
const loading = ref(false)

async function load() {
  loading.value = true
  try { data.value = await getMembers(search.value || undefined, page.value) }
  finally { loading.value = false }
}

watch(search, () => { page.value = 0; load() })
onMounted(load)

async function handleDelete(id: number, name: string) {
  if (!confirm(`Delete member "${name}"? This cannot be undone.`)) return
  await deleteMember(id)
  load()
}
</script>

<template>
  <div class="p-6 lg:p-8">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h1 class="text-2xl font-bold text-slate-900 tracking-tight">Members</h1>
        <p v-if="data" class="text-sm text-slate-500 mt-0.5">{{ data.totalElements }} total</p>
      </div>
      <RouterLink to="/admin/members/new"
        class="inline-flex items-center gap-2 bg-sky-500 hover:bg-sky-600 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors shadow-sm">
        <Plus :size="15" /> Add Member
      </RouterLink>
    </div>

    <div class="bg-white rounded-xl border border-slate-200 shadow-sm">
      <div class="p-4 border-b border-slate-100">
        <div class="relative">
          <Search :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input v-model="search" type="text" placeholder="Search by name or email..."
            class="w-full pl-9 pr-4 py-2 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
        </div>
      </div>

      <div v-if="loading" class="flex items-center justify-center py-16">
        <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
      </div>

      <div v-else-if="data">
        <table class="w-full text-sm">
          <thead>
            <tr class="border-b border-slate-100">
              <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">ID</th>
              <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Name</th>
              <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Email</th>
              <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Phone</th>
              <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Joined</th>
              <th class="px-5 py-3 text-left text-xs font-semibold text-slate-500 uppercase tracking-wide">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-50">
            <tr v-for="m in data.content" :key="m.id" class="hover:bg-slate-50 transition-colors">
              <td class="px-5 py-3.5 text-slate-400 font-mono text-xs">#{{ m.id }}</td>
              <td class="px-5 py-3.5 font-semibold text-slate-900">{{ m.name }}</td>
              <td class="px-5 py-3.5 text-slate-600">{{ m.email }}</td>
              <td class="px-5 py-3.5 text-slate-500">{{ m.phone || '—' }}</td>
              <td class="px-5 py-3.5 text-slate-400 text-xs">{{ new Date(m.createdAt).toLocaleDateString() }}</td>
              <td class="px-5 py-3.5">
                <div class="flex items-center gap-1">
                  <RouterLink :to="`/admin/members/${m.id}`"
                    class="inline-flex items-center gap-1.5 px-2.5 py-1.5 rounded-lg text-xs font-medium text-sky-600 hover:bg-sky-50 transition-colors">
                    <Eye :size="12" /> View
                  </RouterLink>
                  <button @click="handleDelete(m.id, m.name)"
                    class="inline-flex items-center gap-1.5 px-2 py-1.5 rounded-lg text-xs text-slate-400 hover:bg-rose-50 hover:text-rose-500 transition-colors">
                    <Trash2 :size="12" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="data.content.length === 0">
              <td colspan="6" class="px-5 py-12 text-center text-slate-400 text-sm">No members found.</td>
            </tr>
          </tbody>
        </table>

        <div v-if="data.totalPages > 1" class="flex items-center justify-center gap-1.5 p-4 border-t border-slate-100">
          <button @click="page = Math.max(0, page-1); load()" :disabled="page === 0"
            class="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 disabled:opacity-30 transition-colors">
            <ChevronLeft :size="16" />
          </button>
          <button v-for="p in data.totalPages" :key="p" @click="page = p - 1; load()"
            :class="['w-8 h-8 rounded-lg text-sm font-medium transition-colors', page === p - 1 ? 'bg-sky-500 text-white' : 'text-slate-600 hover:bg-slate-100']">
            {{ p }}
          </button>
          <button @click="page = Math.min(data.totalPages-1, page+1); load()" :disabled="page >= data.totalPages-1"
            class="p-1.5 rounded-lg text-slate-400 hover:bg-slate-100 disabled:opacity-30 transition-colors">
            <ChevronRight :size="16" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
