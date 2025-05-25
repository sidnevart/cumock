import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './AdminStyles.css';
import AdminDebugger from '../components/AdminDebugger'; // Import the AdminDebugger component

function AdminDashboard() {
  const { user } = useAuth();
  console.log("Admin dashboard - User:", user); // Debug

  // Check if user is admin with either format
  const isAdmin = user && (user.role === 'ADMIN' || user.role === 'ROLE_ADMIN');
  
  if (!isAdmin) {
    return (
      <div className="admin-container">
        <h1>Access Denied</h1>
        <p>You do not have permission to access the admin panel.</p>
        <pre>Current role: {user?.role || 'No role'}</pre>
        <Link to="/" className="btn btn-primary">Return to Home</Link>
      </div>
    );
  }

  return (
    <div className="admin-container">
       {/*<AdminDebugger />*/}
      <h1>Admin Dashboard</h1>
      <div className="admin-menu">
        <div className="admin-card">
          <h2>User Management</h2>
          <p>View and manage user accounts and roles</p>
          <Link to="/admin/users" className="admin-btn">Manage Users</Link>
        </div>

        <div className="admin-card">
          <h2>Problem Management</h2>
          <p>Create, edit, and delete programming problems</p>
          <Link to="/admin/problems" className="admin-btn">Manage Problems</Link>
        </div>

        <div className="admin-card">
          <h2>Test Case Management</h2>
          <p>Manage test cases for problems</p>
          <Link to="/admin/problems" className="admin-btn">Manage Test Cases</Link>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;