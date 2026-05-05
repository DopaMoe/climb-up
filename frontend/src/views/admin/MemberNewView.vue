<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createMember } from '../../api/admin'
import { ArrowLeft, User, Mail, Phone } from 'lucide-vue-next'

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
  } finally { loading.value = false }
}
</script>

<template>
  <div class="p-6 lg:p-8 max-w-lg">
    <button @click="router.back()" class="inline-flex items-center gap-1.5 text-slate-500 hover:text-slate-900 text-sm font-medium mb-6 transition-colors">
      <ArrowLeft :size="15" /> Back
    </button>
    <h1 class="text-2xl font-bold text-slate-900 tracking-tight mb-6">Add New Member</h1>

    <form @submit.prevent="submit" class="bg-white rounded-xl border border-slate-200 shadow-sm p-6 space-y-5">
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Full Name *</label>
        <div class="relative">
          <User :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input v-model="form.name" required class="w-full pl-9 pr-4 py-2.5 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
        </div>
      </div>
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Email *</label>
        <div class="relative">
          <Mail :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input v-model="form.email" type="email" required class="w-full pl-9 pr-4 py-2.5 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
        </div>
      </div>
      <div>
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Phone</label>
        <div class="relative">
          <Phone :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input v-model="form.phone" class="w-full pl-9 pr-4 py-2.5 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
        </div>
      </div>
      <p v-if="error" class="text-rose-500 text-sm">{{ error }}</p>
      <button type="submit" :disabled="loading"
        class="w-full bg-sky-500 hover:bg-sky-600 disabled:opacity-50 text-white py-2.5 rounded-lg font-semibold text-sm transition-colors">
        {{ loading ? 'Creating...' : 'Create Member' }}
      </button>
    </form>
  </div>
</template>
