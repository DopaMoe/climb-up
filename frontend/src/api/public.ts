import api from './axios'

export interface RegisterRequest {
  name: string
  email: string
  phone?: string
  membershipTypeId: number
}

export const registerMember = (data: RegisterRequest) =>
  api.post('/api/public/register', data).then(r => r.data)
