import axios from 'axios';

const API_URL = 'http://localhost:8080/api'; // Add the base URL

const adminService = {
  // User management
  getAllUsers: () => axios.get(`${API_URL}/admin/users`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }
  }),
  
  updateUserRole: (userId, role) => axios.put(`${API_URL}/admin/users/${userId}/role`, 
    { role }, 
    { headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }}
  ),
  
  // Problem management
  getAllProblems: (topic, title, difficulty) => {
    let url = `${API_URL}/admin/problems`;
    const params = new URLSearchParams();
    if (topic) params.append('topic', topic);
    if (title) params.append('title', title);
    if (difficulty) params.append('difficulty', difficulty);
    
    const queryString = params.toString();
    if (queryString) url = `${url}?${queryString}`;
    
    return axios.get(url, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }
    });
  },
  
  getPagedProblems: (page = 0, size = 10, sortBy = 'id') => 
    axios.get(`${API_URL}/admin/paged?page=${page}&size=${size}&sortBy=${sortBy}`, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }
    }),
  
  createProblem: (problemData) => axios.post(`${API_URL}/admin/problems`, 
    problemData, 
    { headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }}
  ),
  
  updateProblem: (id, problemData) => axios.put(`${API_URL}/admin/problems/${id}`, 
    problemData, 
    { headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }}
  ),
  
  deleteProblem: (id) => axios.delete(`${API_URL}/admin/problems/${id}`, {
    headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }
  }),
  
  // Test case management
  addTestCase: (problemId, testCaseData) => 
    axios.post(`${API_URL}/admin/problems/${problemId}/tests`, 
      testCaseData, 
      { headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }}
    ),
  
  updateTestCase: (problemId, testId, testCaseData) => 
    axios.put(`${API_URL}/admin/problems/${problemId}/tests/${testId}`, 
      testCaseData, 
      { headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }}
    ),
  
  deleteTestCase: (problemId, testId) => 
    axios.delete(`${API_URL}/admin/problems/${problemId}/tests/${testId}`, {
      headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }
    })
};

export default adminService;