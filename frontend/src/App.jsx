import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar.jsx';
import ProtectedRoute from './components/ProtectedRoute.jsx';
import Home from './pages/Home.jsx';
import Login from './pages/Login.jsx';
import Register from './pages/Register.jsx';
import Events from './pages/Events.jsx';
import Announcements from './pages/Announcements.jsx';
import Forum from './pages/Forum.jsx';
import Admin from './pages/Admin.jsx';
import Profile from './pages/Profile.jsx';

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/profile" element={<ProtectedRoute allowedRoles={['STUDENT', 'FACULTY', 'ADMIN']}><Profile /></ProtectedRoute>} />
        <Route path="/register" element={<Register />} />
        <Route path="/events" element={
          <ProtectedRoute allowedRoles={['STUDENT', 'FACULTY']}><Events /></ProtectedRoute>
        } />
        <Route path="/announcements" element={
          <ProtectedRoute allowedRoles={['STUDENT', 'FACULTY']}><Announcements /></ProtectedRoute>
        } />
        <Route path="/forum" element={
          <ProtectedRoute allowedRoles={['STUDENT', 'FACULTY', 'ADMIN']}><Forum /></ProtectedRoute>
        } />
        <Route path="/admin" element={
          <ProtectedRoute allowedRoles={['ADMIN']}><Admin /></ProtectedRoute>
        } />
      </Routes>
    </>
  );
}
