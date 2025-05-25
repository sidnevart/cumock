import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import problemService from '../api/problems';
import pvpService from '../api/pvp'; // Создадим этот сервис позже
import userService from '../api/user'; // Импорт сервиса пользователей

function BattlePage() {
  const { user } = useAuth();
  const userId = user?.id;

  const [problems, setProblems] = useState([]);
  const [selectedProblem1, setSelectedProblem1] = useState('');
  const [selectedProblem2, setSelectedProblem2] = useState('');
  const [searchUsername, setSearchUsername] = useState(''); // Для поля поиска
  const [searchResults, setSearchResults] = useState([]); // Результаты поиска
  const [selectedChallengedUser, setSelectedChallengedUser] = useState(null); // Выбранный для вызова пользователь
  const [challenges, setChallenges] = useState([]); // Входящие вызовы
  const [loading, setLoading] = useState(true);
  const [challengeLoading, setChallengeLoading] = useState(false);
  const [searchLoading, setSearchLoading] = useState(false); // Состояние загрузки для поиска

  useEffect(() => {
    if (!userId) return; // Не загружаем данные, если пользователь не авторизован
    fetchData();
    // Возможно, потребуется polling или WebSockets для обновления списка вызовов в реальном времени
  }, [userId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      // Загружаем список проблем для выбора
      const problemsResponse = await problemService.getAllProblems(); // Используем getAllProblems без пагинации для простоты выбора
      setProblems(problemsResponse.data);

      // Загружаем входящие вызовы для текущего пользователя
      const challengesResponse = await pvpService.getUserChallenges(userId, 'CHALLENGE'); // Создадим этот метод в pvpService
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
      // Фильтруем текущего пользователя из результатов поиска
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
    if (!userId || !selectedProblem1 || !selectedProblem2 || !selectedChallengedUser) {
      alert('Пожалуйста, выберите задачи и пользователя для вызова.');
      return;
    }
    setChallengeLoading(true);
    try {
      await pvpService.createChallenge(userId, selectedChallengedUser.id, selectedProblem1, selectedProblem2);
      alert('Вызов отправлен!');
      // Сброс полей после отправки
      setSearchUsername('');
      setSearchResults([]);
      setSelectedChallengedUser(null);
      setSelectedProblem1('');
      setSelectedProblem2('');
      fetchData(); // Обновить список вызовов
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
      // Добавим перенаправление на страницу матча после принятия
      const acceptedContest = await pvpService.acceptChallenge(contestId, userId);
      alert('Вызов принят! Матч начался.');
      // TODO: Перенаправить на страницу активного матча с contestId
      fetchData(); // Обновить список вызовов
    } catch (error) {
      console.error('Error accepting challenge:', error);
      alert(`Ошибка при принятии вызова: ${error.response?.data || error.message}`);
    }
  };

  const handleRejectChallenge = async (contestId) => {
    if (!userId) return;
    try {
      await pvpService.rejectChallenge(contestId, userId);
      alert('Вызов отклонен.');
      fetchData(); // Обновить список вызовов
    } catch (error) {
      console.error('Error rejecting challenge:', error);
      alert(`Ошибка при отклонении вызова: ${error.response?.data || error.message}`);
    }
  };

  if (loading) {
    return <div>Загрузка данных для PvP...</div>;
  }

  if (!user) {
    return <div>Пожалуйста, войдите, чтобы участвовать в PvP.</div>;
  }

  return (
    <div className="container">
      <h1>PvP Battle</h1>

      {/* Раздел создания вызова */}
      <div>
        <h2>Создать вызов</h2>
        <div>
          <label htmlFor="searchUsername">Найти пользователя для вызова:</label>
          <input
            type="text"
            id="searchUsername"
            value={searchUsername}
            onChange={(e) => setSearchUsername(e.target.value)}
          />
          <button onClick={handleSearchUsers} disabled={searchLoading}>Найти</button>
        </div>

        {/* Результаты поиска */}
        {searchLoading && <p>Поиск...</p>}
        {!searchLoading && searchResults.length > 0 && (
            <div>
                <h3>Результаты поиска:</h3>
                <ul>
                    {searchResults.map(foundUser => (
                        <li key={foundUser.id} onClick={() => setSelectedChallengedUser(foundUser)} style={{ cursor: 'pointer', fontWeight: selectedChallengedUser?.id === foundUser.id ? 'bold' : 'normal' }}>
                            {foundUser.username} ({foundUser.email})
                        </li>
                    ))}
                </ul>
            </div>
        )}
         {!searchLoading && searchUsername && searchResults.length === 0 && <p>Пользователь не найден.</p>}

        {/* Выбранный для вызова пользователь */}
        {selectedChallengedUser && (
            <div>
                <p>Выбран пользователь для вызова: <strong>{selectedChallengedUser.username}</strong></p>
            </div>
        )}

        <div>
          <label htmlFor="problem1">Ваша задача:</label>
          <select
            id="problem1"
            value={selectedProblem1}
            onChange={(e) => setSelectedProblem1(e.target.value)}
          >
            <option value="">Выберите задачу</option>
            {problems.map(problem => (
              <option key={problem.id} value={problem.id}>{problem.title}</option>
            ))}
          </select>
        </div>
        <div>
          <label htmlFor="problem2">Задача противника:</label>
          <select
            id="problem2"
            value={selectedProblem2}
            onChange={(e) => setSelectedProblem2(e.target.value)}
          >
             <option value="">Выберите задачу</option>
            {problems.map(problem => (
              <option key={problem.id} value={problem.id}>{problem.title}</option>
            ))}
          </select>
        </div>
        <button onClick={handleCreateChallenge} disabled={challengeLoading || !selectedChallengedUser || !selectedProblem1 || !selectedProblem2}>Отправить вызов</button>
      </div>

      {/* Раздел входящих вызовов */}
      <div>
          <h2>Входящие вызовы</h2>
          {challenges.length === 0 ? (
              <p>Нет входящих вызовов.</p>
          ) : (
              <ul>
                  {challenges.map(challenge => (
                      <li key={challenge.id}>
                          Вызов от пользователя {challenge.user1Id} ({challenge.status})
                          <button onClick={() => handleAcceptChallenge(challenge.id)}>Принять</button>
                          <button onClick={() => handleRejectChallenge(challenge.id)}>Отклонить</button>
                      </li>
                  ))}
              </ul>
          )}
      </div>

      {/* Здесь можно добавить список активных матчей и историю */}

    </div>
  );
}

export default BattlePage; 