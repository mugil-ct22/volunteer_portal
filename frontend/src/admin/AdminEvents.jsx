import React, { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import api from '../api/axiosConfig';
import './AdminEvents.css';

const AdminEvents = () => {
  const { user } = useAuth();
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingEvent, setEditingEvent] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    eventDate: '',
    points: '',
    maxVolunteers: '',
    category: ''
  });
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    try {
      const response = await api.get('/api/admin/events');
      setEvents(response.data);
      setError('');
    } catch (err) {
      console.error('Error fetching events:', err);
      if (err.response?.status === 401) {
        setError('Authentication failed. Please login again.');
      } else {
        setError('Failed to load events');
      }
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setFormData({
      title: '',
      description: '',
      eventDate: '',
      points: '',
      maxVolunteers: '',
      category: ''
    });
    setEditingEvent(null);
    setShowForm(false);
  };

  const handleEdit = (event) => {
    setEditingEvent(event);
    setFormData({
      title: event.title || '',
      description: event.description || '',
      eventDate: event.eventDate ? event.eventDate.split('T')[0] : '',
      points: event.points || '',
      maxVolunteers: event.maxVolunteers || '',
      category: event.category || ''
    });
    setShowForm(true);
  };

  const handleDelete = async (eventId) => {
    if (window.confirm('Are you sure you want to delete this event?')) {
      try {
        await api.delete(`/api/admin/events/${eventId}`);
        setEvents(events.filter(event => event.id !== eventId));
        alert('Event deleted successfully!');
      } catch (err) {
        console.error('Delete error:', err);
        alert('Failed to delete event');
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const eventData = {
        title: formData.title,
        description: formData.description,
        eventDate: `${formData.eventDate}T12:00:00`,
        points: parseInt(formData.points),
        maxVolunteers: parseInt(formData.maxVolunteers),
        category: formData.category
      };

      if (editingEvent) {
        const response = await api.put(`/api/admin/events/${editingEvent.id}`, eventData);
        setEvents(events.map(event =>
          event.id === editingEvent.id
            ? { ...event, ...response.data }
            : event
        ));
        alert('Event updated successfully!');
      } else {
        const response = await api.post('/api/admin/events', eventData);
        setEvents([...events, response.data]);
        alert('Event created successfully!');
      }

      resetForm();
    } catch (err) {
      console.error('Event submission error:', err);
      alert(`Failed to ${editingEvent ? 'update' : 'create'} event`);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  if (loading) {
    return (
      <div className="loading-container">
        <div className="loading-spinner"></div>
        <p>Loading events...</p>
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
    <div className="admin-events">
      <Navbar />

      <div className="events-management-container">
        <div className="events-header">
          <h1>Events Management</h1>
          <p>Create, edit, and manage volunteer events</p>
          <button
            className="btn btn--primary"
            onClick={() => setShowForm(true)}
          >
            ➕ Create New Event
          </button>
        </div>

        {showForm && (
          <div className="event-form">
            <h3>{editingEvent ? 'Edit Event' : 'Create New Event'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="title">Event Title *</label>
                  <input
                    type="text"
                    id="title"
                    name="title"
                    value={formData.title}
                    onChange={handleInputChange}
                    required
                    placeholder="Enter event title"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="eventDate">Date *</label>
                  <input
                    type="date"
                    id="eventDate"
                    name="eventDate"
                    value={formData.eventDate}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="description">Description *</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  required
                  placeholder="Describe the event and its purpose"
                  rows="4"
                />
              </div>

              <div className="form-group">
                <label htmlFor="category">Category {editingEvent ? '(Optional)' : '*'}</label>
                <select
                  id="category"
                  name="category"
                  value={formData.category || ''}
                  onChange={handleInputChange}
                  required={!editingEvent}
                >
                  <option value="">Select a category</option>
                  <option value="Community Service">Community Service</option>
                  <option value="Environmental">Environmental</option>
                  <option value="Health & Awareness">Health & Awareness</option>
                  <option value="Education & Teaching">Education & Teaching</option>
                  <option value="Blood Donation">Blood Donation</option>
                  <option value="Disaster Relief">Disaster Relief</option>
                  <option value="Others">Others</option>
                </select>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="points">Points *</label>
                  <input
                    type="number"
                    id="points"
                    name="points"
                    value={formData.points}
                    onChange={handleInputChange}
                    required
                    min="1"
                    placeholder="Points awarded"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="maxVolunteers">Max Volunteers *</label>
                  <input
                    type="number"
                    id="maxVolunteers"
                    name="maxVolunteers"
                    value={formData.maxVolunteers}
                    onChange={handleInputChange}
                    required
                    min="1"
                    placeholder="Maximum volunteers"
                  />
                </div>
              </div>

              <div className="form-actions">
                <button type="submit" className="btn btn--primary">
                  {editingEvent ? 'Update Event' : 'Create Event'}
                </button>
                <button type="button" className="btn btn--secondary" onClick={resetForm}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {events.length === 0 ? (
          <div className="no-events">
            <h3>No events found</h3>
            <p>Create your first event to get started!</p>
          </div>
        ) : (
          <div className="events-grid">
            {events.map((event) => (
              <EventCard
                key={event.id}
                event={event}
                isAdmin={true}
                onEdit={handleEdit}
                onDelete={handleDelete}
                showRegisterButton={false}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminEvents;
