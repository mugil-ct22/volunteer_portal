import React from 'react';
import { useAuth } from '../auth/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
  const { user, currentRole, logout, switchRole } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSwitchRole = () => {
    switchRole(navigate);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userNavItems = [
    { path: '/dashboard', label: 'Home' },
    { path: '/events', label: 'Events' },
    { path: '/proof-uploads', label: 'Proof Uploads' },
    { path: '/leaderboard', label: 'Leaderboard' },
  ];

  const adminNavItems = [
    { path: '/admin', label: 'Home' },
    { path: '/admin/events', label: 'Events' },
    { path: '/admin/proof-approval', label: 'Proof Approval' },
  ];

  const navItems = currentRole === 'ADMIN' ? adminNavItems : userNavItems;

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <h2>Volunteer Portal</h2>
        </div>
        
        <ul className="navbar-menu">
          {navItems.map((item) => (
            <li key={item.path}>
              <a
                href={item.path}
                className={`nav-link ${location.pathname === item.path ? 'active' : ''}`}
                onClick={(e) => {
                  e.preventDefault();
                  navigate(item.path);
                }}
              >
                {item.label}
              </a>
            </li>
          ))}
        </ul>
        
        <div className="navbar-user">
          <span className="user-name">Welcome, {user?.name}</span>
          {user?.role === 'ADMIN' && (
            <button onClick={handleSwitchRole} className="switch-role-button">
              Switch to {currentRole === 'ADMIN' ? 'User' : 'Admin'}
            </button>
          )}
          <button onClick={handleLogout} className="logout-button">
            Logout
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
