import React, { useState, useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import Navbar from "../components/Navbar";
import "./CertificateVerification.css";

const CertificateVerification = () => {
  const [searchParams] = useSearchParams();
  const autoCertId = searchParams.get("certId");

  const [certificateId, setCertificateId] = useState(autoCertId || "");
  const [verificationResult, setVerificationResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (autoCertId) {
      verifyCertificate(autoCertId);
    }
  }, [autoCertId]);

  const verifyCertificate = async (id) => {
    setLoading(true);
    setError("");
    setVerificationResult(null);

    try {
      const res = await fetch(`/api/certificates/verify/${id}`);
      const data = await res.json();

      if (res.ok) setVerificationResult(data);
      else setError(data.message || "Invalid certificate");
    } catch {
      setError("Verification failed");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!certificateId.trim()) return setError("Enter Certificate ID");
    verifyCertificate(certificateId.trim());
  };

  const handlePrint = () => window.print();

  const shareUrl = `${window.location.origin}/verify-certificate?certId=${certificateId}`;

  return (
    <div className="certificate-verification">
      <Navbar />

      <div className="verification-container">
        <div className="verification-header">
          <h1>Certificate Verification</h1>
          <p>Official Volunteer Certificate Authentication</p>
        </div>

        <form className="verification-form" onSubmit={handleSubmit}>
          <label>Certificate ID</label>
          <input
            value={certificateId}
            onChange={(e) => setCertificateId(e.target.value)}
            placeholder="CERT-XXXX"
          />
          <button className="btn--primary" disabled={loading}>
            {loading ? "Verifying..." : "Verify Certificate"}
          </button>
        </form>

        {error && <div className="error-message">{error}</div>}

        {verificationResult?.isValid && (
          <div className="valid-certificate">
            <div className="watermark">VOLUNTEER MANAGEMENT WING - I</div>

            <h2>Certificate of Participation</h2>
            <p className="sub-text">
              This certifies that the following individual has successfully
              completed the volunteer service.
            </p>

            <div className="certificate-details">
              <div>
                <span>Volunteer Name</span>
                <strong>{verificationResult.volunteerName}</strong>
              </div>
              <div>
                <span>Event</span>
                <strong>{verificationResult.eventTitle}</strong>
              </div>
              <div>
                <span>Certificate ID</span>
                <strong>{verificationResult.certificateId}</strong>
              </div>
              <div>
                <span>Points Awarded</span>
                <strong>{verificationResult.pointsAwarded}</strong>
              </div>
              <div>
                <span>Date Issued</span>
                <strong>{verificationResult.issuedDate}</strong>
              </div>
            </div>

            <div className="certificate-footer">
              <div className="signature">
                <img src="/signature.png" alt="Signature" />
                <span>Program Coordinator</span>
              </div>

              <div className="seal">
                <img src="/seal.png" alt="Official Seal" />
              </div>
            </div>

            <div className="certificate-actions">
              <button onClick={handlePrint}>🖨 Print / Download</button>
              <a href={shareUrl} target="_blank" rel="noreferrer">
                🌐 Share Verification
              </a>
            </div>
          </div>
        )}

        {verificationResult && !verificationResult.isValid && (
          <div className="invalid-certificate">
            ❌ Invalid Certificate
          </div>
        )}
      </div>
    </div>
  );
};

export default CertificateVerification;
