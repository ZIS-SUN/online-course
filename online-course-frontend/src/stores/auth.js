import { defineStore } from 'pinia'
import { login, register, getMe } from '../api/auth'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.user?.role === 'ADMIN',
    currentUser: (state) => state.user
  },

  actions: {
    async login(credentials) {
      try {
        const response = await login(credentials)
        this.token = response.data.token
        this.user = response.data.user
        localStorage.setItem('token', this.token)
        return response
      } catch (error) {
        throw error
      }
    },

    async register(userData) {
      try {
        const response = await register(userData)
        this.token = response.data.token
        this.user = response.data.user
        localStorage.setItem('token', this.token)
        return response
      } catch (error) {
        throw error
      }
    },

    async fetchCurrentUser() {
      if (!this.token) return null
      try {
        const response = await getMe()
        this.user = response.data
        return response.data
      } catch (error) {
        this.logout()
        throw error
      }
    },

    logout() {
      this.user = null
      this.token = null
      localStorage.removeItem('token')
    }
  }
})