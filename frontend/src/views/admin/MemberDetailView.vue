<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMember, updateMember, assignMembership, activateMembership, cancelMembership, getMembershipTypes } from '../../api/admin'
import { ArrowLeft, Pencil, Plus, CheckCircle } from 'lucide-vue-next'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const id = Number(route.params.id)

const data = ref<any>(null)
const types = ref<any[]>([])
const loading = ref(true)
const editing = ref(false)
const editForm = ref({ name: '', email: '', phone: '' })
const showAssign = ref(false)
const assignForm = ref({ membershipTypeId: '', startDate: today(), directActivate: true })
const assignError = ref('')
const successMsg = ref('')
const activateModal = ref<{ membershipId: number } | null>(null)
const activateStartDate = ref('')

function today() { return new Date().toISOString().split('T')[0] }

async function load() {
  loading.value = true
  try {
    const [detail, typeList] = await Promise.all([getMember(id), getMembershipTypes()])
    data.value = detail
    types.value = typeList
    editForm.value = { name: detail.user.name, email: detail.user.email, phone: detail.user.phone || '' }
  } finally { loading.value = false }
}

onMounted(load)

async function saveEdit() {
  await updateMember(id, editForm.value)
  editing.value = false
  successMsg.value = 'Member updated.'
  load()
}

async function handleAssign() {
  assignError.value = ''
  if (!assignForm.value.membershipTypeId || !assignForm.value.startDate) {
    assignError.value = 'Please fill all fields'; return
  }
  try {
    await assignMembership(id, {
      membershipTypeId: Number(assignForm.value.membershipTypeId),
      startDate: assignForm.value.startDate,
      directActivate: assignForm.value.directActivate,
    })
    showAssign.value = false
    successMsg.value = 'Membership assigned.'
    load()
  } catch (e: any) { assignError.value = e.response?.data?.message || 'Failed to assign membership' }
}

function openActivateModal(mId: number) {
  activateModal.value = { membershipId: mId }
  activateStartDate.value = today()
}

async function confirmActivate() {
  if (!activateModal.value || !activateStartDate.value) return
  await activateMembership(activateModal.value.membershipId, activateStartDate.value)
  activateModal.value = null
  successMsg.value = 'Membership activated.'
  load()
}

async function handleCancel(mId: number) {
  if (!confirm('Cancel this membership?')) return
  await cancelMembership(mId)
  successMsg.value = 'Membership cancelled.'
  load()
}

function statusBadge(s: string) {
  return { ACTIVE: 'bg-emerald-50 text-emerald-700 border border-emerald-200', PENDING: 'bg-amber-50 text-amber-700 border border-amber-200', EXPIRED: 'bg-slate-100 text-slate-500 border border-slate-200', CANCELLED: 'bg-rose-50 text-rose-600 border border-rose-200' }[s] || 'bg-slate-100 text-slate-500'
}
</script>

