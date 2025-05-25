import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './Layout.css';

function Layout({ children }) {
  const { isAuthenticated, logout } = useAuth();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setIsMobileMenuOpen(false);
  };

  const toggleMobileMenu = () => {
    setIsMobileMenuOpen(!isMobileMenuOpen);
  };

  return (
    <div className="layout-container">
      <nav className="navbar">
        <div className="navbar-content">
          <Link to="/" className="navbar-brand" onClick={() => setIsMobileMenuOpen(false)}>
            <img src="/logo.svg" alt="CU Mock Logo" />
          </Link>

          <button className="mobile-menu-button" onClick={toggleMobileMenu}>
            <span className="material-icons">
              {isMobileMenuOpen ? 'close' : 'menu'}
            </span>
          </button>

          <div className={`navbar-nav ${isMobileMenuOpen ? 'active' : ''}`}>
            {isAuthenticated && (
              <>
                <Link to="/battle" className="nav-link" onClick={() => setIsMobileMenuOpen(false)}>
                  Соревнования
                </Link>
                <Link to="/problems" className="nav-link" onClick={() => setIsMobileMenuOpen(false)}>
                  Задачи
                </Link>
                <Link to="/progress" className="nav-link" onClick={() => setIsMobileMenuOpen(false)}>
                  Прогресс
                </Link>
                <Link to="/scheduler" className="nav-link" onClick={() => setIsMobileMenuOpen(false)}>
                  Расписание
                </Link>
              </>
            )}
          </div>

          <div className="navbar-auth">
            {isAuthenticated ? (
              <button onClick={handleLogout} className="auth-button register">
                Выйти
              </button>
            ) : (
              <>
                <Link to="/login" className="auth-button login" onClick={() => setIsMobileMenuOpen(false)}>
                  Войти
                </Link>
                <Link to="/register" className="auth-button register" onClick={() => setIsMobileMenuOpen(false)}>
                  Регистрация
                </Link>
              </>
            )}
          </div>
        </div>
      </nav>

      <main className="main-content">
        {children}
      </main>

      <footer>
        <div className="footer-content">
          <p>&copy; 2024 CU Mock. Все права защищены.</p>
        </div>
      </footer>
    </div>
  );
}

export default Layout;