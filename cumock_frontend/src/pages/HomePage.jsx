import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './HomePage.css';
import UserDebugger from '../components/UserDebugger'; // Import the UserDebugger component

function HomePage() {
  const { isAuthenticated } = useAuth();
    const { user } = useAuth();
    const userId = user?.id;
    console.log('User ID:', userId); // Debugging line to check user ID
    console.log("Authentication status:", isAuthenticated);
  console.log("User object:", user);
    const isAdmin = user && (user.role === 'ADMIN' || user.role === 'ROLE_ADMIN');

  return (
    <div className="homepage">
      <section className="hero">
        <div className="hero-content">
          <h1 className="hero-title">Соревновательное программирование</h1>
          <p className="hero-subtitle">
            Бросьте вызов себе, соревнуйтесь с другими и поднимите свои навыки программирования на новый уровень
          </p>
          {!isAuthenticated && (
            <Link to="/register" className="cta-button">
              Начать путь
            </Link>
          )}
          
          {isAuthenticated && isAdmin && (
            <Link to="/admin" className="cta-button admin-button">
              Панель администратора
            </Link>
          )}
        </div>
      </section>
      
      {/* Debug section - remove in production */}
      {/*
      {isAuthenticated && (
        <UserDebugger />
      )}*/}

      <section className="features">
        <div className="container">
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🏆</div>
              <h3 className="feature-title">PvP Соревнования</h3>
              <p className="feature-description">
                Соревнуйтесь с другими программистами в режиме реального времени
              </p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">📈</div>
              <h3 className="feature-title">Умное отслеживание</h3>
              <p className="feature-description">
                Следите за своим прогрессом с помощью детальной статистики
              </p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🎯</div>
              <h3 className="feature-title">Целевая практика</h3>
              <p className="feature-description">
                Фокусируйтесь на слабых местах с персональными рекомендациями
              </p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🤖</div>
              <h3 className="feature-title">ИИ-обучение</h3>
              <p className="feature-description">
                Получайте умные подсказки и учитесь на своих ошибках быстрее
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="telegram-bot">
        <div className="container">
          <div className="telegram-content">
            <div className="telegram-info">
              <h2 className="section-title">Тренируйтесь где удобно</h2>
              <p className="telegram-description">
                Используйте нашего Telegram бота для подготовки к алгоритмическим собеседованиям в любое время и в любом месте
              </p>
              <a 
                href="https://t.me/cu_mock_bot" 
                target="_blank" 
                rel="noopener noreferrer" 
                className="telegram-button"
              >
                <span className="telegram-icon">📱</span>
                Перейти в Telegram
              </a>
            </div>
            <div className="telegram-preview">
              <div className="preview-card">
                <div className="preview-header">
                  <span className="preview-dot"></span>
                  <span className="preview-dot"></span>
                  <span className="preview-dot"></span>
                </div>
                <div className="preview-content">
                  <p>CU Mock</p>
                  <p>Бот для тренировки алгоритмов и подготовки к собеседованиям</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="how-it-works">
        <div className="container">
          <h2 className="section-title">Как это работает</h2>
          <div className="steps-grid">
            <div className="step-card">
              <div className="step-number">1</div>
              <h3 className="step-title">Выберите задачу</h3>
              <p className="step-description">
                Выберите из множества задач или присоединитесь к PvP битве
              </p>
            </div>
            <div className="step-card">
              <div className="step-number">2</div>
              <h3 className="step-title">Напишите решение</h3>
              <p className="step-description">
                Создайте свое решение на любимом языке программирования
              </p>
            </div>
            <div className="step-card">
              <div className="step-number">3</div>
              <h3 className="step-title">Тестирование</h3>
              <p className="step-description">
                Проверьте код на тестовых примерах и отправьте решение
              </p>
            </div>
            <div className="step-card">
              <div className="step-number">4</div>
              <h3 className="step-title">Развивайтесь</h3>
              <p className="step-description">
                Учитесь на обратной связи и соревнуйтесь с другими
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default HomePage;