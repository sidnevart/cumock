import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './LoginPage.css'; // Import the new CSS file

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await login(username, password);
      navigate('/'); // Redirect to home page on successful login
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
    }
  };

  return (
    <div className="auth-container"> {/* Apply container class */}
      <div className="auth-box"> {/* Apply auth box class */}
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group"> {/* Apply form group class */}
            <label htmlFor="username">Username:</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group"> {/* Apply form group class */}
            <label htmlFor="password">Password:</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {error && <p className="error-message">{error}</p>} {/* Apply error message class */}
          <button type="submit" className="auth-button primary">Login</button> {/* Apply button classes */}
        </form>
        <Link to="/register" className="auth-link">Don't have an account? Register</Link> {/* Apply auth link class */}
      </div>
    </div>
  );
}

export default LoginPage;