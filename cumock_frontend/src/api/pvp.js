import axios from 'axios';

const API_URL = 'http://localhost:8080/api/pvp'; // URL для эндпоинтов PvPController

// Функция для создания вызова
const createChallenge = (challengerId, challengedId, problem1Id, problem2Id) => {
  const requestBody = {
    challengerId,
    challengedId,
    problem1Id,
    problem2Id,
  };
  return axios.post(API_URL + '/challenge', requestBody, {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

// Функция для получения вызовов пользователя (входящих или других статусов)
const getUserChallenges = (userId, status) => {
    return axios.get(API_URL + '/challenges', {
        params: {
            userId: userId,
            status: status // Например, 'CHALLENGE', 'ONGOING', 'FINISHED'
        },
         headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
    });
};

// Функция для принятия вызова
const acceptChallenge = (contestId, userId) => {
     return axios.post(`${API_URL}/challenge/${contestId}/accept`, null, {
         params: {
             userId: userId
         },
         headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
     });
};

// Функция для отклонения вызова
const rejectChallenge = (contestId, userId) => {
    return axios.post(`${API_URL}/challenge/${contestId}/reject`, null, {
         params: {
             userId: userId
         },
         headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
     });
};

// Функция для получения деталей конкретного контеста
const getContestDetails = (contestId) => {
    return axios.get(`${API_URL}/contest/${contestId}`, {
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
    });
};

// Функция для получения прогресса PvP матча (потребуется WebSockets для реального времени)
const getContestProgress = (contestId, userId, problemId, isSubmit) => {
     return axios.get(API_URL + '/progress', {
         params: {
             contestId: contestId,
             userId: userId,
             problemId: problemId,
             isSubmit: isSubmit
         },
         headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
     });
}

const pvpService = {
  createChallenge,
  getUserChallenges,
  acceptChallenge,
  rejectChallenge,
  getContestDetails,
  getContestProgress,
};

export default pvpService; 