import React from 'react';
import './EventCard.css';

const EventCard = ({ event, onRegister, onUnregister, isAdmin = false, onEdit, onDelete, showRegisterButton = true, isRegistered = false, showUnregisterButton = false, isCompleted = false, disabled = false }) => {
  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
  };

  const isFull = event.registeredVolunteers >= event.maxVolunteers;

  return (
    <div className="event-card">
      <div className="event-card__header">
        <h3 className="event-card__title">{event.title}</h3>
        <div className="event-card__points">
          <span className="points-badge">{event.points} pts</span>
        </div>
      </div>

      <div className="event-card__body">
        <p className="event-card__description">{event.description}</p>

        <div className="event-card__category">
          <span className="category-badge">{event.category || 'Uncategorized'}</span>
        </div>

        <div className="event-card__details">
          <div className="event-detail">
            <span className="event-detail__icon">📅</span>
            <span className="event-detail__text">{formatDate(event.eventDate)}</span>
          </div>

          <div className="event-detail">
            <span className="event-detail__icon">👥</span>
            <span className="event-detail__text">
              {event.registeredVolunteers}/{event.maxVolunteers} volunteers
            </span>
          </div>
        </div>

        {isFull && !isAdmin && !showUnregisterButton && !isCompleted && (
          <div className="event-card__status">
            <span className="status-badge status-badge--full">Event Full</span>
          </div>
        )}

        {isCompleted && !isAdmin && (
          <div className="event-card__status">
            <span className="status-badge status-badge--completed">Completed</span>
          </div>
        )}

        {isRegistered && !isAdmin && !isCompleted && (
          <div className="event-card__status">
            <div className="status-with-action">
              <span className="status-badge status-badge--registered">Registered</span>
              {showUnregisterButton && (
                <button
                  onClick={() => onUnregister(event.id)}
                  className="btn btn--danger"
                  disabled={disabled}
                >
                  Unregister
                </button>
              )}
            </div>
          </div>
        )}
      </div>
      
      <div className="event-card__actions">
        {isAdmin ? (
          <div className="admin-actions">
            <button
              onClick={() => onEdit(event)}
              className="btn btn--secondary btn--small"
            >
              Edit
            </button>
            <button
              onClick={() => onDelete(event.id)}
              className="btn btn--danger btn--small"
            >
              Delete
            </button>
          </div>
        ) : (
          showRegisterButton && !isFull && (
            <button
              onClick={() => onRegister(event.id)}
              className="btn btn--primary"
            >
              Register Now
            </button>
          )
        )}
      </div>
    </div>
  );
};

export default EventCard;
