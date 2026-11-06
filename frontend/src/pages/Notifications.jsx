import React,{useCallback,useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import api from '../api/axios.js';
export default function Notifications(){
 const [items,setItems]=useState([]); const [error,setError]=useState(''); const [loading,setLoading]=useState(true);
 const load=useCallback(async()=>{try{const r=await api.get('/notifications');setItems(r.data);setError('');}catch(e){setError('Unable to load notifications. Please try again.');}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const markRead=async(id)=>{try{await api.patch(`/notifications/${id}/read`);setItems(old=>old.map(n=>n.id===id?{...n,read:true}:n));}catch(e){setError('Unable to update this notification.');}};
 const markAll=async()=>{try{await api.patch('/notifications/read-all');setItems(old=>old.map(n=>({...n,read:true})));}catch(e){setError('Unable to mark notifications as read.');}};
 const unread=items.filter(n=>!n.read).length;
 return <main className="container notifications-page"><div className="notifications-heading"><div><h2>Notifications</h2><p className="muted-text">Updates from your campus community.</p></div><button type="button" onClick={markAll} disabled={!unread}>Mark all as read</button></div>
 {error&&<div className="error" role="alert">{error}</div>}
 {loading?<p>Loading notifications...</p>:items.length===0?<div className="card empty-notifications"><h3>You are all caught up</h3><p>New campus updates will appear here.</p><Link to="/announcements">View announcements</Link></div>:<div className="notification-list">{items.map(n=><article className={`card notification-item ${n.read?'':'unread'}`} key={n.id}><div className="notification-copy"><div className="notification-title-row"><h3>{n.title}</h3>{!n.read&&<span className="notification-dot" aria-label="Unread"/>}</div><p>{n.message}</p><small>{n.createdAt?new Date(n.createdAt).toLocaleString():''}</small></div>{!n.read&&<button type="button" onClick={()=>markRead(n.id)}>Mark read</button>}{n.type==='ANNOUNCEMENT'&&<Link to="/announcements">View</Link>}</article>)}</div>}</main>;
}
