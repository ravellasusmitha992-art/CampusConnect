import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function Register() {
  const [form, setForm] = useState({
    fullName: '', email: '', password: '', role: 'STUDENT', department: ''
  });
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      const res = await api.post('/auth/register', form);
      login(res.data);
      navigate('/');
    } catch (err) {
      setError(err.response?.data || 'Registration failed.');
    }
  };

  return (
    <div className="auth-shell">
      <div className="auth-brand">
        <div className="auth-brand-mark">CampusConnect</div>
        <div>
          <div className="auth-brand-headline">Join your campus community today.</div>
          <div className="auth-brand-sub">
            One account, every role. Set up in under a minute.
          </div>
          <div className="auth-feature-list">
            <div className="auth-feature"><span className="auth-feature-dot" /> Students: join events, ask questions</div>
            <div className="auth-feature"><span className="auth-feature-dot" /> Faculty: post events and announcements</div>
            <div className="auth-feature"><span className="auth-feature-dot" /> Admins: keep the platform running smoothly</div>
          </div>
        </div>
        <div />
        <div className="auth-brand-illustration" />
      </div>

      <div className="auth-form-side">
        <div className="auth-form-box">
          <h2>Create your account</h2>
          <p className="auth-sub">It only takes a minute to get started.</p>
          {error && <div className="error">{String(error)}</div>}
          <form onSubmit={handleSubmit}>
            <input name="fullName" placeholder="Full Name" value={form.fullName}
                   onChange={handleChange} required />
            <input name="email" type="email" placeholder="Email" value={form.email}
                   onChange={handleChange} required />
            <input name="password" type="password" placeholder="Password" value={form.password}
                   onChange={handleChange} required />
            <select name="role" value={form.role} onChange={handleChange}>
              <option value="STUDENT">Student</option>
              <option value="FACULTY">Faculty</option>
              <option value="ADMIN">Admin</option>
            </select>
            <input name="department" placeholder="Department" value={form.department}
                   onChange={handleChange} />
            <button className="primary" type="submit">Create Account</button>
          </form>
          <p className="auth-switch">
            Already have an account? <Link to="/login">Log in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
