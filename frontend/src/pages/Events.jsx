import React, { useEffect, useState } from 'react';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';
import Modal from '../components/Modal.jsx';

const emptyEventForm = { title: '', description: '', venue: '', posterUrl: '', eventDate: '' };
const emptyRegistration = { phone: '', teamName: '', notes: '' };

function formatEventDate(value) {
  if (!value) return 'Date TBA';
  return new Date(value).toLocaleString(undefined, {
    day: 'numeric', month: 'short', year: 'numeric', hour: 'numeric', minute: '2-digit'
  });
}

export default function Events() {
  const { user } = useAuth();
  const [events, setEvents] = useState([]);
  const [form, setForm] = useState(emptyEventForm);
  const [registrationForm, setRegistrationForm] = useState(emptyRegistration);
  const [registerTarget, setRegisterTarget] = useState(null);
  const [participantsTarget, setParticipantsTarget] = useState(null);
  const [participants, setParticipants] = useState([]);
  const [loadingParticipants, setLoadingParticipants] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [notice, setNotice] = useState('');

  const canCreate = user?.role === 'FACULTY';
  const isStudent = user?.role === 'STUDENT';

  const loadEvents = async () => {
    try {
      const response = await api.get('/events');
      setEvents(response.data);
      setError('');
    } catch {
      setError('Failed to load events. Please refresh the page.');
    }
  };

  useEffect(() => { loadEvents(); }, []);

  const handleCreate = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      await api.post('/events', form);
      setForm(emptyEventForm);
      setNotice('Event created successfully.');
      await loadEvents();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create event.');
    } finally {
      setSubmitting(false);
    }
  };

  const openRegister = (event) => {
    setRegisterTarget(event);
    setRegistrationForm(emptyRegistration);
    setError('');
    setNotice('');
  };

  const handleRegister = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setError('');
    try {
      await api.post(`/events/${registerTarget.id}/registrations`, registrationForm);
      setRegisterTarget(null);
      setNotice(`You are registered for ${registerTarget.title}.`);
      await loadEvents();
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  const cancelRegistration = async (event) => {
    const confirmed = window.confirm(`Cancel your registration for ${event.title}?`);
    if (!confirmed) return;
    setError('');
    try {
      await api.delete(`/events/${event.id}/registrations`);
      setNotice(`Your registration for ${event.title} was cancelled.`);
      await loadEvents();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not cancel your registration.');
    }
  };

  const openParticipants = async (event) => {
    setParticipantsTarget(event);
    setParticipants([]);
    setLoadingParticipants(true);
    setError('');
    try {
      const response = await api.get(`/events/${event.id}/registrations`);
      setParticipants(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Could not load event participants.');
      setParticipantsTarget(null);
    } finally {
      setLoadingParticipants(false);
    }
  };

  return (
    <div className="container">
      <h2>Campus Events</h2>
      {error && <div className="error" role="alert">{error}</div>}
      {notice && <div className="card" role="status" style={{ color: '#166534', padding: 12 }}>{notice}</div>}

      {canCreate && (
        <div className="card">
          <h3>Create Event</h3>
          <form onSubmit={handleCreate}>
            <input placeholder="Title" value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })} required />
            <textarea placeholder="Description" rows="3" value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })} />
            <input placeholder="Venue" value={form.venue}
              onChange={(e) => setForm({ ...form, venue: e.target.value })} />
            <input placeholder="Poster image URL (optional)" value={form.posterUrl}
              onChange={(e) => setForm({ ...form, posterUrl: e.target.value })} />
            <label htmlFor="event-date">Event date and time</label>
            <input id="event-date" type="datetime-local" value={form.eventDate}
              onChange={(e) => setForm({ ...form, eventDate: e.target.value })} />
            <button className="primary" type="submit" disabled={submitting}>
              {submitting ? 'Saving...' : 'Create Event'}
            </button>
          </form>
        </div>
      )}

      {events.length === 0 ? (
        <div className="card"><p>No events are available right now.</p></div>
      ) : (
        <div className="poster-grid">
          {events.map((event) => (
            <article className="poster-card" key={event.id}>
              {event.posterUrl && <img className="poster-image" src={event.posterUrl} alt={`${event.title} poster`} />}
              <div className="poster-body">
                <h3>{event.title}</h3>
                <p>{event.description || 'More details will be announced soon.'}</p>
                <p className="poster-meta">{event.venue || 'Venue TBA'} · {formatEventDate(event.eventDate)}</p>
                {event.organizer && <p className="poster-meta">Organized by {event.organizer}</p>}
                <p className="poster-meta">{event.participantCount || 0} registered</p>

                {isStudent && (event.registeredByMe ? (
                  <button type="button" className="primary" onClick={() => cancelRegistration(event)}
                    style={{ marginTop: 12, width: 'auto', padding: '8px 16px', background: '#64748b' }}>
                    Cancel registration
                  </button>
                ) : (
                  <button type="button" className="primary" onClick={() => openRegister(event)}
                    style={{ marginTop: 12, width: 'auto', padding: '8px 16px' }}>
                    Register for event
                  </button>
                ))}

                {(user?.role === 'ADMIN' || (canCreate && event.organizerId === user.userId)) && (
                  <button type="button" className="primary" onClick={() => openParticipants(event)}
                    style={{ marginTop: 12, width: 'auto', padding: '8px 16px', background: '#3D5A80' }}>
                    View participants
                  </button>
                )}
              </div>
            </article>
          ))}
        </div>
      )}

      {registerTarget && (
        <Modal onClose={() => setRegisterTarget(null)}>
          <h2 style={{ fontSize: 22, marginBottom: 4 }}>Register for {registerTarget.title}</h2>
          <p className="auth-sub" style={{ marginBottom: 20 }}>
            {registerTarget.venue || 'Venue TBA'} · {formatEventDate(registerTarget.eventDate)}
          </p>
          <form onSubmit={handleRegister}>
            <input value={user?.fullName || ''} aria-label="Full name" disabled />
            <input value={user?.email || ''} aria-label="Email" disabled />
            <input placeholder="Phone number (optional)" value={registrationForm.phone}
              onChange={(e) => setRegistrationForm({ ...registrationForm, phone: e.target.value })} />
            <input placeholder="Team name (optional)" value={registrationForm.teamName}
              onChange={(e) => setRegistrationForm({ ...registrationForm, teamName: e.target.value })} />
            <textarea placeholder="Notes for the organizer (optional)" rows="3" value={registrationForm.notes}
              onChange={(e) => setRegistrationForm({ ...registrationForm, notes: e.target.value })} />
            <button className="primary" type="submit" disabled={submitting}>
              {submitting ? 'Registering...' : 'Confirm Registration'}
            </button>
          </form>
        </Modal>
      )}

      {participantsTarget && (
        <Modal onClose={() => setParticipantsTarget(null)}>
          <h2 style={{ fontSize: 22 }}>Participants – {participantsTarget.title}</h2>
          {loadingParticipants ? <p>Loading participants...</p> : participants.length === 0 ? (
            <p>No students have registered yet.</p>
          ) : (
            <div style={{ maxHeight: '60vh', overflowY: 'auto' }}>
              {participants.map((participant) => (
                <div className="card" key={participant.registrationId} style={{ marginBottom: 10 }}>
                  <h3>{participant.fullName}</h3>
                  <p>{participant.email}</p>
                  {participant.department && <p className="poster-meta">{participant.department}</p>}
                  {participant.phone && <p className="poster-meta">Phone: {participant.phone}</p>}
                  {participant.teamName && <p className="poster-meta">Team: {participant.teamName}</p>}
                  {participant.notes && <p className="poster-meta">Notes: {participant.notes}</p>}
                  <p className="poster-meta">Registered: {formatEventDate(participant.registeredAt)}</p>
                </div>
              ))}
            </div>
          )}
        </Modal>
      )}
    </div>
  );
}
