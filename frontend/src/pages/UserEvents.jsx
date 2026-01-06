import React, { useState, useEffect } from 'react';
import { useAuth } from '../auth/AuthContext';
import Navbar from '../components/Navbar';
import EventCard from '../components/EventCard';
import { eventsAPI, proofsAPI } from '../services/api';
import './UserEvents.css';

const UserEvents = () => {
  const { user } = useAuth();
  const [events, setEvents] = useState([]);
  const [registeredEvents, setRegisteredEvents] = useState([]);
  const [completedEvents, setCompletedEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [registering, setRegistering] = useState(null);
  const [unregistering, setUnregistering] = useState(null);
  const [selectedCategory, setSelectedCategory] = useState('All Categories');
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [eventsData, registeredData, proofsData, categoriesData] = await Promise.all([
          eventsAPI.getAllEvents(),
          eventsAPI.getRegisteredEvents(),
          proofsAPI.getUserProofs(),
          eventsAPI.getAllCategories()
        ]);

        // Separate completed and pending events
        const approvedProofs = proofsData.filter(proof => proof.status === 'APPROVED');
        const completedEventIds = new Set(approvedProofs.map(proof => proof.eventId));

        const completed = registeredData.filter(event => completedEventIds.has(event.id));
        const pending = registeredData.filter(event => !completedEventIds.has(event.id));

        setEvents(eventsData);
        setRegisteredEvents(pending);
        setCompletedEvents(completed);
        setCategories(['All Categories', ...categoriesData]);
      } catch (err) {
        setError('Failed to load events');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleRegister = async (eventId) => {
    setRegistering(eventId);
    try {
      await eventsAPI.registerForEvent(eventId);

      // Refresh the events data to get the latest state
      const [eventsData, registeredData, proofsData] = await Promise.all([
        eventsAPI.getAllEvents(),
        eventsAPI.getRegisteredEvents(),
        proofsAPI.getUserProofs()
      ]);

      // Separate completed and pending events
      const approvedProofs = proofsData.filter(proof => proof.status === 'APPROVED');
      const completedEventIds = new Set(approvedProofs.map(proof => proof.eventId));

      const completed = registeredData.filter(event => completedEventIds.has(event.id));
      const pending = registeredData.filter(event => !completedEventIds.has(event.id));

      setEvents(eventsData);
      setRegisteredEvents(pending);
      setCompletedEvents(completed);

      alert('Successfully registered for the event!');
    } catch (err) {
      alert(err.message || 'Failed to register for the event');
    } finally {
      setRegistering(null);
    }
  };

  const handleUnregister = async (eventId) => {
    if (!window.confirm('Are you sure you want to unregister from this event?')) {
      return;
    }

    setUnregistering(eventId);
    try {
      await eventsAPI.unregisterFromEvent(eventId);

      // Refresh the events data to get the latest state
      const [eventsData, registeredData, proofsData] = await Promise.all([
        eventsAPI.getAllEvents(),
        eventsAPI.getRegisteredEvents(),
        proofsAPI.getUserProofs()
      ]);

      // Separate completed and pending events
      const approvedProofs = proofsData.filter(proof => proof.status === 'APPROVED');
      const completedEventIds = new Set(approvedProofs.map(proof => proof.eventId));

      const completed = registeredData.filter(event => completedEventIds.has(event.id));
      const pending = registeredData.filter(event => !completedEventIds.has(event.id));

      setEvents(eventsData);
      setRegisteredEvents(pending);
      setCompletedEvents(completed);

      // Reset category filter to show the unregistered event
      setSelectedCategory('All Categories');

      alert('Successfully unregistered from the event!');
    } catch (err) {
      alert(err.message || 'Failed to unregister from the event');
    } finally {
      setUnregistering(null);
    }
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

  // Filter out registered and completed events from available events, and apply category filter
  const availableEvents = events.filter(event => {
    const isNotRegisteredOrCompleted = !registeredEvents.some(regEvent => regEvent.id === event.id) &&
                                      !completedEvents.some(compEvent => compEvent.id === event.id);
    const matchesCategory = selectedCategory === 'All Categories' || event.category === selectedCategory;
    return isNotRegisteredOrCompleted && matchesCategory;
  });

  return (
    <div className="user-events">
      <Navbar />
      
      <div className="events-container">
        {/* Available Events Section */}
        <div className="events-section">
          <div className="events-header">
            <div className="events-header-content">
              <div>
                <h1>Available Events</h1>
                <p>Discover and register for volunteer opportunities</p>
              </div>
              <div className="category-filter">
                <select
                  value={selectedCategory}
                  onChange={(e) => setSelectedCategory(e.target.value)}
                  className="category-select"
                >
                  {categories.map(category => (
                    <option key={category} value={category}>{category}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>
          
          {availableEvents.length === 0 ? (
            <div className="no-events">
              <h3>No events available at the moment</h3>
              <p>Check back later for new volunteer opportunities!</p>
            </div>
          ) : (
            <div className="events-grid">
              {availableEvents.map((event) => (
                <EventCard
                  key={event.id}
                  event={event}
                  onRegister={handleRegister}
                  isAdmin={false}
                  showRegisterButton={true}
                  disabled={registering === event.id}
                />
              ))}
            </div>
          )}
        </div>

        {/* Registered Events Section */}
        {registeredEvents.length > 0 && (
          <div className="events-section">
            <div className="events-header">
              <h1>My Registered Events</h1>
              <p>Events you've signed up for</p>
            </div>

            <div className="events-grid">
              {registeredEvents.map((event) => (
                <EventCard
                  key={event.id}
                  event={event}
                  onRegister={handleRegister}
                  onUnregister={handleUnregister}
                  isAdmin={false}
                  showRegisterButton={false}
                  showUnregisterButton={true}
                  isRegistered={true}
                  disabled={unregistering === event.id}
                />
              ))}
            </div>
          </div>
        )}

        {/* Completed Events Section */}
        {completedEvents.length > 0 && (
          <div className="events-section">
            <div className="events-header">
              <h1>Completed Events</h1>
              <p>Events you've completed and earned points for</p>
            </div>

            <div className="events-grid">
              {completedEvents.map((event) => (
                <EventCard
                  key={event.id}
                  event={event}
                  onRegister={handleRegister}
                  isAdmin={false}
                  showRegisterButton={false}
                  isCompleted={true}
                  disabled={false}
                />
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserEvents;
