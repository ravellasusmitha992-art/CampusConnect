import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/auth/login', { email, password });
      login(res.data);
      navigate('/');
    } catch (err) {
      setError(err.response?.data || 'Login failed. Check your credentials.');
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-brand">
        <div className="auth-brand-mark">CampusConnect</div>
        <div>
          <div className="auth-brand-headline">Everything your campus needs, in one place.</div>
          <div className="auth-brand-sub">
            Events, announcements, and discussions — built for students, faculty, and admins to work together.
          </div>
          <div className="auth-feature-list">
            <div className="auth-feature"><span className="auth-feature-dot" /> Discover and join campus events</div>
            <div className="auth-feature"><span className="auth-feature-dot" /> Stay current with faculty announcements</div>
            <div className="auth-feature"><span className="auth-feature-dot" /> Ask questions, get answers in the forum</div>
          </div>
        </div>
        <div />
        <div className="auth-brand-illustration" />
      </div>

      <div className="auth-form-side">
        <div className="auth-form-box">
          <h2>Welcome back</h2>
          <p className="auth-sub">Log in to continue to CampusConnect.</p>
          {error && <div className="error">{String(error)}</div>}
          <form onSubmit={handleSubmit}>
            <input type="email" placeholder="Email" value={email}
                   onChange={(e) => setEmail(e.target.value)} required />
            <input type="password" placeholder="Password" value={password}
                   onChange={(e) => setPassword(e.target.value)} required />
            <button className="primary" type="submit">Log In</button>
          </form>
          <p className="auth-switch">
            No account yet? <Link to="/register">Register here</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
