import React, { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import Navbar from '../components/Navbar';
import { proofsAPI } from '../services/api';
import './AdminProofApproval.css';

const AdminProofApproval = () => {
  const { user } = useAuth();
  const [proofs, setProofs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [processing, setProcessing] = useState(null);
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    fetchProofs();
  }, []);

  const fetchProofs = async () => {
    try {
      const proofsData = await proofsAPI.getAllProofs();

      // Sort proofs: PENDING first, then APPROVED, then REJECTED
      // Within each status group, sort by submittedAt DESC (newest first)
      const sortedProofs = proofsData.sort((a, b) => {
        const statusOrder = { 'PENDING': 0, 'APPROVED': 1, 'REJECTED': 2 };
        const statusDiff = statusOrder[a.status] - statusOrder[b.status];
        if (statusDiff !== 0) return statusDiff;

        // If same status, sort by submittedAt DESC
        return new Date(b.submittedAt) - new Date(a.submittedAt);
      });

      setProofs(sortedProofs);
    } catch (err) {
      setError('Failed to load proofs');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (proofId) => {
    setProcessing(proofId);
    try {
      await proofsAPI.approveProof(proofId);
      setProofs(proofs.map(proof => 
        proof.id === proofId 
          ? { ...proof, status: 'APPROVED', pointsAwarded: proof.pointsAwarded || 50 }
          : proof
      ));
      alert('Proof approved successfully!');
    } catch (err) {
      alert('Failed to approve proof');
    } finally {
      setProcessing(null);
    }
  };

  const handleReject = async (proofId) => {
    const reason = window.prompt('Please provide a reason for rejection:');
    if (!reason || reason.trim() === '') {
      alert('Rejection reason is required');
      return;
    }
    setProcessing(proofId);
    try {
      await proofsAPI.rejectProof(proofId, reason);
      setProofs(proofs.map(proof =>
        proof.id === proofId
          ? { ...proof, status: 'REJECTED', rejectionReason: reason }
          : proof
      ));
      alert('Proof rejected successfully!');
    } catch (err) {
      alert('Failed to reject proof');
    } finally {
      setProcessing(null);
    }
  };

  const handleRegenerateCertificate = async (proofId) => {
    if (!window.confirm('Are you sure you want to regenerate the certificate for this proof?')) {
      return;
    }
    setProcessing(proofId);
    try {
      await proofsAPI.regenerateCertificate(proofId);
      alert('Certificate regenerated successfully!');
    } catch (err) {
      alert('Failed to regenerate certificate');
    } finally {
      setProcessing(null);
    }
  };

  const getStatusBadgeClass = (status) => {
    switch (status) {
      case 'APPROVED':
        return 'status-badge status-badge--approved';
      case 'REJECTED':
        return 'status-badge status-badge--rejected';
      case 'PENDING':
      default:
        return 'status-badge status-badge--pending';
    }
  };

  const filteredProofs = proofs.filter(proof => {
    if (filter === 'ALL') return true;
    return proof.status === filter;
  });

  const pendingCount = proofs.filter(p => p.status === 'PENDING').length;

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading proof approvals...</p>
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
    <div className="admin-proof-approval">
      <Navbar />
      
      <div className="proof-approval-container">
        <div className="approval-header">
          <h1>Proof Approval</h1>
          <p>Review and manage volunteer proof submissions</p>
          
          {pendingCount > 0 && (
            <div className="pending-badge">
              ⏳ {pendingCount} pending approval{pendingCount !== 1 ? 's' : ''}
            </div>
          )}
        </div>

        <div className="filter-section">
          <div className="filter-buttons">
            <button 
              className={`filter-btn ${filter === 'ALL' ? 'active' : ''}`}
              onClick={() => setFilter('ALL')}
            >
              All ({proofs.length})
            </button>
            <button 
              className={`filter-btn ${filter === 'PENDING' ? 'active' : ''}`}
              onClick={() => setFilter('PENDING')}
            >
              Pending ({pendingCount})
            </button>
            <button 
              className={`filter-btn ${filter === 'APPROVED' ? 'active' : ''}`}
              onClick={() => setFilter('APPROVED')}
            >
              Approved ({proofs.filter(p => p.status === 'APPROVED').length})
            </button>
            <button 
              className={`filter-btn ${filter === 'REJECTED' ? 'active' : ''}`}
              onClick={() => setFilter('REJECTED')}
            >
              Rejected ({proofs.filter(p => p.status === 'REJECTED').length})
            </button>
          </div>
        </div>
        
        {filteredProofs.length === 0 ? (
          <div className="no-proofs">
            <h3>No proofs found</h3>
            <p>
              {filter === 'PENDING' 
                ? 'No pending proofs at the moment.' 
                : `No ${filter.toLowerCase()} proofs found.`}
            </p>
          </div>
        ) : (
          <div className="proofs-list">
            {filteredProofs.map((proof) => (
              <div key={proof.id} className="proof-item">
                <div className="proof-content">
                  <div className="proof-info">
                    <h4>{proof.eventTitle}</h4>
                    <div className="proof-meta">
                      <span className="user-id">User ID: {proof.userId}</span>
                      <span className="event-name">🎯 Event: {proof.eventTitle}</span>
                      <span className="submitted-date">Submitted: {proof.submittedAt}</span>
                    </div>
                    {proof.proofUrl && (
                      <div className="proof-link-container">
                        <a
                          href={proof.proofUrl}
                          className="proof-link"
                        >
                          🔗 View Proof
                        </a>
                      </div>
                    )}
                  </div>
                  
                  <div className="proof-status">
                    <span className={getStatusBadgeClass(proof.status)}>
                      {proof.status}
                    </span>
                    {proof.status === 'APPROVED' && (
                      <div className="points-awarded">
                        +{proof.pointsAwarded} points awarded
                      </div>
                    )}
                  </div>
                </div>
                {proof.status === 'REJECTED' && proof.rejectionReason && (
  <div className="rejection-box">
    <p><strong>Reason: </strong>{proof.rejectionReason}</p>
  </div>
)}
                
                {proof.status === 'PENDING' && (
                  <div className="proof-actions">
                    <button
                      className="btn btn--success"
                      onClick={() => handleApprove(proof.id)}
                      disabled={processing === proof.id}
                    >
                      {processing === proof.id ? 'Processing...' : '✅ Approve'}
                    </button>
                    <button
                      className="btn btn--danger"
                      onClick={() => handleReject(proof.id)}
                      disabled={processing === proof.id}
                    >
                      {processing === proof.id ? 'Processing...' : '❌ Reject'}
                    </button>
                  </div>
                )}

               {proof.status === 'APPROVED' && (
  <div className="proof-actions">
    <button
      className={`btn btn--secondary ${
        processing === proof.id ? 'btn--loading' : ''
      }`}
      onClick={() => handleRegenerateCertificate(proof.id)}
      disabled={processing === proof.id}
    >
      {processing === proof.id ? (
        <>
          <span className="spinner"></span>
          Regenerating...
        </>
      ) : (
        '🔄 Regenerate Certificate'
      )}
    </button>
  </div>
)}
              </div>
            ))}
          </div>
        )}
        
        <div className="approval-footer">
          <div className="stats-summary">
            <h3>Approval Summary</h3>
            <div className="summary-stats">
              <div className="stat-item">
                <span className="stat-number">{proofs.length}</span>
                <span className="stat-label">Total Submissions</span>
              </div>
              <div className="stat-item">
                <span className="stat-number">{pendingCount}</span>
                <span className="stat-label">Pending Review</span>
              </div>
              <div className="stat-item">
                <span className="stat-number">
                  {proofs.filter(p => p.status === 'APPROVED').length}
                </span>
                <span className="stat-label">Approved</span>
              </div>
              <div className="stat-item">
                <span className="stat-number">
                  {proofs.filter(p => p.status === 'REJECTED').length}
                </span>
                <span className="stat-label">Rejected</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminProofApproval;
