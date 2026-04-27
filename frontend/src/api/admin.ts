import api from './axios'

// Dashboard
export const getDashboard = () => api.get('/api/admin/dashboard').then(r => r.data)

// Members
export const getMembers = (search?: string, page = 0) =>
  api.get('/api/admin/members', { params: { search, page } }).then(r => r.data)

export const getMember = (id: number) =>
  api.get(`/api/admin/members/${id}`).then(r => r.data)

export const createMember = (data: { name: string; email: string; phone?: string }) =>
  api.post('/api/admin/members', data).then(r => r.data)

export const updateMember = (id: number, data: { name: string; email: string; phone?: string }) =>
  api.put(`/api/admin/members/${id}`, data).then(r => r.data)

export const deleteMember = (id: number) =>
  api.delete(`/api/admin/members/${id}`)

// Memberships
export const assignMembership = (userId: number, data: {
  membershipTypeId: number
  startDate: string
  directActivate: boolean
}) => api.post(`/api/admin/members/${userId}/memberships`, data).then(r => r.data)

export const activateMembership = (id: number) =>
  api.put(`/api/admin/memberships/${id}/activate`).then(r => r.data)

export const cancelMembership = (id: number) =>
  api.put(`/api/admin/memberships/${id}/cancel`).then(r => r.data)

// Membership Types
export const getMembershipTypes = () =>
  api.get('/api/admin/membership-types').then(r => r.data)

export const getMembershipType = (id: number) =>
  api.get(`/api/admin/membership-types/${id}`).then(r => r.data)

export const createMembershipType = (data: object) =>
  api.post('/api/admin/membership-types', data).then(r => r.data)

export const updateMembershipType = (id: number, data: object) =>
  api.put(`/api/admin/membership-types/${id}`, data).then(r => r.data)

export const deactivateMembershipType = (id: number) =>
  api.delete(`/api/admin/membership-types/${id}`)

// Check-in
export const checkIn = (userId: number) =>
  api.post('/api/admin/check-in', { userId }).then(r => r.data)

// Discounts
export const getDiscounts = () =>
  api.get('/api/admin/discounts').then(r => r.data)

export const updateDiscounts = (data: object) =>
  api.put('/api/admin/discounts', data).then(r => r.data)
