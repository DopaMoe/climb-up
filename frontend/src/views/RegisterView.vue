<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMembershipTypes } from '../api/member'
import { registerMember } from '../api/public'
import { Mountain, User, Mail, Phone, CreditCard, PartyPopper } from 'lucide-vue-next'

const router = useRouter()
const auth = useAuthStore()

const form = ref({ name: '', email: '', phone: '', membershipTypeId: 0 })
const plans = ref<any[]>([])
const loading = ref(false)
const error = ref('')
const result = ref<{ userId: number; name: string; membershipTypeName: string } | null>(null)

onMounted(async () => {
  plans.value = await getMembershipTypes()
  if (plans.value.length) form.value.membershipTypeId = plans.value[0].id
})

async function submit() {
  error.value = ''
  loading.value = true
  try {
    result.value = await registerMember({
      name: form.value.name,
      email: form.value.email,
      phone: form.value.phone || undefined,
      membershipTypeId: form.value.membershipTypeId,
    })
  } catch (e: any) {
    error.value = e.response?.data?.message ?? 'Registration failed. Please try again.'
  } finally {
    loading.value = false
  }
}

function goToDashboard() {
  if (!result.value) return
  auth.enterAs('MEMBER', result.value.userId)
  router.push('/member/dashboard')
}
</script>

<template>
  <div class="min-h-screen bg-slate-900 flex items-center justify-center p-4"
    style="background-image: linear-gradient(rgba(56,189,248,0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(56,189,248,0.03) 1px, transparent 1px); background-size: 32px 32px;">
    <div class="absolute top-1/4 left-1/2 -translate-x-1/2 w-96 h-96 bg-sky-500/10 rounded-full blur-3xl pointer-events-none"></div>

    <div class="relative bg-slate-800 border border-slate-700 rounded-2xl shadow-2xl w-full max-w-md p-8">

      <!-- Success -->
      <div v-if="result" class="text-center space-y-5">
        <div class="w-16 h-16 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl flex items-center justify-center mx-auto">
          <PartyPopper :size="28" class="text-emerald-400" />
        </div>
        <div>
          <h2 class="text-2xl font-bold text-slate-100">You're in!</h2>
          <p class="text-slate-400 mt-2 text-sm">Welcome, <strong class="text-slate-200">{{ result.name }}</strong>. Your <em class="text-sky-400">{{ result.membershipTypeName }}</em> membership is pending activation.</p>
        </div>
        <div class="bg-slate-900/60 border border-slate-700 rounded-xl p-5">
          <p class="text-xs font-semibold text-slate-500 uppercase tracking-widest mb-2">Your Member ID</p>
          <p class="text-5xl font-black text-sky-400 font-mono">#{{ result.userId }}</p>
          <p class="text-xs text-slate-600 mt-2">Save this ID — you'll need it to log in.</p>
        </div>
        <p class="text-sm text-slate-500">Visit the gym to pay and activate your membership.</p>
        <button @click="goToDashboard"
          class="w-full bg-sky-500 hover:bg-sky-400 text-white py-3 rounded-xl font-semibold transition-colors shadow-sm shadow-sky-500/20">
          Go to My Dashboard →
        </button>
      </div>

      <!-- Form -->
      <div v-else>
        <div class="flex items-center gap-3 mb-7">
          <div class="w-9 h-9 bg-gradient-to-br from-sky-400 to-sky-600 rounded-xl flex items-center justify-center shadow-md shadow-sky-500/20 flex-shrink-0">
            <Mountain :size="17" class="text-white" :stroke-width="2.5" />
          </div>
          <div>
            <h1 class="text-xl font-bold text-slate-100 tracking-tight">Join ClimbUp</h1>
            <p class="text-slate-500 text-xs mt-0.5">Register and pick your membership</p>
          </div>
        </div>

        <form @submit.prevent="submit" class="space-y-4">
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Full Name</label>
            <div class="relative">
              <User :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
              <input v-model="form.name" required type="text" placeholder="Your full name"
                class="w-full bg-slate-900/60 border border-slate-700 rounded-lg pl-9 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
            </div>
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Email</label>
            <div class="relative">
              <Mail :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
              <input v-model="form.email" required type="email" placeholder="you@example.com"
                class="w-full bg-slate-900/60 border border-slate-700 rounded-lg pl-9 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
            </div>
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">
              Phone <span class="text-slate-700 normal-case font-normal">(optional)</span>
            </label>
            <div class="relative">
              <Phone :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
              <input v-model="form.phone" type="tel" placeholder="05XXXXXXXX"
                class="w-full bg-slate-900/60 border border-slate-700 rounded-lg pl-9 pr-4 py-2.5 text-sm text-slate-200 placeholder:text-slate-600 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500" />
            </div>
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Membership Plan</label>
            <div class="relative">
              <CreditCard :size="14" class="absolute left-3 top-1/2 -translate-y-1/2 text-slate-500 pointer-events-none" />
              <select v-model="form.membershipTypeId" required
                class="w-full bg-slate-900/60 border border-slate-700 rounded-lg pl-9 pr-4 py-2.5 text-sm text-slate-200 focus:outline-none focus:ring-2 focus:ring-sky-500 focus:border-sky-500 appearance-none">
                <option v-for="plan in plans" :key="plan.id" :value="plan.id">
                  {{ plan.name }} — {{ plan.effectivePrice }} SAR
                </option>
              </select>
            </div>
            <p class="text-xs text-slate-600 mt-1.5">Start date confirmed when you pay at the gym.</p>
          </div>

          <p v-if="error" class="text-rose-400 text-sm">{{ error }}</p>

          <button type="submit" :disabled="loading"
            class="w-full bg-sky-500 hover:bg-sky-400 disabled:opacity-50 text-white py-3 rounded-xl font-semibold transition-colors shadow-sm shadow-sky-500/20 mt-1">
            {{ loading ? 'Creating account...' : 'Create Account →' }}
          </button>
        </form>

        <p class="text-center text-sm text-slate-600 mt-5">
          Already a member?
          <RouterLink to="/login" class="text-sky-400 hover:text-sky-300 font-semibold transition-colors">Log in</RouterLink>
        </p>
      </div>
    </div>
  </div>
</template>
