import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // Fix the import path
import adminService from '../api/adminService'; // Fix the import path
import './AdminStyles.css';


function ProblemManagement() {
  const { user } = useAuth();
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [searchTitle, setSearchTitle] = useState('');
  const [searchTopic, setSearchTopic] = useState('');
  const [searchDifficulty, setSearchDifficulty] = useState('');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [editingProblem, setEditingProblem] = useState(null);
  
  // Form state for creating/editing problems
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    difficulty: 'EASY',
    topic: ''
  });

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
    fetchProblems();
  }, [currentPage]);

  const fetchProblems = async () => {
    setLoading(true);
    try {
      const response = await adminService.getPagedProblems(currentPage, 10);
      setProblems(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error('Error fetching problems:', error);
    } finally {
      setLoading(false);
    }
  };

  const searchProblems = async () => {
    setLoading(true);
    try {
      const response = await adminService.getAllProblems(
        searchTopic || null,
        searchTitle || null,
        searchDifficulty || null
      );
      setProblems(response.data);
      setCurrentPage(0);
      setTotalPages(1); // Simple search doesn't use pagination
    } catch (error) {
      console.error('Error searching problems:', error);
    } finally {
      setLoading(false);
    }
  };

  const resetSearch = () => {
    setSearchTitle('');
    setSearchTopic('');
    setSearchDifficulty('');
    fetchProblems();
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleCreateProblem = () => {
    setEditingProblem(null);
    setFormData({
      title: '',
      description: '',
      difficulty: 'EASY',
      topic: ''
    });
    setShowCreateForm(true);
  };

  const handleEditProblem = (problem) => {
    setEditingProblem(problem);
    setFormData({
      title: problem.title,
      description: problem.description || '',
      difficulty: problem.difficulty,
      topic: problem.topic
    });
    setShowCreateForm(true);
  };

  const handleDeleteProblem = async (id) => {
    if (window.confirm('Are you sure you want to delete this problem?')) {
      try {
        await adminService.deleteProblem(id);
        setProblems(problems.filter(p => p.id !== id));
      } catch (error) {
        console.error('Error deleting problem:', error);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingProblem) {
        await adminService.updateProblem(editingProblem.id, formData);
      } else {
        await adminService.createProblem(formData);
      }
      setShowCreateForm(false);
      fetchProblems();
    } catch (error) {
      console.error('Error saving problem:', error);
    }
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
    }
  };

  if (loading && problems.length === 0) {
    return <div className="admin-container">Loading problems...</div>;
  }

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Problem Management</h1>
        <Link to="/admin" className="admin-btn">Back to Dashboard</Link>
      </div>

      <div className="admin-content">
        {/* Search and filters */}
        <div className="admin-search">
          <input
            type="text"
            placeholder="Search by title"
            value={searchTitle}
            onChange={(e) => setSearchTitle(e.target.value)}
          />
          <input
            type="text"
            placeholder="Filter by topic"
            value={searchTopic}
            onChange={(e) => setSearchTopic(e.target.value)}
          />
          <select
            value={searchDifficulty}
            onChange={(e) => setSearchDifficulty(e.target.value)}
          >
            <option value="">All Difficulties</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
          <button className="admin-btn-small" onClick={searchProblems}>Search</button>
          <button className="admin-btn-small" onClick={resetSearch}>Reset</button>
        </div>
        
        <button className="admin-btn" onClick={handleCreateProblem}>
          Create New Problem
        </button>

        {showCreateForm && (
          <div className="admin-form-container">
            <h2>{editingProblem ? 'Edit Problem' : 'Create New Problem'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Title:</label>
                <input
                  type="text"
                  name="title"
                  value={formData.title}
                  onChange={handleInputChange}
                  required
                />
              </div>
              
              <div className="form-group">
                <label>Description:</label>
                <textarea
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  required
                  rows="6"
                ></textarea>
              </div>
              
              <div className="form-group">
                <label>Difficulty:</label>
                <select
                  name="difficulty"
                  value={formData.difficulty}
                  onChange={handleInputChange}
                  required
                >
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>
              
              <div className="form-group">
                <label>Topic:</label>
                <input
                  type="text"
                  name="topic"
                  value={formData.topic}
                  onChange={handleInputChange}
                  required
                />
              </div>
              
              <div className="form-actions">
                <button type="submit" className="admin-btn">
                  {editingProblem ? 'Update Problem' : 'Create Problem'}
                </button>
                <button 
                  type="button" 
                  className="admin-btn cancel"
                  onClick={() => setShowCreateForm(false)}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Problems table */}
        <table className="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Title</th>
              <th>Topic</th>
              <th>Difficulty</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {problems.map(problem => (
              <tr key={problem.id}>
                <td>{problem.id}</td>
                <td>{problem.title}</td>
                <td>{problem.topic}</td>
                <td>{problem.difficulty}</td>
                <td>
                  <button 
                    className="admin-btn-small"
                    onClick={() => handleEditProblem(problem)}
                  >
                    Edit
                  </button>
                  <button 
                    className="admin-btn-small danger"
                    onClick={() => handleDeleteProblem(problem.id)}
                  >
                    Delete
                  </button>
                  <Link 
                    to={`/admin/test-cases/${problem.id}`}
                    className="admin-btn-small"
                  >
                    Test Cases
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="pagination">
          <button 
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 0}
          >
            Previous
          </button>
          <span>Page {currentPage + 1} of {totalPages}</span>
          <button 
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages - 1}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProblemManagement;