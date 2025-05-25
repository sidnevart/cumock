import axios from 'axios';

const API_URL = 'http://localhost:8080/api/pvp';

// Updated function for creating a challenge - now problems are selected randomly on the backend
const createChallenge = (challengerId, challengedId) => {
  const requestBody = {
    challengerId,
    challengedId
  };
  return axios.post(API_URL + '/challenge', requestBody, {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

// Remaining functions stay the same
const getUserChallenges = (userId, status) => {
  return axios.get(API_URL + '/challenges', {
    params: {
      userId: userId,
      status: status
    },
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const acceptChallenge = (contestId, userId) => {
  return axios.post(`${API_URL}/challenge/${contestId}/accept`, null, {
    params: {
      userId: userId
    },
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const rejectChallenge = (contestId, userId) => {
  return axios.post(`${API_URL}/challenge/${contestId}/reject`, null, {
    params: {
      userId: userId
    },
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

const getContestDetails = (contestId) => {
  return axios.get(`${API_URL}/contest/${contestId}`, {
    headers: { 'Authorization': 'Bearer ' + localStorage.getItem('user_token') }
  });
};

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
};

const pvpService = {
  createChallenge,
  getUserChallenges,
  acceptChallenge,
  rejectChallenge,
  getContestDetails,
  getContestProgress,
};

export default pvpService;