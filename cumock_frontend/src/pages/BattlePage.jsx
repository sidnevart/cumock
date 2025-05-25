import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import problemService from '../api/problems';
import pvpService from '../api/pvp';
import userService from '../api/user';
import './BattlePage.css';

function BattlePage() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const userId = user?.id;

  const [searchUsername, setSearchUsername] = useState('');
  const [searchResults, setSearchResults] = useState([]);
  const [selectedChallengedUser, setSelectedChallengedUser] = useState(null);
  const [challenges, setChallenges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [challengeLoading, setChallengeLoading] = useState(false);
  const [searchLoading, setSearchLoading] = useState(false);

  useEffect(() => {
    if (!userId) return;
    fetchData();
    // Poll for new challenges every 30 seconds
    const interval = setInterval(() => fetchData(), 30000);
    return () => clearInterval(interval);
  }, [userId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      // Fetch incoming challenges for the current user
      const challengesResponse = await pvpService.getUserChallenges(userId, 'CHALLENGE');
      setChallenges(challengesResponse.data);
    } catch (error) {
      console.error('Error fetching battle data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchUsers = async () => {
    if (!searchUsername) return;
    setSearchLoading(true);
    try {
      const response = await userService.searchUsers(searchUsername);
      // Filter out the current user from search results
      const filteredResults = response.data.filter(u => u.id !== userId);
      setSearchResults(filteredResults);
    } catch (error) {
      console.error('Error searching users:', error);
      setSearchResults([]);
    } finally {
      setSearchLoading(false);
    }
  };

  const handleCreateChallenge = async () => {
    if (!userId || !selectedChallengedUser) {
      alert('Пожалуйста, выберите пользователя для вызова.');
      return;
    }
    
    setChallengeLoading(true);
    try {
      // The backend will select random problems for both users
      const response = await pvpService.createChallenge(userId, selectedChallengedUser.id);
      alert('Вызов отправлен! Ваша задача и задача соперника будут выбраны случайным образом.');
      
      // Reset form after sending
      setSearchUsername('');
      setSearchResults([]);
      setSelectedChallengedUser(null);
      fetchData();
    } catch (error) {
      console.error('Error creating challenge:', error);
      alert(`Ошибка при создании вызова: ${error.response?.data || error.message}`);
    } finally {
      setChallengeLoading(false);
    }
  };

  const handleAcceptChallenge = async (contestId) => {
    if (!userId) return;
    try {
      await pvpService.acceptChallenge(contestId, userId);
      alert('Вызов принят! Матч начался.');
      // Navigate to the contest page
      navigate(`/pvp/contest/${contestId}`);
    } catch (error) {
      console.error('Error accepting challenge:', error);
      alert(`Ошибка при принятии вызова: ${error.response?.data || error.message}`);
      fetchData();
    }
  };

  const handleRejectChallenge = async (contestId) => {
    if (!userId) return;
    try {
      await pvpService.rejectChallenge(contestId, userId);
      alert('Вызов отклонен.');
      fetchData();
    } catch (error) {
      console.error('Error rejecting challenge:', error);
      alert(`Ошибка при отклонении вызова: ${error.response?.data || error.message}`);
    }
  };

  if (loading && challenges.length === 0) {
    return <div className="container loading">Загрузка данных для PvP...</div>;
  }

  if (!user) {
    return <div className="container">Пожалуйста, войдите, чтобы участвовать в PvP.</div>;
  }

  return (
    <div className="container">
      <h1>PvP Battle</h1>

      {/* Challenge creation section */}
      <section className="challenge-section">
        <h2>Создать вызов</h2>
        <div className="info-box">
          <p>Внимание: Задачи для обоих участников будут выбраны системой случайным образом.</p>
        </div>
        
        <div className="search-container">
          <label htmlFor="searchUsername">Найти пользователя для вызова:</label>
          <div className="search-input-group">
            <input
              type="text"
              id="searchUsername"
              value={searchUsername}
              onChange={(e) => setSearchUsername(e.target.value)}
              placeholder="Введите имя пользователя"
            />
            <button 
              onClick={handleSearchUsers} 
              disabled={searchLoading || !searchUsername.trim()}
              className="search-button"
            >
              {searchLoading ? 'Поиск...' : 'Найти'}
            </button>
          </div>
        </div>

        {/* Search results */}
        {searchLoading && <p className="loading-text">Поиск пользователей...</p>}
        
        {!searchLoading && searchResults.length > 0 && (
          <div className="search-results">
            <h3>Результаты поиска:</h3>
            <ul>
              {searchResults.map(foundUser => (
                <li 
                  key={foundUser.id} 
                  onClick={() => setSelectedChallengedUser(foundUser)}
                  className={selectedChallengedUser?.id === foundUser.id ? 'selected' : ''}
                >
                  <span className="username">{foundUser.username}</span>
                  <span className="email">({foundUser.email})</span>
                  {selectedChallengedUser?.id === foundUser.id && 
                    <span className="selected-mark">✓</span>
                  }
                </li>
              ))}
            </ul>
          </div>
        )}
        
        {!searchLoading && searchUsername && searchResults.length === 0 && 
          <p className="no-results">Пользователь не найден.</p>
        }

        {/* Selected user info */}
        {selectedChallengedUser && (
          <div className="selected-user">
            <p>Выбран пользователь для вызова: <strong>{selectedChallengedUser.username}</strong></p>
          </div>
        )}

        <button 
          onClick={handleCreateChallenge} 
          disabled={challengeLoading || !selectedChallengedUser}
          className="submit-button"
        >
          {challengeLoading ? 'Отправка...' : 'Отправить вызов'}
        </button>
      </section>

      {/* Incoming challenges section */}
      <section className="challenges-section">
        <h2>Входящие вызовы</h2>
        {challenges.length === 0 ? (
          <p className="no-challenges">Нет входящих вызовов.</p>
        ) : (
          <ul className="challenge-list">
            {challenges.map(challenge => (
              <li key={challenge.id} className="challenge-item">
                <div className="challenge-info">
                  <span className="challenger">Вызов от: {challenge.user1Username || challenge.user1Id}</span>
                  <span className="status">Статус: {challenge.status}</span>
                  <span className="expires">Истекает: {new Date(challenge.challengeExpiresAt).toLocaleString()}</span>
                </div>
                <div className="challenge-actions">
                  <button onClick={() => handleAcceptChallenge(challenge.id)} className="accept-button">
                    Принять
                  </button>
                  <button onClick={() => handleRejectChallenge(challenge.id)} className="reject-button">
                    Отклонить
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  );
}

export default BattlePage;