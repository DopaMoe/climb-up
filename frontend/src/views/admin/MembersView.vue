<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getMembers, deleteMember } from '../../api/admin'

const search = ref('')
const page = ref(0)
const data = ref<any>(null)
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    data.value = await getMembers(search.value || undefined, page.value)
  } finally {
    loading.value = false
  }
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
  <div class="p-8">
    <div class="flex items-center justify-between mb-6">
      <h2 class="text-2xl font-bold text-gray-900">Members</h2>
      <RouterLink to="/admin/members/new"
        class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700 transition-colors">
        + Add Member
      </RouterLink>
    </div>

    <div class="bg-white rounded-xl shadow-sm border border-gray-100">
      <div class="p-4 border-b border-gray-100">
        <input v-model="search" type="text" placeholder="Search by name or email..."
          class="w-full border border-gray-200 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>

      <div v-if="loading" class="p-8 text-center text-gray-400">Loading...</div>

      <div v-else-if="data">
        <table class="w-full text-sm">
          <thead>
            <tr class="text-left text-gray-500 border-b border-gray-100">
              <th class="px-5 py-3 font-medium">ID</th>
              <th class="px-5 py-3 font-medium">Name</th>
              <th class="px-5 py-3 font-medium">Email</th>
              <th class="px-5 py-3 font-medium">Phone</th>
              <th class="px-5 py-3 font-medium">Joined</th>
              <th class="px-5 py-3 font-medium">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-50">
            <tr v-for="m in data.content" :key="m.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-5 py-3 text-gray-400 font-mono">#{{ m.id }}</td>
              <td class="px-5 py-3 font-medium text-gray-900">{{ m.name }}</td>
              <td class="px-5 py-3 text-gray-600">{{ m.email }}</td>
              <td class="px-5 py-3 text-gray-600">{{ m.phone || '-' }}</td>
              <td class="px-5 py-3 text-gray-400">{{ new Date(m.createdAt).toLocaleDateString() }}</td>
              <td class="px-5 py-3">
                <div class="flex gap-2">
                  <RouterLink :to="`/admin/members/${m.id}`"
                    class="text-blue-600 hover:text-blue-800 font-medium">View</RouterLink>
                  <button @click="handleDelete(m.id, m.name)"
                    class="text-red-500 hover:text-red-700">Delete</button>
                </div>
              </td>
            </tr>
            <tr v-if="data.content.length === 0">
              <td colspan="6" class="px-5 py-8 text-center text-gray-400">No members found.</td>
            </tr>
          </tbody>
        </table>

        <!-- Pagination -->
        <div v-if="data.totalPages > 1" class="flex justify-center gap-2 p-4 border-t border-gray-100">
          <button v-for="p in data.totalPages" :key="p"
            @click="page = p - 1; load()"
            :class="['px-3 py-1 rounded text-sm', page === p - 1 ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200']">
            {{ p }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
