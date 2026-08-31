import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

import api, { ACCESS_TOKEN_KEY } from '../services/api'
import type { LoginRequest, LoginResponse, UserProfile } from '../types/auth'

const USER_KEY = 'psychology_user'

function readStoredUser(): UserProfile | null {
  const stored = localStorage.getItem(USER_KEY)
  if (!stored) {
    return null
  }

  try {
    return JSON.parse(stored) as UserProfile
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem(ACCESS_TOKEN_KEY))
  const user = ref<UserProfile | null>(readStoredUser())
  const isAuthenticated = computed(() => Boolean(token.value))

  async function login(credentials: LoginRequest) {
    const { data } = await api.post<LoginResponse>('/auth/login', credentials)
    token.value = data.accessToken
    user.value = data.user
    localStorage.setItem(ACCESS_TOKEN_KEY, data.accessToken)
    localStorage.setItem(USER_KEY, JSON.stringify(data.user))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, user, isAuthenticated, login, logout }
})