<template>
  <div class="p-6 lg:p-8">
    <button @click="router.back()" class="inline-flex items-center gap-1.5 text-slate-500 hover:text-slate-900 text-sm font-medium mb-6 transition-colors">
      <ArrowLeft :size="15" /> Back
    </button>

    <div v-if="loading" class="flex items-center justify-center py-20">
      <div class="w-6 h-6 border-2 border-sky-500 border-t-transparent rounded-full animate-spin"></div>
    </div>

    <div v-else-if="data">
      <div v-if="successMsg" class="mb-5 bg-emerald-50 border border-emerald-200 text-emerald-700 px-4 py-3 rounded-xl text-sm font-medium flex items-center gap-2">
        <CheckCircle :size="15" class="text-emerald-500 flex-shrink-0" /> {{ successMsg }}
      </div>

      <div class="bg-white rounded-xl border border-slate-200 shadow-sm p-6 mb-6">
        <div class="flex items-start justify-between mb-5">
          <div>
            <h2 class="text-xl font-bold text-slate-900">{{ data.user.name }}</h2>
            <p class="text-sm text-slate-500 font-mono mt-0.5">Member #{{ data.user.id }}</p>
          </div>
          <button @click="editing = !editing"
            class="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-sm font-medium text-sky-600 hover:bg-sky-50 transition-colors">
            <Pencil :size="13" /> {{ editing ? 'Cancel' : 'Edit' }}
          </button>
        </div>

        <div v-if="!editing" class="grid grid-cols-2 gap-4 text-sm">
          <div><span class="text-slate-500 text-xs uppercase tracking-wide font-semibold">Email</span><p class="font-medium text-slate-900 mt-1">{{ data.user.email }}</p></div>
          <div><span class="text-slate-500 text-xs uppercase tracking-wide font-semibold">Phone</span><p class="font-medium text-slate-900 mt-1">{{ data.user.phone || '—' }}</p></div>
          <div><span class="text-slate-500 text-xs uppercase tracking-wide font-semibold">Joined</span><p class="font-medium text-slate-900 mt-1">{{ dayjs(data.user.createdAt).format('DD MMM YYYY') }}</p></div>
        </div>

        <form v-else @submit.prevent="saveEdit" class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Name</label>
            <input v-model="editForm.name" required class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Email</label>
            <input v-model="editForm.email" type="email" required class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
          </div>
          <div>
            <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Phone</label>
            <input v-model="editForm.phone" class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
          </div>
          <div class="flex items-end">
            <button type="submit" class="bg-sky-500 hover:bg-sky-600 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors">Save Changes</button>
          </div>
        </form>
      </div>

      <div class="bg-white rounded-xl border border-slate-200 shadow-sm">
        <div class="px-6 py-4 border-b border-slate-100 flex items-center justify-between">
          <h3 class="text-sm font-semibold text-slate-900">Memberships</h3>
          <button @click="showAssign = !showAssign"
            class="inline-flex items-center gap-1.5 bg-sky-500 hover:bg-sky-600 text-white px-3 py-1.5 rounded-lg text-xs font-semibold transition-colors">
            <Plus :size="13" /> Assign
          </button>
        </div>

        <div v-if="showAssign" class="px-6 py-5 bg-sky-50 border-b border-sky-100">
          <h4 class="text-sm font-semibold text-slate-800 mb-3">Assign New Membership</h4>
          <div class="grid grid-cols-3 gap-3">
            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Type</label>
              <select v-model="assignForm.membershipTypeId" class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white">
                <option value="">Select...</option>
                <option v-for="t in types.filter(t => t.isActive)" :key="t.id" :value="t.id">{{ t.name }} — {{ t.basePrice }} SAR</option>
              </select>
            </div>
            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Start Date</label>
              <input v-model="assignForm.startDate" type="date" class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
            </div>
            <div>
              <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Status</label>
              <select v-model="assignForm.directActivate" class="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 bg-white">
                <option :value="true">Activate immediately</option>
                <option :value="false">Save as Pending</option>
              </select>
            </div>
          </div>
          <p v-if="assignError" class="text-rose-500 text-sm mt-2">{{ assignError }}</p>
          <div class="flex gap-2 mt-3">
            <button @click="handleAssign" class="bg-sky-500 hover:bg-sky-600 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors">Assign</button>
            <button @click="showAssign = false" class="bg-slate-200 hover:bg-slate-300 text-slate-700 px-4 py-2 rounded-lg text-sm font-medium transition-colors">Cancel</button>
          </div>
        </div>

        <div v-if="data.memberships.length === 0" class="py-10 text-center text-slate-400 text-sm">No memberships yet.</div>

        <div class="divide-y divide-slate-50">
          <div v-for="m in data.memberships" :key="m.id" class="px-6 py-4">
            <div class="flex items-start justify-between">
              <div>
                <div class="flex items-center gap-2 mb-1.5">
                  <span class="font-semibold text-slate-900 text-sm">{{ m.membershipType.name }}</span>
                  <span :class="['text-xs px-2.5 py-0.5 rounded-full font-semibold', statusBadge(m.status)]">{{ m.status }}</span>
                </div>
                <p class="text-xs text-slate-500">
                  Start: {{ m.startDate ?? 'To be confirmed' }}
                  <template v-if="m.endDate"> · End: {{ m.endDate }}</template>
                  <template v-if="m.entriesRemaining != null"> · Entries: <strong class="text-sky-600">{{ m.entriesRemaining }}</strong></template>
                </p>
                <p class="text-xs text-slate-400 mt-0.5">Paid: {{ m.pricePaid }} SAR</p>
              </div>
              <div class="flex gap-2">
                <button v-if="m.status === 'PENDING'" @click="openActivateModal(m.id)"
                  class="text-xs bg-emerald-500 hover:bg-emerald-600 text-white px-3 py-1.5 rounded-lg font-semibold transition-colors">Activate</button>
                <button v-if="m.status === 'ACTIVE' || m.status === 'PENDING'" @click="handleCancel(m.id)"
                  class="text-xs bg-rose-50 hover:bg-rose-100 text-rose-600 border border-rose-200 px-3 py-1.5 rounded-lg font-medium transition-colors">Cancel</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <div v-if="activateModal" class="fixed inset-0 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-2xl p-6 w-full max-w-sm">
      <h2 class="text-lg font-bold text-slate-900 mb-4">Activate Membership</h2>
      <div class="mb-4">
        <label class="block text-xs font-semibold text-slate-500 uppercase tracking-wide mb-1.5">Start Date</label>
        <input v-model="activateStartDate" type="date" required class="w-full border border-slate-200 rounded-lg px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-sky-500" />
      </div>
      <div class="flex gap-3">
        <button @click="activateModal = null" class="flex-1 border border-slate-200 text-slate-700 py-2.5 rounded-lg text-sm font-medium hover:bg-slate-50 transition-colors">Cancel</button>
        <button @click="confirmActivate" :disabled="!activateStartDate" class="flex-1 bg-emerald-500 hover:bg-emerald-600 disabled:opacity-50 text-white py-2.5 rounded-lg text-sm font-semibold transition-colors">Confirm</button>
      </div>
    </div>
  </div>
</template>
