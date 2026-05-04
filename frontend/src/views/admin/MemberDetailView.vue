<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMember, updateMember, assignMembership, activateMembership, cancelMembership, getMembershipTypes } from '../../api/admin'
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
  } finally {
    loading.value = false
  }
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
    assignError.value = 'Please fill all fields'
    return
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
  } catch (e: any) {
    assignError.value = e.response?.data?.message || 'Failed to assign membership'
  }
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

function statusColor(s: string) {
  return { ACTIVE: 'bg-green-100 text-green-700', PENDING: 'bg-yellow-100 text-yellow-700',
    EXPIRED: 'bg-gray-100 text-gray-500', CANCELLED: 'bg-red-100 text-red-600' }[s] || ''
}
</script>

<template>
  <div class="p-8">
    <button @click="router.back()" class="text-gray-500 hover:text-gray-700 text-sm mb-4 flex items-center gap-1">
      ← Back
    </button>

    <div v-if="loading" class="text-gray-400">Loading...</div>

    <div v-else-if="data">
      <div v-if="successMsg" class="mb-4 bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg text-sm">
        {{ successMsg }}
      </div>

      <!-- Member info card -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-6 mb-6">
        <div class="flex items-start justify-between mb-4">
          <div>
            <h2 class="text-xl font-bold text-gray-900">{{ data.user.name }}</h2>
            <p class="text-gray-500 text-sm">Member #{{ data.user.id }}</p>
          </div>
          <button @click="editing = !editing"
            class="text-sm text-blue-600 hover:text-blue-800 font-medium">
            {{ editing ? 'Cancel' : 'Edit' }}
          </button>
        </div>

        <div v-if="!editing" class="grid grid-cols-2 gap-4 text-sm">
          <div><span class="text-gray-500">Email:</span> <span class="ml-2 font-medium">{{ data.user.email }}</span></div>
          <div><span class="text-gray-500">Phone:</span> <span class="ml-2 font-medium">{{ data.user.phone || '-' }}</span></div>
          <div><span class="text-gray-500">Joined:</span> <span class="ml-2">{{ dayjs(data.user.createdAt).format('DD MMM YYYY') }}</span></div>
        </div>

        <form v-else @submit.prevent="saveEdit" class="grid grid-cols-2 gap-4 text-sm">
          <div>
            <label class="text-gray-500 block mb-1">Name</label>
            <input v-model="editForm.name" required class="w-full border border-gray-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="text-gray-500 block mb-1">Email</label>
            <input v-model="editForm.email" type="email" required class="w-full border border-gray-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="text-gray-500 block mb-1">Phone</label>
            <input v-model="editForm.phone" class="w-full border border-gray-200 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div class="flex items-end">
            <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">Save Changes</button>
          </div>
        </form>
      </div>

      <!-- Memberships -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-100">
        <div class="p-5 border-b border-gray-100 flex items-center justify-between">
          <h3 class="font-semibold text-gray-900">Memberships</h3>
          <button @click="showAssign = !showAssign"
            class="bg-blue-600 text-white px-3 py-1.5 rounded-lg text-sm font-medium hover:bg-blue-700">
            + Assign Membership
          </button>
        </div>

        <!-- Assign form -->
        <div v-if="showAssign" class="p-5 bg-blue-50 border-b border-blue-100">
          <h4 class="font-medium text-gray-800 mb-3">Assign New Membership</h4>
          <div class="grid grid-cols-3 gap-3">
            <div>
              <label class="text-xs text-gray-500 block mb-1">Membership Type</label>
              <select v-model="assignForm.membershipTypeId" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option value="">Select type...</option>
                <option v-for="t in types.filter(t => t.isActive)" :key="t.id" :value="t.id">
                  {{ t.name }} — {{ t.basePrice }} SAR
                </option>
              </select>
            </div>
            <div>
              <label class="text-xs text-gray-500 block mb-1">Start Date</label>
              <input v-model="assignForm.startDate" type="date" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
            </div>
            <div>
              <label class="text-xs text-gray-500 block mb-1">Status</label>
              <select v-model="assignForm.directActivate" class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
                <option :value="true">Activate immediately</option>
                <option :value="false">Save as Pending</option>
              </select>
            </div>
          </div>
          <p v-if="assignError" class="text-red-500 text-sm mt-2">{{ assignError }}</p>
          <div class="flex gap-2 mt-3">
            <button @click="handleAssign" class="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium hover:bg-blue-700">Assign</button>
            <button @click="showAssign = false" class="bg-gray-200 text-gray-700 px-4 py-2 rounded-lg text-sm font-medium hover:bg-gray-300">Cancel</button>
          </div>
        </div>

        <div v-if="data.memberships.length === 0" class="p-5 text-gray-400 text-sm">No memberships yet.</div>

        <div class="divide-y divide-gray-50">
          <div v-for="m in data.memberships" :key="m.id" class="p-5">
            <div class="flex items-start justify-between">
              <div>
                <div class="flex items-center gap-2 mb-1">
                  <span class="font-medium text-gray-900">{{ m.membershipType.name }}</span>
                  <span :class="['text-xs px-2 py-0.5 rounded-full font-medium', statusColor(m.status)]">{{ m.status }}</span>
                </div>
                <p class="text-sm text-gray-500">
                  Start: {{ m.startDate ?? 'To be confirmed' }}
                  <template v-if="m.endDate"> · End: {{ m.endDate }}</template>
                  <template v-if="m.entriesRemaining != null"> · Entries left: {{ m.entriesRemaining }}</template>
                  <template v-if="m.entriesExpiryDate"> · Expires: {{ m.entriesExpiryDate }}</template>
                </p>
                <p class="text-sm text-gray-400 mt-0.5">Paid: {{ m.pricePaid }} SAR</p>
              </div>
              <div class="flex gap-2">
                <button v-if="m.status === 'PENDING'" @click="openActivateModal(m.id)"
                  class="text-xs bg-green-500 text-white px-3 py-1.5 rounded-lg hover:bg-green-600 font-medium">
                  Activate
                </button>
                <button v-if="m.status === 'ACTIVE' || m.status === 'PENDING'"
                  @click="handleCancel(m.id)"
                  class="text-xs bg-red-100 text-red-600 px-3 py-1.5 rounded-lg hover:bg-red-200 font-medium">
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Activate membership modal -->
  <div v-if="activateModal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
    <div class="bg-white rounded-2xl shadow-2xl p-8 w-full max-w-sm">
      <h2 class="text-lg font-bold text-gray-900 mb-4">Activate Membership</h2>
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">Start Date</label>
        <input v-model="activateStartDate" type="date" required
          class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-green-500" />
      </div>
      <div class="flex gap-3">
        <button @click="activateModal = null"
          class="flex-1 border border-gray-300 text-gray-700 py-2 rounded-lg text-sm hover:bg-gray-50 transition-colors">
          Cancel
        </button>
        <button @click="confirmActivate" :disabled="!activateStartDate"
          class="flex-1 bg-green-600 text-white py-2 rounded-lg text-sm font-medium hover:bg-green-700 disabled:opacity-50 transition-colors">
          Confirm
        </button>
      </div>
    </div>
  </div>
</template>
