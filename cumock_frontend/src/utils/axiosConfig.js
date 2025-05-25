import axios from 'axios';

const axiosInstance = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true
});

axiosInstance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('user_token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
      
      // Логируем только первые 10 символов токена для безопасности
      console.log(`API Request to: ${config.baseURL}${config.url} with token: ${token.substring(0, 10)}...`);
    } else {
      console.warn(`API Request to: ${config.baseURL}${config.url} without authentication token!`);
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// Улучшенная обработка ошибок
axiosInstance.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    // Подробная информация об ошибке
    if (error.response) {
      const { status, data } = error.response;
      
      console.error(`API Error (${status}):`, data || 'No response data');
      
      // Определение типа ошибки и соответствующие действия
      if (status === 401) {
        console.error("Authentication failed - your session may have expired");
        
        // Можно автоматически перенаправить на страницу логина
        // window.location.href = '/login';
      } 
      else if (status === 403) {
        console.error("Access forbidden - you don't have permission to access this resource");
        console.error(`Request URL that caused 403: ${error.config.url}`);
        console.error(`Method: ${error.config.method.toUpperCase()}`);
      }
      else if (status === 404) {
        console.error(`Resource not found at: ${error.config.url}`);
      }
    } else if (error.request) {
      console.error("No response received from server:", error.request);
    } else {
      console.error("Error setting up request:", error.message);
    }
    
    return Promise.reject(error);
  }
);

export default axiosInstance;