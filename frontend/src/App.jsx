import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './auth/AuthContext';
import Login from './auth/Login';
import Register from './auth/Register';
import ProtectedRoute from './components/ProtectedRoute';
import AdminRoute from './components/AdminRoute';

// User Pages
import UserHome from './pages/UserHome';
import UserEvents from './pages/UserEvents';
import ProofUploads from './pages/ProofUploads';
import Leaderboard from './pages/Leaderboard';


// Admin Pages
import AdminHome from './admin/AdminHome';
import AdminEvents from './admin/AdminEvents';
import AdminProofApproval from './admin/AdminProofApproval';


import './styles/global.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            
            {/* Redirect root to login */}
            <Route path="/" element={<Navigate to="/login" replace />} />
            
            {/* User Routes */}
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <UserHome />
              </ProtectedRoute>
            } />
            
            <Route path="/events" element={
              <ProtectedRoute>
                <UserEvents />
              </ProtectedRoute>
            } />
            
            <Route path="/proof-uploads" element={
              <ProtectedRoute>
                <ProofUploads />
              </ProtectedRoute>
            } />

            <Route path="/leaderboard" element={
              <ProtectedRoute>
                <Leaderboard />
              </ProtectedRoute>
            } />


            
            {/* Admin Routes */}
            <Route path="/admin" element={
              <AdminRoute>
                <AdminHome />
              </AdminRoute>
            } />
            
            <Route path="/admin/dashboard" element={
              <AdminRoute>
                <AdminHome />
              </AdminRoute>
            } />
            
            <Route path="/admin/events" element={
              <AdminRoute>
                <AdminEvents />
              </AdminRoute>
            } />
            
            <Route path="/admin/proof-approval" element={
              <AdminRoute>
                <AdminProofApproval />
              </AdminRoute>
            } />



            {/* Legacy routes - redirect to new paths */}
            <Route path="/user/dashboard" element={<Navigate to="/dashboard" replace />} />
            <Route path="/user/events" element={<Navigate to="/events" replace />} />
            <Route path="/user/proof-uploads" element={<Navigate to="/proof-uploads" replace />} />
            <Route path="/user/leaderboard" element={<Navigate to="/leaderboard" replace />} />
            
            {/* Catch all route - redirect to login */}
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
