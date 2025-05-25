import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function ProtectedRoute({ element, requiredRole }) {
  const { isAuthenticated, user, loading } = useAuth();
  
  // If still loading auth status, show loading
  if (loading) {
    return <div className="loading">Checking authentication...</div>;
  }
  
  // Check if user is authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }
  
  // If there's a required role, check for it
  if (requiredRole && user) {
    const hasRole = user.role === requiredRole || 
                   (requiredRole === 'ADMIN' && user.role === 'ROLE_ADMIN') ||
                   (requiredRole === 'ROLE_ADMIN' && user.role === 'ADMIN');
    
    if (!hasRole) {
      return <Navigate to="/" />;
    }
  }
  
  // User is authenticated (and has required role if specified)
  return element;
}

export default ProtectedRoute;