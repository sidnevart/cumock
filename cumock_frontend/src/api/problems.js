import axios from 'axios';

const API_URL = 'http://localhost:8080'; // Базовый URL вашего бекенда

// Функция для получения списка проблем (с фильтрацией)
const getAllProblems = (filters = {}) => {
  return axios.get(API_URL + '/problems', {
    params: filters,
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

// Функция для получения списка проблем с пагинацией
const getPagedProblems = (page = 0, size = 10, sortBy = 'id') => {
  return axios.get(API_URL + '/problems/paged', {
    params: {
      page,
      size,
      sortBy
    },
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

// Функция для получения деталей конкретной проблемы
const getProblemById = (id) => {
  return axios.get(`${API_URL}/problems/${id}`, {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const problemService = {
  getAllProblems,
  getPagedProblems,
  getProblemById,
};

export default problemService; 