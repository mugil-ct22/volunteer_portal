import React, { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import Navbar from '../components/Navbar';
import DashboardCard from '../components/DashboardCard';
import { dashboardAPI } from '../services/api';
import './UserHome.css';

const UserHome = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const userStats = await dashboardAPI.getUserStats();
        setStats(userStats);
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
        <p>Loading dashboard...</p>
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
    <div className="user-home">
      <Navbar />
      
      <div className="dashboard-container">
        <div className="dashboard-header">
          <h1>Welcome back, {user?.name}!</h1>
          <p>Here's your volunteer activity overview</p>
        </div>
        
        <div className="dashboard-grid">
          <DashboardCard
            title="Total Events"
            value={stats.totalEvents}
            icon="📅"
            color="blue"
          />

          <DashboardCard
            title="Applied Events"
            value={stats.appliedEvents}
            icon="📝"
            color="yellow"
          />

          <DashboardCard
            title="Completed Events"
            value={stats.completedEvents}
            icon="✅"
            color="green"
          />

          <DashboardCard
            title="Rejected Events"
            value={stats.rejectedEvents}
            icon="❌"
            color="red"
          />

          <DashboardCard
            title="Total Points"
            value={stats.totalPoints}
            icon="🏆"
            color="purple"
          />
        </div>

        <div className="quick-actions-section">
          <h2>Quick Actions</h2>
          <div className="actions-grid">
            <div className="action-card">
              <div className="action-icon">📅</div>
              <h3>Browse Events</h3>
              <p>Find and register for volunteer opportunities</p>
              <button
                className="btn btn--primary"
                onClick={() => window.location.href = '/events'}
              >
                View Events
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon">📤</div>
              <h3>Submit Proof</h3>
              <p>Upload proof for your completed volunteer work</p>
              <button
                className="btn btn--primary"
                onClick={() => window.location.href = '/proof-uploads'}
              >
                Upload Proof
              </button>
            </div>

            <div className="action-card">
              <div className="action-icon">🏆</div>
              <h3>View Leaderboard</h3>
              <p>See how you rank among other volunteers</p>
              <button
                className="btn btn--primary"
                onClick={() => window.location.href = '/leaderboard'}
              >
                View Rankings
              </button>
            </div>
          </div>
        </div>
        
        <div className="dashboard-footer">
          <div className="motivational-section">
            <h2>Keep up the great work!</h2>
            <p>Your dedication to community service makes a real difference. Continue participating in events to earn more points and climb the leaderboard.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserHome;
