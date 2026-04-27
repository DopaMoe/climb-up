import api from './axios'

export const getMe = (userId: number) =>
  api.get('/api/member/me', { params: { userId } }).then(r => r.data)

export const getMembershipTypes = () =>
  api.get('/api/member/membership-types').then(r => r.data)

export const requestMembership = (userId: number, data: {
  membershipTypeId: number
  startDate: string
  directActivate: boolean
}) => api.post('/api/member/memberships/request', data, { params: { userId } }).then(r => r.data)

export const getCheckIns = (userId: number, page = 0) =>
  api.get('/api/member/check-ins', { params: { userId, page } }).then(r => r.data)
