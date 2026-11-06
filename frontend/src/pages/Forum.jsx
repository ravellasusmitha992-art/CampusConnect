import React, { useEffect, useState } from 'react';
import api from '../api/axios.js';

export default function Forum() {
  const [questions, setQuestions] = useState([]);
  const [form, setForm] = useState({ title: '', content: '' });
  const [answerDrafts, setAnswerDrafts] = useState({});
  const [error, setError] = useState('');

  const load = async () => {
    try {
      const res = await api.get('/questions');
      setQuestions(res.data);
    } catch (err) {
      setError('Failed to load forum.');
    }
  };

  useEffect(() => { load(); }, []);

  const handleAsk = async (e) => {
    e.preventDefault();
    try {
      await api.post('/questions', form);
      setForm({ title: '', content: '' });
      load();
    } catch (err) {
      setError('Failed to post question.');
    }
  };

  const handleAnswer = async (id) => {
    const content = answerDrafts[id];
    if (!content) return;
    try {
      await api.post(`/questions/${id}/answers`, { content });
      setAnswerDrafts({ ...answerDrafts, [id]: '' });
      load();
    } catch (err) {
      setError('Failed to post answer.');
    }
  };

  return (
    <div className="container">
      <h2>Discussion Forum</h2>
      {error && <div className="error">{error}</div>}

      <div className="card">
        <h3>Ask a Question</h3>
        <form onSubmit={handleAsk}>
          <input placeholder="Title" value={form.title}
                 onChange={(e) => setForm({ ...form, title: e.target.value })} required />
          <textarea placeholder="Details" rows="3" value={form.content}
                    onChange={(e) => setForm({ ...form, content: e.target.value })} required />
          <button className="primary" type="submit">Post Question</button>
        </form>
      </div>

      {questions.map((q) => (
        <div className="card" key={q.id}>
          <h3>{q.title}</h3>
          <p>{q.content}</p>
          <p style={{ color: '#4b5563', fontSize: 13, marginTop: 6 }}>
            Asked by {q.askedBy?.fullName}
          </p>

          <div style={{ marginTop: 12, paddingLeft: 12, borderLeft: '2px solid #e5e7eb' }}>
            {q.answers?.map((a) => (
              <div key={a.id} style={{ marginBottom: 8 }}>
                <strong>{a.answeredBy?.fullName}:</strong> {a.content}
              </div>
            ))}
          </div>

          <div style={{ marginTop: 10, display: 'flex', gap: 8 }}>
            <input
              placeholder="Write an answer..."
              value={answerDrafts[q.id] || ''}
              onChange={(e) => setAnswerDrafts({ ...answerDrafts, [q.id]: e.target.value })}
              style={{ marginBottom: 0 }}
            />
            <button className="primary" style={{ width: 'auto', padding: '10px 16px' }}
                    onClick={() => handleAnswer(q.id)}>
              Reply
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
