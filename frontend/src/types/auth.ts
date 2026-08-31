export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest extends LoginRequest {
  displayName?: string
}
export interface UserProfile {
  id: number
  username: string
  displayName: string
  role: string
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: UserProfile
}
