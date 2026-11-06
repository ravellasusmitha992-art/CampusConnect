import React from 'react';
import { useAuth } from '../context/AuthContext.jsx';

export default function Home() {
  const { user } = useAuth();
  return (
    <div>
      <div className="hero">
        <div className="hero-eyebrow">Campus Portal</div>
        <h1>Welcome{user ? `, ${user.fullName}` : ''} to CampusConnect</h1>
        <p>A single home for campus events, announcements, and discussions — built for students, faculty, and admins.</p>
      </div>

      <div className="feature-grid">
        <div className="feature-card">
          <h3>Events</h3>
          <p>Discover what's happening on campus and join in a click. Faculty post, students participate.</p>
        </div>
        <div className="feature-card">
          <h3>Announcements</h3>
          <p>Academic updates and important notices from faculty, all in one reliable feed — no more missed emails.</p>
        </div>
        <div className="feature-card">
          <h3>Forum</h3>
          <p>Ask a question, get an answer from faculty or peers. A shared space for campus conversation.</p>
        </div>
      </div>
    </div>
  );
}
