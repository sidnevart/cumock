import React, { createContext, useState, useEffect, useContext } from 'react';
import authService from '../api/auth';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null); // Здесь можно хранить информацию о пользователе
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Проверяем токен при загрузке приложения
    const currentUser = authService.getCurrentUser();
    if (currentUser.isAuthenticated) {
      setIsAuthenticated(true);
      // В реальном приложении здесь бы загружалась информация о пользователе из /api/me
      // setUser(currentUser.user); // Если у currentUser есть поле user
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    try {
      const response = await authService.login(email, password);
      if (response.data.token) {
        localStorage.setItem('user_token', response.data.token);
        setIsAuthenticated(true);
        // Здесь можно загрузить информацию о пользователе после успешного входа
        // const userInfo = await api.getUserInfo(); // Пример
        // setUser(userInfo);
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login failed:', error);
      return false;
    }
  };

  const logout = () => {
    authService.logout();
    setIsAuthenticated(false);
    setUser(null);
  };

  const register = async (username, email, password) => {
    try {
      await authService.register(username, email, password);
      // После регистрации можно автоматически залогинить или просто сообщить об успехе
      return true;
    } catch (error) {
       console.error('Registration failed:', error);
       return false;
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, loading, login, logout, register }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext); 