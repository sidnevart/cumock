import axios from 'axios';

const API_URL = 'http://localhost:8080/api/auth'; // Убедитесь, что это правильный URL вашего бекенда

const register = (username, email, password) => {
  return axios.post(API_URL + '/register', {
    username,
    email,
    password,
  });
};

const login = (email, password) => {
  return axios.post(API_URL + '/login', {
    email,
    password,
  });
};

const logout = () => {
  // В данном случае просто удаляем токен из локального хранилища
  localStorage.removeItem('user_token');
};

const getCurrentUser = () => {
  // Получаем информацию о пользователе, возможно, из токена или отдельного запроса к /api/me
  // Для простоты пока просто проверим наличие токена
  const token = localStorage.getItem('user_token');
  // В реальном приложении здесь бы декодировался JWT или делался запрос к /api/me
  return token ? { isAuthenticated: true, token: token } : { isAuthenticated: false };
};

const authService = {
  register,
  login,
  logout,
  getCurrentUser,
};

export default authService; 