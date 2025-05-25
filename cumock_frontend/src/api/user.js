import axios from 'axios';

const API_URL = 'http://localhost:8080/api/users'; // URL для эндпоинтов UserController

// Функция для поиска пользователей по username
const searchUsers = (username) => {
  return axios.get(API_URL + '/search', {
    params: {
      username: username
    },
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const userService = {
  searchUsers,
  // Можно добавить другие функции, например, для получения информации о пользователе по ID
};

export default userService; 