import api from '../api/axiosConfig';

// Authentication API
export const authAPI = {
  login: async (usernameOrEmail, password) => {
    try {
      const response = await api.post('/api/auth/login', { username: usernameOrEmail, password });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Login failed');
    }
  },

  register: async (userData) => {
    try {
      console.log('API Register - Sending data:', userData);
      console.log('API Register - URL:', '/api/auth/register');
      const response = await api.post('/api/auth/register', userData);
      console.log('API Register - Response:', response.data);
      return response.data;
    } catch (error) {
      console.error('API Register - Error:', error);
      console.error('API Register - Error response:', error.response);
      throw new Error(error.response?.data?.message || 'Registration failed');
    }
  }
};

// Events API
export const eventsAPI = {
  getAllEvents: async () => {
    try {
      const response = await api.get('/api/events');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch events');
    }
  },

  getEventsByCategory: async (category) => {
    try {
      const response = await api.get(`/api/events/category/${encodeURIComponent(category)}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch events by category');
    }
  },

  getAllCategories: async () => {
    try {
      const response = await api.get('/api/events/categories');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch categories');
    }
  },

  createEvent: async (eventData) => {
    try {
      const response = await api.post('/api/admin/events', eventData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create event');
    }
  },

  updateEvent: async (id, eventData) => {
    try {
      const response = await api.put(`/api/admin/events/${id}`, eventData);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update event');
    }
  },

  deleteEvent: async (id) => {
    try {
      await api.delete(`/api/admin/events/${id}`);
      return true;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to delete event');
    }
  },

  registerForEvent: async (eventId) => {
    try {
      await api.post(`/api/events/register/${eventId}`);
      return true;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to register for event');
    }
  },

  unregisterFromEvent: async (eventId) => {
    try {
      await api.delete(`/api/events/unregister/${eventId}`);
      return true;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to unregister from event');
    }
  },

  getRegisteredEvents: async () => {
    try {
      const response = await api.get('/api/events/registered');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch registered events');
    }
  }
};

// Proofs API
export const proofsAPI = {
  getUserProofs: async () => {
    const response = await api.get('/api/proof/user');
    return response.data;
  },

  getAllProofs: async () => {
    const response = await api.get('/api/admin/proofs');
    return response.data;
  },

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

  approveProof: async (proofId) => {
    const response = await api.put(`/api/admin/proofs/${proofId}/approve`);
    return response.data;
  },

  rejectProof: async (proofId, reason) => {
    const response = await api.put(`/api/admin/proofs/${proofId}/reject?reason=${encodeURIComponent(reason)}`);
    return response.data;
  },

  regenerateCertificate: async (proofId) => {
    const response = await api.put(`/api/admin/proofs/${proofId}/regenerate-certificate`);
    return response.data;
  },

  deleteProof: async (proofId) => {
    await api.delete(`/api/proof/${proofId}`);
  }
};


// Leaderboard API
export const leaderboardAPI = {
  getLeaderboard: async () => {
    try {
      const response = await api.get('/api/leaderboard');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch leaderboard');
    }
  },

  recalculatePoints: async () => {
    try {
      const response = await api.post('/api/leaderboard/recalculate');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to recalculate points');
    }
  }
};



// Dashboard API
export const dashboardAPI = {
  getUserStats: async () => {
    try {
      const response = await api.get('/api/user/dashboard');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch user dashboard');
    }
  },

  getAdminStats: async () => {
    try {
      const response = await api.get('/api/admin/dashboard');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch admin dashboard');
    }
  }
};

// Notifications API
export const notificationsAPI = {
  getNotificationsForUser: async (userId) => {
    try {
      const response = await api.get(`/api/notifications/user/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch notifications');
    }
  },

  getUnreadNotificationsForUser: async (userId) => {
    try {
      const response = await api.get(`/api/notifications/user/${userId}/unread`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch unread notifications');
    }
  },

  getUnreadNotificationCount: async (userId) => {
    try {
      const response = await api.get(`/api/notifications/user/${userId}/unread/count`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch notification count');
    }
  },

  sendNotification: async (title, message, senderId, receiverId, notificationType) => {
    try {
      const response = await api.post('/api/notifications', null, {
        params: { title, message, senderId, receiverId, notificationType }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to send notification');
    }
  },

  markAsRead: async (notificationId, userId) => {
    try {
      await api.put(`/api/notifications/${notificationId}/read`, null, {
        params: { userId }
      });
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to mark notification as read');
    }
  },

  markAllAsRead: async (userId) => {
    try {
      await api.put(`/api/notifications/user/${userId}/read-all`);
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to mark all notifications as read');
    }
  },

  deleteNotification: async (notificationId, userId) => {
    try {
      await api.delete(`/api/notifications/${notificationId}`, {
        params: { userId }
      });
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to delete notification');
    }
  }
};

// Meetings API
export const meetingsAPI = {
  getMeetingsForUser: async (userId) => {
    try {
      const response = await api.get(`/api/meetings/user/${userId}`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch meetings');
    }
  },

  getUpcomingMeetingsForUser: async (userId) => {
    try {
      const response = await api.get(`/api/meetings/user/${userId}/upcoming`);
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch upcoming meetings');
    }
  },

  createMeeting: async (title, description, meetingDate, durationMinutes, organizerId, participantId, meetingLink) => {
    try {
      const response = await api.post('/api/meetings', null, {
        params: { title, description, meetingDate, durationMinutes, organizerId, participantId, meetingLink }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to create meeting');
    }
  },

  updateMeeting: async (meetingId, title, description, meetingDate, durationMinutes, status, meetingLink, userId) => {
    try {
      const response = await api.put(`/api/meetings/${meetingId}`, null, {
        params: { title, description, meetingDate, durationMinutes, status, meetingLink, userId }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to update meeting');
    }
  },

  deleteMeeting: async (meetingId, userId) => {
    try {
      await api.delete(`/api/meetings/${meetingId}`, {
        params: { userId }
      });
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to delete meeting');
    }
  },

  respondToMeetingInvitation: async (meetingId, accepted, userId) => {
    try {
      const response = await api.put(`/api/meetings/${meetingId}/respond`, null, {
        params: { accepted, userId }
      });
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to respond to meeting invitation');
    }
  }
};

// Users API
export const usersAPI = {
  getAllUsers: async () => {
    try {
      const response = await api.get('/api/admin/users');
      return response.data;
    } catch (error) {
      throw new Error(error.response?.data?.message || 'Failed to fetch users');
    }
  }
};



export default api;
