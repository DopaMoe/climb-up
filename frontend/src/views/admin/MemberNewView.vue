<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createMember } from '../../api/admin'

const router = useRouter()
const form = ref({ name: '', email: '', phone: '' })
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    await createMember(form.value)
    router.push('/admin/members')
  } catch (e: any) {
    error.value = e.response?.data?.message || 'Failed to create member'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="p-8 max-w-lg">
    <button @click="router.back()" class="text-gray-500 hover:text-gray-700 text-sm mb-4 flex items-center gap-1">← Back</button>
    <h2 class="text-2xl font-bold text-gray-900 mb-6">Add New Member</h2>

    <form @submit.prevent="submit" class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 space-y-4">
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Full Name *</label>
        <input v-model="form.name" required class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Email *</label>
        <input v-model="form.email" type="email" required class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>
      <div>
        <label class="text-sm font-medium text-gray-700 block mb-1">Phone</label>
        <input v-model="form.phone" class="w-full border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
      </div>
      <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>
      <button type="submit" :disabled="loading"
        class="w-full bg-blue-600 text-white py-2 rounded-lg font-medium hover:bg-blue-700 disabled:opacity-50">
        {{ loading ? 'Creating...' : 'Create Member' }}
      </button>
    </form>
  </div>
</template>
