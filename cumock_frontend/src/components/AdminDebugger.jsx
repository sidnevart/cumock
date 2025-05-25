import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

function AdminDebugger() {
  const { user, isAuthenticated } = useAuth();
  const [testResult, setTestResult] = useState(null);
  
  const testAdminAccess = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/admin/users', {
        headers: { 'Authorization': `Bearer ${localStorage.getItem('user_token')}` }
      });
      setTestResult({
        success: true,
        data: response.data
      });
    } catch (error) {
      setTestResult({
        success: false,
        error: error.response?.data || error.message,
        status: error.response?.status
      });
    }
  };
  
  const styles = {
    debugger: {
      position: 'fixed',
      bottom: '10px',
      right: '10px',
      padding: '15px',
      backgroundColor: 'rgba(0,0,0,0.9)',
      color: '#fff',
      zIndex: 9999,
      maxWidth: '400px',
      maxHeight: '500px',
      overflow: 'auto',
      borderRadius: '5px',
      fontSize: '12px'
    },
    section: {
      marginBottom: '10px',
      padding: '10px',
      backgroundColor: 'rgba(50,50,50,0.5)',
      borderRadius: '4px'
    },
    button: {
      backgroundColor: '#e74c3c',
      color: 'white',
      border: 'none',
      padding: '6px 12px',
      borderRadius: '4px',
      cursor: 'pointer',
      marginTop: '10px'
    }
  };
  
  return (
    <div style={styles.debugger}>
      <h3>Admin Auth Debugger</h3>
      
      <div style={styles.section}>
        <h4>Authentication State:</h4>
        <p>Authenticated: {isAuthenticated ? 'Yes' : 'No'}</p>
        <p>User ID: {user?.id || 'N/A'}</p>
        <p>Username: {user?.username || 'N/A'}</p>
        <p>Email: {user?.email || 'N/A'}</p>
        <p>Role: {user?.role || 'N/A'}</p>
      </div>
      
      <div style={styles.section}>
        <h4>Test Admin Access:</h4>
        <button style={styles.button} onClick={testAdminAccess}>
          Test /admin/users Endpoint
        </button>
        
        {testResult && (
          <div style={{marginTop: '10px'}}>
            <p>Success: {testResult.success ? 'Yes' : 'No'}</p>
            {testResult.success ? (
              <pre style={{maxHeight: '150px', overflow: 'auto'}}>
                {JSON.stringify(testResult.data, null, 2)}
              </pre>
            ) : (
              <div>
                <p>Error: {testResult.error}</p>
                <p>Status: {testResult.status}</p>
              </div>
            )}
          </div>
        )}
      </div>
      
      <div style={styles.section}>
        <h4>Auth Token:</h4>
        <pre style={{maxHeight: '100px', overflow: 'auto', wordBreak: 'break-all'}}>
          {localStorage.getItem('user_token') || 'No token'}
        </pre>
      </div>
    </div>
  );
}

export default AdminDebugger;