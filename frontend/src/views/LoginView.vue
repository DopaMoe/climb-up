<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMembers } from '../api/admin'
import { Mountain, Lock, User, ArrowRight } from 'lucide-vue-next'

const router = useRouter()
const auth = useAuthStore()
const memberIdInput = ref('')
const memberError = ref('')
const loading = ref(false)

async function enterAsAdmin() {
  auth.enterAs('ADMIN')
  router.push('/admin/dashboard')
}

async function enterAsMember() {
  memberError.value = ''
  const id = Number(memberIdInput.value)
  if (!id) {
    memberError.value = 'Please enter your member ID'
    return
  }
  loading.value = true
  try {
    await getMembers(undefined, 0)
    auth.enterAs('MEMBER', id)
    router.push('/member/dashboard')
  } catch {
    memberError.value = 'Member not found. Please check your ID.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="min-h-screen bg-slate-900 flex items-center justify-center p-4"
    style="background-image: linear-gradient(rgba(56,189,248,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(56,189,248,0.03) 1px, transparent 1px); background-size: 32px 32px;">
    <div class="absolute top-1/4 left-1/2 -translate-x-1/2 w-96 h-96 bg-sky-500/10 rounded-full blur-3xl pointer-events-none"></div>

    <div class="relative bg-slate-800 border border-slate-700 rounded-2xl shadow-2xl w-full max-w-sm p-8">
      <div class="text-center mb-8">
        <div class="w-12 h-12 bg-gradient-to-br from-sky-400 to-sky-600 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-sky-500/25">
          <Mountain :size="22" class="text-white" :stroke-width="2.5" />
        </div>
        <h1 class="text-2xl font-bold text-slate-100 tracking-tight">ClimbUp</h1>
        <p class="text-slate-500 text-sm mt-1">Gym Membership Manager</p>
      </div>

      <div class="space-y-3">
        <p class="text-xs font-semibold text-slate-500 uppercase tracking-widest text-center">Sign in as</p>

        <button @click="enterAsAdmin"
          class="w-full flex items-center gap-3 bg-slate-900/80 hover:bg-slate-900 border border-slate-700 hover:border-slate-600 text-slate-200 px-4 py-3.5 rounded-xl font-semibold transition-all group">
          <div class="w-8 h-8 bg-slate-800 rounded-lg flex items-center justify-center group-hover:bg-slate-700 transition-colors flex-shrink-0">
            <Lock :size="14" class="text-slate-400" />
          </div>
          <span class="flex-1 text-left text-sm">Continue as Admin</span>
          <ArrowRight :size="14" class="text-slate-600 group-hover:text-slate-400 transition-colors" />
        </button>

        <div class="flex items-center gap-3 py-1">
          <div class="flex-1 h-px bg-slate-700"></div>
          <span class="text-xs text-slate-600 font-medium uppercase tracking-wider">or member</span>
          <div class="flex-1 h-px bg-slate-700"></div>
        </div>

        <div class="bg-slate-900/50 border border-slate-700 rounded-xl p-4 space-y-3">
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Member ID</label>
            <div class="relative">
              <User :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
              <input v-model="memberIdInput" type="number" placeholder="Enter your member ID"
                class="w-full bg-slate-800 border border-slate-700 rounded-lg pl-9 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500"
                @keyup.enter="enterAsMember" />
            </div>
            <p v-if="memberError" class="text-rose-400 text-xs mt-1.5">{{ memberError }}</p>
          </div>
          <button @click="enterAsMember" :disabled="loading"
            class="w-full bg-sky-500 hover:bg-sky-400 disabled:opacity-50 text-white py-2.5 rounded-lg text-sm font-semibold transition-colors shadow-sm shadow-sky-500/20">
            {{ loading ? 'Checking...' : 'Enter Member Portal' }}
          </button>
        </div>
      </div>

      <p class="text-center text-sm text-slate-600 mt-6">
        New member?
        <RouterLink to="/register" class="text-sky-400 hover:text-sky-300 font-semibold transition-colors">Register here →</RouterLink>
      </p>
    </div>
  </div>
</template>
