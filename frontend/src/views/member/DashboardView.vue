<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../../stores/auth'
import { getMe } from '../../api/member'
import { Mountain, User, Mail, Phone, Hash } from 'lucide-vue-next'

const auth = useAuthStore()
const data = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  if (auth.userId) {
    try { data.value = await getMe(auth.userId) } catch {}
  }
  loading.value = false
})

function statusConfig(s: string) {
  const c: Record<string, { dot: string; badge: string; label: string }> = {
    ACTIVE: { dot: 'bg-emerald-500', badge: 'bg-emerald-50 text-emerald-700 border border-emerald-200', label: 'Active' },
    PENDING: { dot: 'bg-amber-500', badge: 'bg-amber-50 text-amber-700 border border-amber-200', label: 'Pending' },
    EXPIRED: { dot: 'bg-slate-400', badge: 'bg-slate-100 text-slate-500 border border-slate-200', label: 'Expired' },
    CANCELLED: { dot: 'bg-rose-500', badge: 'bg-rose-50 text-rose-600 border border-rose-200', label: 'Cancelled' },
  }
  return c[s] || c.EXPIRED
}
</script>

<template>
  <div>
    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else-if="!auth.userId" class="bg-amber-50 border border-amber-200 rounded-xl p-6 text-center">
      <p class="text-amber-800 font-medium">No member ID set. Please log in again.</p>
      <RouterLink to="/login" class="text-sky-500 hover:text-sky-600 text-sm mt-2 block font-medium">Back to login →</RouterLink>
    </div>

    <div v-else-if="data">
      <!-- Active membership hero -->
      <template v-for="m in data.memberships.filter((m: any) => m.status === 'ACTIVE')" :key="m.id">
        <div class="bg-gradient-to-br from-slate-900 via-slate-800 to-sky-950 rounded-2xl p-6 mb-6 relative overflow-hidden shadow-xl">
          <div class="absolute -right-8 -top-8 w-48 h-48 rounded-full border border-sky-500/10 pointer-events-none"></div>
          <div class="absolute -right-16 -top-16 w-72 h-72 rounded-full border border-sky-500/06 pointer-events-none"></div>
          <div class="relative">
            <div class="flex items-start justify-between mb-5">
              <div>
                <p class="text-xs font-semibold text-sky-400 uppercase tracking-widest mb-1">Current Plan</p>
                <h2 class="text-2xl font-black text-white tracking-tight">{{ m.membershipType.name }}</h2>
              </div>
              <div :class="['flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold', statusConfig(m.status).badge]">
                <span :class="['w-1.5 h-1.5 rounded-full', statusConfig(m.status).dot]"></span>
                {{ statusConfig(m.status).label }}
              </div>
            </div>
            <div class="grid grid-cols-3 gap-4">
              <div v-if="m.endDate">
                <p class="text-xs text-slate-500 uppercase tracking-wide mb-1">Expires</p>
                <p class="text-sm font-semibold text-slate-200">{{ m.endDate }}</p>
              </div>
              <div v-if="m.entriesRemaining != null">
                <p class="text-xs text-slate-500 uppercase tracking-wide mb-1">Entries Left</p>
                <p class="text-sm font-semibold text-sky-400">{{ m.entriesRemaining }}</p>
              </div>
              <div>
                <p class="text-xs text-slate-500 uppercase tracking-wide mb-1">Member ID</p>
                <p class="text-sm font-semibold text-slate-200 font-mono">#{{ data.user.id }}</p>
              </div>
              <div>
                <p class="text-xs text-slate-500 uppercase tracking-wide mb-1">Paid</p>
                <p class="text-sm font-semibold text-slate-200">{{ m.pricePaid }} SAR</p>
              </div>
            </div>
          </div>
        </div>
      </template>

      <!-- Profile -->
      <div class="bg-white rounded-xl border border-slate-200 shadow-sm p-5 mb-5">
        <h3 class="text-xs font-semibold text-slate-500 uppercase tracking-wide mb-4">Profile</h3>
        <div class="grid grid-cols-2 gap-3">
          <div class="flex items-center gap-2.5">
            <User :size="14" class="text-slate-400 flex-shrink-0" />
            <div><p class="text-xs text-slate-500">Name</p><p class="text-sm font-semibold text-slate-900">{{ data.user.name }}</p></div>
          </div>
          <div class="flex items-center gap-2.5">
            <Mail :size="14" class="text-slate-400 flex-shrink-0" />
            <div><p class="text-xs text-slate-500">Email</p><p class="text-sm text-slate-700 break-all">{{ data.user.email }}</p></div>
          </div>
          <div class="flex items-center gap-2.5">
            <Phone :size="14" class="text-slate-400 flex-shrink-0" />
            <div><p class="text-xs text-slate-500">Phone</p><p class="text-sm text-slate-700">{{ data.user.phone || '—' }}</p></div>
          </div>
          <div class="flex items-center gap-2.5">
            <Hash :size="14" class="text-slate-400 flex-shrink-0" />
            <div><p class="text-xs text-slate-500">Member ID</p><p class="text-sm font-mono font-semibold text-sky-600">#{{ data.user.id }}</p></div>
          </div>
        </div>
      </div>

      <!-- Non-active memberships -->
      <div v-if="data.memberships.filter((m: any) => m.status !== 'ACTIVE').length > 0" class="space-y-3">
        <h3 class="text-xs font-semibold text-slate-500 uppercase tracking-wide px-1">Other Memberships</h3>
        <div v-for="m in data.memberships.filter((m: any) => m.status !== 'ACTIVE')" :key="m.id"
          class="bg-white rounded-xl border border-slate-200 shadow-sm p-5">
          <div class="flex items-start justify-between mb-3">
            <h4 class="font-semibold text-slate-900">{{ m.membershipType.name }}</h4>
            <span :class="['inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold', statusConfig(m.status).badge]">
              <span :class="['w-1.5 h-1.5 rounded-full', statusConfig(m.status).dot]"></span>
              {{ statusConfig(m.status).label }}
            </span>
          </div>
          <div class="grid grid-cols-2 gap-2 text-sm mb-3">
            <div><span class="text-slate-500">Start:</span> <span class="ml-1.5 text-slate-700">{{ m.startDate ?? 'To be confirmed' }}</span></div>
            <div v-if="m.endDate"><span class="text-slate-500">Expires:</span> <span class="ml-1.5 text-slate-700">{{ m.endDate }}</span></div>
          </div>
          <div v-if="m.status === 'PENDING'" class="bg-amber-50 border border-amber-200 rounded-lg px-3 py-2 text-xs text-amber-700 font-medium">
            Pending activation — visit the gym to complete your payment.
          </div>
        </div>
      </div>

      <!-- No memberships -->
      <div v-if="data.memberships.length === 0" class="bg-white rounded-xl border border-slate-200 shadow-sm p-10 text-center">
        <Mountain :size="36" class="text-slate-300 mx-auto mb-3" />
        <p class="text-slate-500 font-medium mb-4">No memberships yet.</p>
        <RouterLink to="/member/memberships"
          class="inline-flex items-center gap-2 bg-sky-500 hover:bg-sky-400 text-white px-5 py-2.5 rounded-lg text-sm font-semibold transition-colors">
          Browse Membership Plans →
        </RouterLink>
      </div>
    </div>
  </div>
</template>
