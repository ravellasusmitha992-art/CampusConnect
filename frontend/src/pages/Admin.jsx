import React, { useEffect, useState } from 'react';
import api from '../api/axios.js';

export default function Admin() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/admin/users');
      setUsers(res.data);
    } catch (err) {
      setError('Failed to load users.');
    }
  };

  useEffect(() => { load(); }, []);

  const handleDeleteUser = async (id) => {
    if (!window.confirm('Delete this user?')) return;
    try {
      await api.delete(`/admin/users/${id}`);
      load();
    } catch (err) {
      setError('Failed to delete user.');
    }
  };

  return (
    <div className="container">
      <h2>Admin Panel - User Management</h2>
      {error && <div className="error">{error}</div>}
      {users.map((u) => (
        <div className="card" key={u.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <strong>{u.fullName}</strong> — {u.email} ({u.role})
            {u.department && <span> · {u.department}</span>}
          </div>
          <button className="primary" style={{ width: 'auto', padding: '8px 16px', background: '#dc2626' }}
                  onClick={() => handleDeleteUser(u.id)}>
            Delete
          </button>
        </div>
      ))}
    </div>
  );
}
