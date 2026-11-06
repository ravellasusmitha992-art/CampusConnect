import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
   };

  return (
    <div className="navbar">
      <Link to="/" className="brand" style={{ textDecoration: 'none' }}>
        CampusConnect
      </Link>
      <div>
        {user ? (
          <>
            {user.role !== 'ADMIN' && (
              <>
                <Link to="/events">Events</Link>
                <Link to="/announcements">Announcements</Link>
              </>
            )}
            <Link to="/forum">Forum</Link>
            {user.role === 'ADMIN' && <Link to="/admin">Admin</Link>}
            <button onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </div>
  );
}
