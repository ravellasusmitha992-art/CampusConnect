import React, { useEffect, useState } from 'react';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';

const emptyProfile = {
  fullName: '', email: '', role: '', department: '', phone: '',
  bio: '', course: '', studyYear: '',
};

export default function Profile() {
  const { updateUser } = useAuth();
  const [profile, setProfile] = useState(emptyProfile);
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [passwordSaving, setPasswordSaving] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    api.get('/profile')
      .then(({ data }) => { if (active) setProfile({ ...emptyProfile, ...data }); })
      .catch((err) => { if (active) setError(err.response?.data?.message || 'Could not load your profile.'); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, []);

  const changeField = (event) => {
    const { name, value } = event.target;
    setProfile((current) => ({ ...current, [name]: value }));
  };

  const saveProfile = async (event) => {
    event.preventDefault();
    setSaving(true); setMessage(''); setError('');
    try {
      const { data } = await api.put('/profile', {
        fullName: profile.fullName,
        phone: profile.phone,
        bio: profile.bio,
        department: profile.department,
        course: profile.course,
        studyYear: profile.studyYear,
      });
      setProfile((current) => ({ ...current, ...data }));
      updateUser(data);
      setMessage('Your profile has been updated.');
    } catch (err) {
      setError(err.response?.data?.message || 'Could not save your profile.');
    } finally { setSaving(false); }
  };

  const changePassword = async (event) => {
    event.preventDefault();
    setPasswordSaving(true); setMessage(''); setError('');
    try {
      const { data } = await api.put('/profile/password', passwords);
      setMessage(data.message || 'Password updated successfully.');
      setPasswords({ currentPassword: '', newPassword: '' });
    } catch (err) {
      setError(err.response?.data?.message || 'Could not update your password.');
    } finally { setPasswordSaving(false); }
  };

  if (loading) return <main className="container"><p>Loading profile...</p></main>;

  return (
    <main className="container profile-page">
      <header className="profile-heading">
        <div>
          <p className="profile-eyebrow">YOUR ACCOUNT</p>
          <h1>My Profile</h1>
          <p>Manage your campus details and account security.</p>
        </div>
        <div className="profile-avatar" aria-hidden="true">
          {(profile.fullName || 'U').trim().charAt(0).toUpperCase()}
        </div>
      </header>

      {message && <div className="profile-success" role="status">{message}</div>}
      {error && <div className="error" role="alert">{error}</div>}

      <section className="card">
        <h2>Personal information</h2>
        <form onSubmit={saveProfile}>
          <div className="profile-grid">
            <label>Full name<input name="fullName" value={profile.fullName} onChange={changeField} required maxLength="120" /></label>
            <label>Email address<input value={profile.email} disabled /></label>
            <label>Role<input value={profile.role} disabled /></label>
            <label>Phone number<input name="phone" value={profile.phone || ''} onChange={changeField} maxLength="20" placeholder="Enter phone number" /></label>
            <label>Department<input name="department" value={profile.department || ''} onChange={changeField} maxLength="100" placeholder="Enter department" /></label>
            <label>Course<input name="course" value={profile.course || ''} onChange={changeField} maxLength="100" placeholder="e.g. B.Tech CSE" /></label>
            <label>Year of study<input name="studyYear" value={profile.studyYear || ''} onChange={changeField} maxLength="30" placeholder="e.g. 3rd Year" /></label>
          </div>
          <label className="profile-bio-label">About me<textarea name="bio" value={profile.bio || ''} onChange={changeField} maxLength="500" rows="4" placeholder="Write a short introduction..." /></label>
          <div className="profile-form-footer"><small>{(profile.bio || '').length}/500 characters</small><button className="primary profile-save" disabled={saving}>{saving ? 'Saving...' : 'Save changes'}</button></div>
        </form>
      </section>

      <section className="card">
        <h2>Change password</h2>
        <p className="profile-section-note">Use your current password to set a new one.</p>
        <form onSubmit={changePassword} className="password-form">
          <label>Current password<input type="password" autoComplete="current-password" value={passwords.currentPassword} onChange={(e) => setPasswords((p) => ({ ...p, currentPassword: e.target.value }))} required /></label>
          <label>New password<input type="password" autoComplete="new-password" minLength="8" value={passwords.newPassword} onChange={(e) => setPasswords((p) => ({ ...p, newPassword: e.target.value }))} required /></label>
          <button className="primary profile-save" disabled={passwordSaving}>{passwordSaving ? 'Updating...' : 'Update password'}</button>
        </form>
      </section>
    </main>
  );
}
