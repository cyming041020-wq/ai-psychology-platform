import axios from 'axios'

export const ACCESS_TOKEN_KEY = 'psychology_access_token'

const api = axios.create({
  baseURL: '/api',
  timeout: 10_000,
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(ACCESS_TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem(ACCESS_TOKEN_KEY)
      localStorage.removeItem('psychology_user')
      if (typeof window !== 'undefined') {
        window.dispatchEvent(new Event('psychology:unauthorized'))
      }
    }
    return Promise.reject(error)
  },
)

export default api
