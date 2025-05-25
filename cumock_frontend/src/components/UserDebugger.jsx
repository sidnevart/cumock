import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

function UserDebugger() {
  const { isAuthenticated, user, refreshUserData, loading } = useAuth();
  const [apiResponse, setApiResponse] = useState(null);

  const testUserEndpoint = async () => {
    try {
      const token = localStorage.getItem('user_token');
      const response = await axios.get('http://localhost:8080/api/users/me', {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        withCredentials: true
      });
      setApiResponse(response.data);
    } catch (error) {
      console.error("API error:", error);
      setApiResponse({error: error.message, details: error.response?.data});
    }
  };

  const debugStyles = {
    container: {
      position: 'fixed',
      bottom: '10px',
      right: '10px',
      backgroundColor: 'rgba(0,0,0,0.9)',
      color: 'white',
      padding: '15px',
      borderRadius: '10px',
      zIndex: 9999,
      maxWidth: '400px',
      maxHeight: '80vh',
      overflow: 'auto',
      fontSize: '12px',
      fontFamily: 'monospace'
    },
    button: {
      backgroundColor: '#e74c3c',
      border: 'none',
      color: 'white',
      padding: '5px 10px',
      margin: '5px',
      borderRadius: '4px',
      cursor: 'pointer'
    },
    pre: {
      whiteSpace: 'pre-wrap',
      wordBreak: 'break-all',
      backgroundColor: '#333',
      padding: '10px',
      borderRadius: '5px'
    }
  };

  return (
    <div style={debugStyles.container}>
      <h3>User Debugger</h3>
      
      <h4>Current Auth State:</h4>
      <p>Loading: {loading ? 'Yes' : 'No'}</p>
      <p>Authenticated: {isAuthenticated ? 'Yes' : 'No'}</p>
      
      <h4>User Object:</h4>
      <pre style={debugStyles.pre}>
        {user ? JSON.stringify(user, null, 2) : 'null'}
      </pre>
      
      <h4>Tools:</h4>
      <button style={debugStyles.button} onClick={refreshUserData}>
        Refresh User Data
      </button>
      <button style={debugStyles.button} onClick={testUserEndpoint}>
        Test /users/me Endpoint
      </button>
      
      {apiResponse && (
        <>
          <h4>API Response:</h4>
          <pre style={debugStyles.pre}>
            {JSON.stringify(apiResponse, null, 2)}
          </pre>
        </>
      )}
    </div>
  );
}

export default UserDebugger;