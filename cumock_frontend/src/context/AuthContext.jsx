import React, { createContext, useState, useEffect, useContext } from 'react';
import authService from '../api/auth';
import axios from 'axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // Function to fetch user data from /api/users/me
// fetchUserData function
  const fetchUserData = async () => {
    try {
      const token = localStorage.getItem('user_token');
      if (!token) {
        setLoading(false);
        return;
      }

      console.log("Fetching user data with token:", token.substring(0, 10) + "...");
      
      const response = await axios.get('http://localhost:8080/api/users/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        withCredentials: true
      });
      
      console.log("Raw API response:", response);
      
      if (response.data) {
        console.log("User data received:", response.data);
        
        // Make sure the role is stored correctly
        const userData = {
          id: response.data.id,
          email: response.data.email, 
          username: response.data.username || '',
          role: response.data.role || 'USER' // Ensure we capture the role
        };
        
        setUser(userData);
        setIsAuthenticated(true);
      }
    } catch (error) {
      console.error('Error fetching user data:', error);
      console.error('Error details:', error.response?.data);
      // Error handling...
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // Load user data when component mounts
    fetchUserData();
  }, []);

  // Rest of your component remains the same...
  
  // For debugging
  useEffect(() => {
    console.log("Current user state:", user);
  }, [user]);

  const login = async (email, password) => {
    try {
      const response = await authService.login(email, password);
      if (response.data.token) {
        localStorage.setItem('user_token', response.data.token);
        setIsAuthenticated(true);
        
        // Fetch user data after successful login
        await fetchUserData();
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login failed:', error);
      throw error; // Re-throw to let component handle it
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
      // Registration successful, but don't login automatically
      return true;
    } catch (error) {
      console.error('Registration failed:', error);
      throw error; // Re-throw to let component handle it
    }
  };

  return (
    <AuthContext.Provider value={{ 
      isAuthenticated, 
      user, 
      loading, 
      login, 
      logout, 
      register,
      refreshUserData: fetchUserData // Expose a way to refresh user data
    }}>
      {!loading && children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);