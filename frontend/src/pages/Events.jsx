import React, { useEffect, useState } from 'react';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';
import Modal from '../components/Modal.jsx';

export default function Events() {
  const { user } = useAuth();
  const [events, setEvents] = useState([]);
  const [form, setForm] = useState({ title: '', description: '', venue: '', posterUrl: '', eventDate: '' });
  const [error, setError] = useState('');

  const [registerTarget, setRegisterTarget] = useState(null); // event being registered for
  const [regForm, setRegForm] = useState({ phone: '', teamName: '', notes: '' });
  const [regSuccess, setRegSuccess] = useState(false);

  const canCreate = user && user.role === 'FACULTY';

  const loadEvents = async () => {
    try {
      const res = await api.get('/events');
      setEvents(res.data);
    } catch (err) {
      setError('Failed to load events.');
    }
  };

  useEffect(() => { loadEvents(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await api.post('/events', form);
      setForm({ title: '', description: '', venue: '', posterUrl: '', eventDate: '' });
      loadEvents();
    } catch (err) {
      setError('Failed to create event.');
    }
  };

  const openRegister = (ev) => {
    setRegisterTarget(ev);
    setRegForm({ phone: '', teamName: '', notes: '' });
    setRegSuccess(false);
  };

  const closeRegister = () => {
    setRegisterTarget(null);
    setRegSuccess(false);
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    try {
      await api.post(`/events/${registerTarget.id}/join`);
      setRegSuccess(true);
      loadEvents();
    } catch (err) {
      setError('Registration failed. You may have already joined this event.');
    }
  };

  const isAlreadyJoined = (ev) =>
    ev.participants?.some((p) => p.email === user?.email);

  return (
    <div className="container">
      <h2>Campus Events</h2>
      {error && <div className="error">{error}</div>}

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
            <input type="datetime-local" value={form.eventDate}
                   onChange={(e) => setForm({ ...form, eventDate: e.target.value })} />
            <button className="primary" type="submit">Create Event</button>
          </form>
        </div>
      )}

      <div className="poster-grid">
        {events.map((ev) => (
          <div className="poster-card" key={ev.id}>
            {ev.posterUrl && (
              <img className="poster-image" src={ev.posterUrl} alt={ev.title} />
            )}
            <div className="poster-body">
              <h3>{ev.title}</h3>
              <p>{ev.description}</p>
              <p className="poster-meta">
                {ev.venue || 'Venue TBA'} · {ev.eventDate ? new Date(ev.eventDate).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' }) : 'Date TBA'}
              </p>
              <p className="poster-meta">{ev.participants?.length || 0} joined</p>
              {user && user.role === 'STUDENT' && (
                isAlreadyJoined(ev) ? (
                  <button className="primary" disabled style={{ marginTop: 12, width: 'auto', padding: '8px 16px', background: '#94a3b8', cursor: 'default' }}>
                    Registered ✓
                  </button>
                ) : (
                  <button className="primary" style={{ marginTop: 12, width: 'auto', padding: '8px 16px' }}
                          onClick={() => openRegister(ev)}>
                    Join Event
                  </button>
                )
              )}
            </div>
          </div>
        ))}
      </div>

      {registerTarget && (
        <Modal onClose={closeRegister}>
          {!regSuccess ? (
            <>
              <h2 style={{ fontSize: 22, marginBottom: 4 }}>Register for {registerTarget.title}</h2>
              <p className="auth-sub" style={{ marginBottom: 20 }}>
                {registerTarget.venue || 'Venue TBA'} · {registerTarget.eventDate ? new Date(registerTarget.eventDate).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' }) : 'Date TBA'}
              </p>
              <form onSubmit={handleRegisterSubmit}>
                <input value={user?.fullName || ''} disabled />
                <input value={user?.email || ''} disabled />
                <input placeholder="Phone number" value={regForm.phone}
                       onChange={(e) => setRegForm({ ...regForm, phone: e.target.value })} />
                <input placeholder="Team name (if applicable)" value={regForm.teamName}
                       onChange={(e) => setRegForm({ ...regForm, teamName: e.target.value })} />
                <textarea placeholder="Any notes for the organizer (optional)" rows="2" value={regForm.notes}
                          onChange={(e) => setRegForm({ ...regForm, notes: e.target.value })} />
                <button className="primary" type="submit">Confirm Registration</button>
              </form>
            </>
          ) : (
            <div style={{ textAlign: 'center', padding: '20px 0' }}>
              <div style={{ fontSize: 40, marginBottom: 12 }}>✓</div>
              <h2 style={{ fontSize: 22, marginBottom: 8 }}>You're registered!</h2>
              <p className="auth-sub">You've successfully joined {registerTarget.title}.</p>
              <button className="primary" style={{ marginTop: 20 }} onClick={closeRegister}>Close</button>
            </div>
          )}
        </Modal>
      )}
    </div>
  );
}
