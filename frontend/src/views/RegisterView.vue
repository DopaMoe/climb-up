<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { getMembershipTypes } from '../api/member'
import { registerMember } from '../api/public'

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
  <div class="min-h-screen bg-gradient-to-br from-gray-900 to-blue-900 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-2xl w-full max-w-md p-8">

      <!-- Success state -->
      <div v-if="result" class="text-center space-y-4">
        <div class="text-5xl">🎉</div>
        <h2 class="text-2xl font-bold text-gray-900">Registration Submitted!</h2>
        <p class="text-gray-600">Welcome, <strong>{{ result.name }}</strong>. Your <em>{{ result.membershipTypeName }}</em> membership is pending activation.</p>
        <div class="bg-blue-50 border border-blue-200 rounded-xl p-4 space-y-1">
          <p class="text-sm text-blue-700 font-medium">Your Member ID</p>
          <p class="text-4xl font-bold text-blue-600">#{{ result.userId }}</p>
          <p class="text-xs text-blue-500">Save this ID — you'll need it to log in.</p>
        </div>
        <p class="text-sm text-gray-500">Visit the gym to pay and your membership will be activated.</p>
        <button @click="goToDashboard"
          class="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 transition-colors">
          Go to My Dashboard
        </button>
      </div>

      <!-- Registration form -->
      <div v-else>
        <div class="text-center mb-6">
          <div class="text-5xl mb-3">🧗</div>
          <h1 class="text-2xl font-bold text-gray-900">Join ClimbUp</h1>
          <p class="text-gray-500 mt-1 text-sm">Register and pick your membership</p>
        </div>

        <form @submit.prevent="submit" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
            <input v-model="form.name" required type="text" placeholder="Your name"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Email</label>
            <input v-model="form.email" required type="email" placeholder="you@example.com"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Phone <span class="text-gray-400">(optional)</span></label>
            <input v-model="form.phone" type="tel" placeholder="05XXXXXXXX"
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Membership Plan</label>
            <select v-model="form.membershipTypeId" required
              class="w-full border border-gray-300 rounded-lg px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500">
              <option v-for="plan in plans" :key="plan.id" :value="plan.id">
                {{ plan.name }} — {{ plan.effectivePrice }} SAR
              </option>
            </select>
            <p class="text-xs text-gray-400 mt-1">Start date will be confirmed when you pay at the gym.</p>
          </div>

          <p v-if="error" class="text-red-500 text-sm">{{ error }}</p>

          <button type="submit" :disabled="loading"
            class="w-full bg-blue-600 text-white py-3 rounded-xl font-semibold hover:bg-blue-700 disabled:opacity-50 transition-colors">
            {{ loading ? 'Submitting...' : 'Register' }}
          </button>
        </form>

        <p class="text-center text-sm text-gray-500 mt-4">
          Already have an account?
          <RouterLink to="/login" class="text-blue-600 hover:underline">Log in</RouterLink>
        </p>
      </div>

    </div>
  </div>
</template>
