import React, { useCallback, useEffect, useState } from 'react';
import api from '../api/axios.js';
import { useAuth } from '../context/AuthContext.jsx';

export default function Forum() {
  const { user } = useAuth();
  const [questions, setQuestions] = useState([]);
  const [form, setForm] = useState({ title: '', content: '' });
  const [answerDrafts, setAnswerDrafts] = useState({});
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(async () => {
    try {
      setError('');
      const res = await api.get('/questions');
      setQuestions(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load forum.');
    }
  }, []);

  useEffect(() => { load(); }, [load]);

  const handleAsk = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    setError('');
    setSuccess('');
    try {
      await api.post('/questions', {
        title: form.title.trim(),
        content: form.content.trim(),
      });
      setForm({ title: '', content: '' });
      setSuccess('Your question has been posted.');
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to post question.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleAnswer = async (questionId) => {
    const content = (answerDrafts[questionId] || '').trim();
    if (!content) {
      setError('Please write an answer before submitting.');
      return;
    }

    setError('');
    setSuccess('');
    try {
      await api.post(`/questions/${questionId}/answers`, { content });
      setAnswerDrafts((current) => ({ ...current, [questionId]: '' }));
      setSuccess('Your answer has been posted.');
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to post answer.');
    }
  };

  const handleDeleteQuestion = async (questionId) => {
    if (!window.confirm('Delete this question and all its answers?')) return;
    setError('');
    setSuccess('');
    try {
      await api.delete(`/questions/${questionId}`);
      setSuccess('Question deleted.');
      await load();
    } catch (err) {
      setError(err.response?.status === 403
        ? 'You can only delete your own questions.'
        : (err.response?.data?.message || 'Failed to delete question.'));
    }
  };

  const handleDeleteAnswer = async (questionId, answerId) => {
    if (!window.confirm('Delete this answer?')) return;
    setError('');
    setSuccess('');
    try {
      await api.delete(`/questions/${questionId}/answers/${answerId}`);
      setSuccess('Answer deleted.');
      await load();
    } catch (err) {
      setError(err.response?.status === 403
        ? 'You can only delete your own answers.'
        : (err.response?.data?.message || 'Failed to delete answer.'));
    }
  };

  const canDelete = (ownerId) => user?.role === 'ADMIN' || user?.userId === ownerId;
  const formatDate = (value) => value
    ? new Date(value).toLocaleString()
    : '';

  return (
    <div className="container">
      <h2>Discussion Forum</h2>
      {error && <div className="error" role="alert">{error}</div>}
      {success && <div className="success" role="status">{success}</div>}

      <div className="card">
        <h3>Ask a Question</h3>
        <form onSubmit={handleAsk}>
          <input
            placeholder="Question title"
            value={form.title}
            maxLength={150}
            onChange={(e) => setForm({ ...form, title: e.target.value })}
            required
          />
          <textarea
            placeholder="Describe your question..."
            rows="4"
            value={form.content}
            maxLength={3000}
            onChange={(e) => setForm({ ...form, content: e.target.value })}
            required
          />
          <button className="primary" type="submit" disabled={submitting}>
            {submitting ? 'Posting...' : 'Post Question'}
          </button>
        </form>
      </div>

      {questions.length === 0 ? (
        <div className="card">
          <p>No questions yet. Be the first to start a discussion.</p>
        </div>
      ) : questions.map((question) => (
        <div className="card" key={question.id}>
          <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12, alignItems: 'flex-start' }}>
            <div>
              <h3>{question.title}</h3>
              <p style={{ whiteSpace: 'pre-wrap' }}>{question.content}</p>
              <p style={{ color: '#4b5563', fontSize: 13, marginTop: 6 }}>
                Asked by {question.askedByName}
                {question.createdAt && ` · ${formatDate(question.createdAt)}`}
              </p>
            </div>
            {canDelete(question.askedById) && (
              <button type="button" className="secondary"
                onClick={() => handleDeleteQuestion(question.id)}>
                Delete
              </button>
            )}
          </div>

          <div style={{ marginTop: 12, paddingLeft: 12, borderLeft: '2px solid #e5e7eb' }}>
            <h4>Answers ({question.answers?.length || 0})</h4>
            {question.answers?.length ? question.answers.map((answer) => (
              <div key={answer.id} style={{ marginBottom: 12 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', gap: 10 }}>
                  <div>
                    <strong>{answer.answeredByName}:</strong> {answer.content}
                    {answer.createdAt && (
                      <div style={{ color: '#6b7280', fontSize: 12 }}>
                        {formatDate(answer.createdAt)}
                      </div>
                    )}
                  </div>
                  {canDelete(answer.answeredById) && (
                    <button type="button" className="secondary"
                      onClick={() => handleDeleteAnswer(question.id, answer.id)}>
                      Delete
                    </button>
                  )}
                </div>
              </div>
            )) : <p>No answers yet.</p>}
          </div>

          <div style={{ marginTop: 10, display: 'flex', gap: 8 }}>
            <input
              aria-label={`Answer question: ${question.title}`}
              placeholder="Write an answer..."
              value={answerDrafts[question.id] || ''}
              maxLength={3000}
              onChange={(e) => setAnswerDrafts((current) => ({
                ...current, [question.id]: e.target.value,
              }))}
              style={{ marginBottom: 0 }}
            />
            <button className="primary" type="button"
              style={{ width: 'auto', padding: '10px 16px' }}
              onClick={() => handleAnswer(question.id)}>
              Reply
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
