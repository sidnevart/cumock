import axiosInstance from '../utils/axiosConfig';

// Function to get problem list (with filtering)
const getAllProblems = (filters = {}) => {
  return axiosInstance.get('/api/problems', { params: filters });
};

// Function for paginated problems
const getPagedProblems = (page = 0, size = 10, sortBy = 'id') => {
  return axiosInstance.get('/api/problems/paged', { 
    params: { page, size, sortBy }
  });
};

// Function to get problem details - ИЗМЕНЕНО! Использует правильный endpoint
const getProblemById = (id) => {
  console.log('Token used for problem fetch:', localStorage.getItem('user_token')?.substring(0, 15) + '...');
  // Изменен URL согласно бэкенд-контроллеру
  return axiosInstance.get(`/api/problems/${id}/details`);
};

// Function to get test cases for a problem
const getProblemTestCases = (id) => {
  return axiosInstance.get(`/api/problems/${id}/tests`);
};

const problemService = {
  getAllProblems,
  getPagedProblems,
  getProblemById,
  getProblemTestCases
};

export default problemService;