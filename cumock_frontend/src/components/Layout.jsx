import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Layout.css';

function Layout({ children }) {
  const { isAuthenticated, logout } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="layout-container">
      <nav className="navbar">
        <Link to="/" className="navbar-brand">
          <img src="/logo.svg" alt="CU Mock Logo" />
          CU Mock
        </Link>
        <div className="navbar-nav">
          {isAuthenticated && (
            <>
              <Link to="/battle" className="nav-link">Battle</Link>
              <Link to="/problems" className="nav-link">Problems</Link>
              <Link to="/progress" className="nav-link">Progress</Link>
              <Link to="/scheduler" className="nav-link">Scheduler</Link>
              <Link to="/telegram-bot" className="nav-link">Telegram Bot</Link>
            </>
          )}
        </div>
        <div className="navbar-auth">
          {isAuthenticated ? (
            <button onClick={handleLogout} className="auth-button logout">Выйти</button>
          ) : (
            <>
              <Link to="/login" className="auth-button login">Log In</Link>
              <Link to="/register" className="auth-button register">Get Started</Link>
            </>
          )}
        </div>
      </nav>
      <main className="main-content">
        {children}
      </main>
      <footer>
        <p>&copy; 2024 CU Mock. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default Layout;