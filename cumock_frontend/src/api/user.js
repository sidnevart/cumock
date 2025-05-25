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

// Функция для получения данных текущего пользователя
const getCurrentUser = () => {
  return axios.get(API_URL + '/me', {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') },
    withCredentials: true
  });
};

// Функция для проверки, является ли текущий пользователь администратором
const checkIfAdmin = () => {
  return axios.get(API_URL + '/is_admin', {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const userService = {
  searchUsers,
  getCurrentUser,
  checkIfAdmin
};

export default userService;