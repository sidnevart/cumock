import axios from 'axios';

const API_URL = 'http://localhost:8080/api/code'; // URL для эндпоинтов CodeSandboxController

// Функция для выполнения кода против примеров тестов
const runCode = (userId, problemId, language, code, contestId = null) => {
  const requestBody = {
    userId: userId,
    problemId: problemId,
    language: language,
    code: code,
    pvp: contestId !== null, // Указываем pvp: true если есть contestId
    contestId: contestId, // contestId опционален
  };
  return axios.post(API_URL + '/run', requestBody, {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

// Функция для отправки решения против всех тестов
const submitCode = (userId, problemId, language, code, contestId = null) => {
   const requestBody = {
    userId: userId,
    problemId: problemId,
    language: language,
    code: code,
    pvp: contestId !== null, // Указываем pvp: true если есть contestId
    contestId: contestId, // contestId опционален
  };
  return axios.post(API_URL + '/submit', requestBody, {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const codeService = {
  runCode,
  submitCode,
};

export default codeService; 