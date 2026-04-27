import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export type Role = 'ADMIN' | 'MEMBER'

export const useAuthStore = defineStore('auth', () => {
  const role = ref<Role | null>(localStorage.getItem('role') as Role | null)
  const userId = ref<number | null>(
    localStorage.getItem('userId') ? Number(localStorage.getItem('userId')) : null
  )

  const isAdmin = computed(() => role.value === 'ADMIN')
  const isMember = computed(() => role.value === 'MEMBER')
  const isLoggedIn = computed(() => role.value !== null)

  function enterAs(r: Role, id?: number) {
    role.value = r
    localStorage.setItem('role', r)
    if (id !== undefined) {
      userId.value = id
      localStorage.setItem('userId', String(id))
    }
  }

  function logout() {
    role.value = null
    userId.value = null
    localStorage.removeItem('role')
    localStorage.removeItem('userId')
  }

  return { role, userId, isAdmin, isMember, isLoggedIn, enterAs, logout }
})
