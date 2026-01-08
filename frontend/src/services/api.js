import api from '../api/axiosConfig';

/* =====================================================
   AUTH API
===================================================== */
export const authAPI = {
  login: async (usernameOrEmail, password) => {
    const response = await api.post('/api/auth/login', {
      username: usernameOrEmail,
      password,
    });
    return response.data;
  },

  register: async (userData) => {
    const response = await api.post('/api/auth/register', userData);
    return response.data;
  }
};

/* =====================================================
   EVENTS API
===================================================== */
export const eventsAPI = {
  getAllEvents: async () =>
    (await api.get('/api/events')).data,

  getEventsByCategory: async (category) =>
    (await api.get(`/api/events/category/${encodeURIComponent(category)}`)).data,

  getAllCategories: async () =>
    (await api.get('/api/events/categories')).data,

  createEvent: async (eventData) =>
    (await api.post('/api/admin/events', eventData)).data,

  updateEvent: async (id, eventData) =>
    (await api.put(`/api/admin/events/${id}`, eventData)).data,

  deleteEvent: async (id) => {
    await api.delete(`/api/admin/events/${id}`);
    return true;
  },

  registerForEvent: async (eventId) => {
    await api.post(`/api/events/register/${eventId}`);
    return true;
  },

  unregisterFromEvent: async (eventId) => {
    await api.delete(`/api/events/unregister/${eventId}`);
    return true;
  },

  getRegisteredEvents: async () =>
    (await api.get('/api/events/registered')).data
};

/* =====================================================
   PROOFS API
===================================================== */
export const proofsAPI = {
  getUserProofs: async () =>
    (await api.get('/api/proof/user')).data,

  getAllProofs: async () =>
    (await api.get('/api/admin/proofs')).data,

  uploadProof: async (eventId, file) => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post(
      `/api/proof/upload/${eventId}`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
    return response.data;
  },

  approveProof: async (proofId) =>
    (await api.put(`/api/admin/proofs/${proofId}/approve`)).data,

  rejectProof: async (proofId, reason) =>
    (await api.put(`/api/admin/proofs/${proofId}/reject`, null, {
      params: { reason }
    })).data,

  regenerateCertificate: async (proofId) =>
    (await api.put(`/api/admin/proofs/${proofId}/regenerate-certificate`)).data,

  deleteProof: async (proofId) =>
    api.delete(`/api/proof/${proofId}`)
};

/* =====================================================
   LEADERBOARD API
===================================================== */
export const leaderboardAPI = {
  getLeaderboard: async () =>
    (await api.get('/api/leaderboard')).data,

  recalculatePoints: async () =>
    (await api.post('/api/leaderboard/recalculate')).data
};

/* =====================================================
   DASHBOARD API
===================================================== */
export const dashboardAPI = {
  getUserStats: async () =>
    (await api.get('/api/user/dashboard')).data,

  getAdminStats: async () =>
    (await api.get('/api/admin/dashboard')).data
};

/* =====================================================
   USERS API
===================================================== */
export const usersAPI = {
  getAllUsers: async () =>
    (await api.get('/api/admin/users')).data
};

export default api;
