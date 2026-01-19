import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

export const claimService = {
  createClaim: (claimData) => api.post('/claims', claimData),
  getClaim: (id) => api.get(`/claims/${id}`),
  getClaimsByCustomer: (customerId) => api.get(`/claims/customer/${customerId}`),
  getAllClaims: () => api.get('/claims'),
  updateClaim: (id, claimData) => api.put(`/claims/${id}`, claimData),
  addComment: (claimId, comment) => api.post(`/claims/${claimId}/comments`, comment),
  getComments: (claimId) => api.get(`/claims/${claimId}/comments`),
  updateStatus: (id, status) => api.patch(`/claims/${id}/status?status=${status}`),
}

export const workflowService = {
  getClaimDetails: (id) => api.get(`/workflow/claims/${id}`),
  approveClaim: (approvalRequest) => api.post('/workflow/approve', approvalRequest),
  assignSupervisor: (claimId, supervisorId) => 
    api.post(`/workflow/claims/${claimId}/assign?supervisorId=${supervisorId}`),
}

export default api

