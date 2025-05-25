import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import adminService from '../api/adminService';
import './AdminStyles.css';

function UserManagement() {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [editingUser, setEditingUser] = useState(null);
  const [selectedRole, setSelectedRole] = useState('');

  // Check if user is admin
  if (!user || user.role !== 'ROLE_ADMIN') {
    return (
      <div className="admin-container">
        <h1>Access Denied</h1>
        <p>You do not have permission to access this page.</p>
        <Link to="/" className="btn btn-primary">Return to Home</Link>
      </div>
    );
  }

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await adminService.getAllUsers();
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching users:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleEditRole = (userToEdit) => {
    setEditingUser(userToEdit);
    setSelectedRole(userToEdit.role);
  };

  const handleRoleChange = (e) => {
    setSelectedRole(e.target.value);
  };

  const handleSaveRole = async () => {
    if (!editingUser || !selectedRole) return;
    
    try {
      await adminService.updateUserRole(editingUser.id, selectedRole);
      // Update user in the local state
      setUsers(users.map(u => 
        u.id === editingUser.id ? { ...u, role: selectedRole } : u
      ));
      setEditingUser(null);
    } catch (error) {
      console.error('Error updating user role:', error);
    }
  };

  const handleCancel = () => {
    setEditingUser(null);
  };

  if (loading) {
    return <div className="admin-container">Loading users...</div>;
  }

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>User Management</h1>
        <Link to="/admin" className="admin-btn">Back to Dashboard</Link>
      </div>

      <div className="admin-content">
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Username</th>
              <th>Email</th>
              <th>Role</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.username}</td>
                <td>{user.email}</td>
                <td>
                  {editingUser && editingUser.id === user.id ? (
                    <select value={selectedRole} onChange={handleRoleChange}>
                      <option value="USER">USER</option>
                      <option value="ADMIN">ADMIN</option>
                    </select>
                  ) : (
                    user.role
                  )}
                </td>
                <td>
                  {editingUser && editingUser.id === user.id ? (
                    <>
                      <button className="admin-btn-small" onClick={handleSaveRole}>Save</button>
                      <button className="admin-btn-small cancel" onClick={handleCancel}>Cancel</button>
                    </>
                  ) : (
                    <button className="admin-btn-small" onClick={() => handleEditRole(user)}>Change Role</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default UserManagement;