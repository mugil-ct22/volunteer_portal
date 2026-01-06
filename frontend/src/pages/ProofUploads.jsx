import React, { useState, useEffect } from "react";
import { useAuth } from "../auth/AuthContext";
import Navbar from "../components/Navbar";
import { proofsAPI, eventsAPI } from "../services/api";
import "./ProofUploads.css";

const ProofUploads = () => {
  const { user } = useAuth();
  const [proofs, setProofs] = useState([]);
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [uploading, setUploading] = useState(false);
  const [showUploadForm, setShowUploadForm] = useState(false);
  const [uploadData, setUploadData] = useState({ eventId: "", file: null });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [proofsData, eventsData] = await Promise.all([
        proofsAPI.getUserProofs(),
        eventsAPI.getRegisteredEvents(),
      ]);

      const statusOrder = { PENDING: 0, APPROVED: 1, REJECTED: 2 };

      const sortedProofs = proofsData.sort((a, b) => {
        const diff = statusOrder[a.status] - statusOrder[b.status];
        return diff !== 0
          ? diff
          : new Date(b.submittedAt) - new Date(a.submittedAt);
      });

      const approvedEventIds = new Set(
        sortedProofs.filter(p => p.status === "APPROVED").map(p => p.eventId)
      );

      setEvents(eventsData.filter(e => !approvedEventIds.has(e.id)));
      setProofs(sortedProofs);
    } catch {
      setError("Failed to load data");
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!uploadData.eventId || !uploadData.file) {
      alert("Please fill all fields");
      return;
    }

    setUploading(true);
    try {
      await proofsAPI.uploadProof(+uploadData.eventId, uploadData.file);
      await fetchData();
      setUploadData({ eventId: "", file: null });
      setShowUploadForm(false);
      alert("Proof uploaded successfully!");
    } catch (err) {
      alert(err.message || "Upload failed");
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteProof = async (id) => {
    if (!window.confirm("Are you sure you want to delete this proof?")) return;
    await proofsAPI.deleteProof(id);
    fetchData();
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
  <div className="proof-uploads">
    <Navbar />

    <div className="proofs-container">
      {/* HEADER */}
      <div className="proofs-header">
        <div>
          <h1>Proof Uploads</h1>
          <p>Upload and track your volunteer event proofs</p>
        </div>

        <button
          className="btn btn--primary"
          onClick={() => setShowUploadForm(!showUploadForm)}
        >
          {showUploadForm ? "Cancel" : "Upload New Proof"}
        </button>
      </div>

      {/* UPLOAD FORM */}
      {showUploadForm && (
        <div className="upload-form-card">
          <h3>Upload Event Proof</h3>

          <form onSubmit={handleUpload} className="upload-form">
            <div className="upload-row">
              <select
                value={uploadData.eventId}
                onChange={(e) =>
                  setUploadData({ ...uploadData, eventId: e.target.value })
                }
                required
              >
                <option value="">Choose an event...</option>
                {events.map((event) => (
                  <option key={event.id} value={event.id}>
                    {event.title}
                  </option>
                ))}
              </select>

              <input
                type="file"
                onChange={(e) =>
                  setUploadData({ ...uploadData, file: e.target.files[0] })
                }
                required
              />
            </div>

            <div className="upload-actions">
              <button type="submit" disabled={uploading}>
                {uploading ? "Uploading..." : "Upload Proof"}
              </button>
            </div>
          </form>
        </div>
      )}

      {/* PROOFS LIST */}
      <div className="proofs-list">
        {proofs.map((proof) => (
          <div key={proof.id} className="proof-card">
            {/* HEADER */}
            <div className="proof-card-header">
              <div className="proof-title-group">
                <div className="event-title-row">
                  <h4>{proof.eventTitle}</h4>
                  <span className="event-category-badge">
                    {proof.eventCategory}
                  </span>
                </div>

                <span className="event-created-by">
                  Event Co-ordinator: {proof.eventCoordinatorName}
                </span>

                <span className="proof-dates">
                  Submitted:{" "}
                  {new Date(proof.submittedAt).toLocaleString()}
                  {proof.reviewedAt && (
                    <>
                      {" "}
                      · Reviewed:{" "}
                      {new Date(proof.reviewedAt).toLocaleString()}
                    </>
                  )}
                </span>
              </div>

              <span className={`status-pill ${proof.status.toLowerCase()}`}>
                {proof.status}
              </span>
            </div>

            {/* ACTION ROW */}
            <div className="proof-action-row">
              {proof.proofUrl && (
                <a
                  href={proof.proofUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="proof-link"
                >
                  🔗 View Proof
                </a>
              )}

              {proof.status === "APPROVED" && (
                <button
                  className="download-cert-btn"
                  onClick={() =>
                    window.open(
                      `${import.meta.env.VITE_API_BASE_URL}/api/certificates/download/${proof.certificateId}`,
                      "_blank"
                    )
                  }
                >
                  📄 Download Certificate
                </button>
              )}

              {proof.status === "PENDING" && (
                <button
                  className="delete-proof-btn"
                  onClick={() => handleDeleteProof(proof.id)}
                >
                  🗑️ Delete Proof
                </button>
              )}
            </div>

            {/* REJECTION */}
            {proof.status === "REJECTED" && (
              <div className="proof-reason">
                <strong>Reason:</strong> {proof.rejectionReason}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  </div>
);
};

export default ProofUploads;
