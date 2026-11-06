import React, { useEffect, useState } from 'react';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';
import Modal from '../components/Modal.jsx';

export default function Announcements() {
  const { user } = useAuth();
  const [announcements, setAnnouncements] = useState([]);
  const [form, setForm] = useState({ title: '', content: '', posterUrl: '' });
  const [error, setError] = useState('');
  const [detailsTarget, setDetailsTarget] = useState(null);

  const canPost = user && user.role === 'FACULTY';

  const load = async () => {
    try {
      const res = await api.get('/announcements');
      setAnnouncements(res.data);
    } catch (err) {
      setError('Failed to load announcements.');
    }
  };

  useEffect(() => { load(); }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      await api.post('/announcements', form);
      setForm({ title: '', content: '', posterUrl: '' });
      load();
    } catch (err) {
      setError('Failed to post announcement.');
    }
  };

  return (
    <div className="container">
      <h2>Announcements</h2>
      {error && <div className="error">{error}</div>}

      {canPost && (
        <div className="card">
          <h3>Post Announcement</h3>
          <form onSubmit={handleCreate}>
            <input placeholder="Title" value={form.title}
                   onChange={(e) => setForm({ ...form, title: e.target.value })} required />
            <textarea placeholder="Content" rows="3" value={form.content}
                      onChange={(e) => setForm({ ...form, content: e.target.value })} required />
            <input placeholder="Poster image URL (optional)" value={form.posterUrl}
                   onChange={(e) => setForm({ ...form, posterUrl: e.target.value })} />
            <button className="primary" type="submit">Post</button>
          </form>
        </div>
      )}

      <div className="poster-grid">
        {announcements.map((a) => (
          <div className="poster-card poster-card-clickable" key={a.id} onClick={() => setDetailsTarget(a)}>
            {a.posterUrl && (
              <img className="poster-image" src={a.posterUrl} alt={a.title} />
            )}
            <div className="poster-body">
              <h3>{a.title}</h3>
              <p className="poster-excerpt">{a.content}</p>
              <p className="poster-meta">
                By {a.postedBy?.fullName || 'Faculty'} · {a.createdAt ? new Date(a.createdAt).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' }) : ''}
              </p>
              <span className="read-more-link">Read full announcement →</span>
            </div>
          </div>
        ))}
      </div>

      {detailsTarget && (
        <Modal onClose={() => setDetailsTarget(null)}>
          {detailsTarget.posterUrl && (
            <img className="modal-poster-image" src={detailsTarget.posterUrl} alt={detailsTarget.title} />
          )}
          <h2 style={{ fontSize: 24, marginTop: 16, marginBottom: 6 }}>{detailsTarget.title}</h2>
          <p className="poster-meta" style={{ marginBottom: 18 }}>
            By {detailsTarget.postedBy?.fullName || 'Faculty'} · {detailsTarget.createdAt ? new Date(detailsTarget.createdAt).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''}
          </p>
          <p style={{ fontSize: 15, lineHeight: 1.7, color: 'var(--ink)' }}>{detailsTarget.content}</p>
        </Modal>
      )}
    </div>
  );
}
