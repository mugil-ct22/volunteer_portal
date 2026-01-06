import React, { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import Navbar from '../components/Navbar';
import DashboardCard from '../components/DashboardCard';
import { dashboardAPI } from '../services/api';
import './AdminHome.css';

const AdminHome = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const adminStats = await dashboardAPI.getAdminStats();
        setStats(adminStats);
      } catch (err) {
        setError('Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading admin dashboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <p>{error}</p>
      </div>
    );
  }

  return (
    <div className="admin-home">
      <Navbar />
      
      <div className="admin-dashboard-container">
        <div className="dashboard-header">
          <h1>Admin Dashboard</h1>
          <p>Welcome back, {user?.name}! Manage the volunteer portal efficiently.</p>
        </div>
        
        <div className="dashboard-grid">
          <DashboardCard
            title="Total Volunteers"
            value={stats.totalVolunteers}
            icon="👥"
            color="blue"
          />
          
          <DashboardCard
            title="Total Events"
            value={stats.totalEvents}
            icon="📅"
            color="green"
          />
          
          <DashboardCard
            title="Pending Approvals"
            value={stats.pendingApprovals}
            icon="⏳"
            color="yellow"
          />
        </div>
        
        <div className="admin-actions-section">
          <h2>Quick Actions</h2>
          <div className="actions-grid">
            <div className="action-card">
              <div className="action-icon">➕</div>
              <h3>Create New Event</h3>
              <p>Add a new volunteer opportunity for the community</p>
              <button 
                className="btn btn--primary"
                onClick={() => window.location.href = '/admin/events'}
              >
                Manage Events
              </button>
            </div>
            
            <div className="action-card">
              <div className="action-icon">✅</div>
              <h3>Review Proofs</h3>
              <p>Approve or reject volunteer proof submissions</p>
              <button 
                className="btn btn--primary"
                onClick={() => window.location.href = '/admin/proof-approval'}
              >
                Review Proofs
              </button>
            </div>
            

          </div>
        </div>
        
        <div className="admin-footer">
          <div className="admin-info">
            <h2>Administrator Panel</h2>
            <p>You have full access to manage all aspects of the volunteer portal. Use your privileges responsibly to maintain a positive and engaging community experience.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminHome;
