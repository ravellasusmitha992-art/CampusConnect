import React,{useCallback,useEffect,useState} from 'react';
import {Link,useNavigate} from 'react-router-dom';
import {useAuth} from '../context/AuthContext.jsx';
import api from '../api/axios.js';
export default function Navbar(){
 const {user,logout}=useAuth(); const navigate=useNavigate(); const [unread,setUnread]=useState(0);
 const refresh=useCallback(async()=>{if(!user){setUnread(0);return;}try{const r=await api.get('/notifications/unread-count');setUnread(r.data.count||0);}catch(e){/* Keep navigation available if notifications are offline. */}},[user]);
 useEffect(()=>{refresh();if(!user)return undefined;const timer=window.setInterval(refresh,30000);return()=>window.clearInterval(timer);},[refresh,user]);
 const handleLogout=()=>{logout();navigate('/login');};
 return <div className="navbar"><Link to="/" className="brand" style={{textDecoration:'none'}}>CampusConnect</Link><div>{user?<>
 {user.role!=='ADMIN'&&<><Link to="/events">Events</Link><Link to="/announcements">Announcements</Link></>}
 <Link to="/notifications" className="notification-nav-link">Notifications {unread>0&&<span className="notification-count">{unread>99?'99+':unread}</span>}</Link>
 <Link to="/forum">Forum</Link>{user.role==='ADMIN'&&<Link to="/admin">Admin</Link>}<button onClick={handleLogout}>Logout</button>
 </>:<><Link to="/login">Login</Link><Link to="/register">Register</Link></>}</div></div>;
}
