import React, { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import adminService from '../api/adminService';
import problemService from '../api/problems';
import './AdminStyles.css';

function TestCaseManagement() {
  const { user } = useAuth();
  const { problemId } = useParams();
  const [problem, setProblem] = useState(null);
  const [testCases, setTestCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingTestCase, setEditingTestCase] = useState(null);
  
  const [formData, setFormData] = useState({
    input: '',
    expectedOutput: '',
    sample: false,
    pvp: false
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
    if (problemId) {
      fetchProblemAndTestCases();
    }
  }, [problemId]);

  const fetchProblemAndTestCases = async () => {
    setLoading(true);
    try {
      // Get problem details
      const problemResponse = await problemService.getProblemById(problemId);
      setProblem(problemResponse.data);
      
      // Get test cases for this problem
      // Note: You'll need to add this endpoint to your problemService
      const testCasesResponse = await problemService.getProblemTestCases(problemId);
      setTestCases(testCasesResponse.data);
    } catch (error) {
      console.error('Error fetching problem details:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData({ 
      ...formData, 
      [name]: type === 'checkbox' ? checked : value 
    });
  };

  const handleCreateTestCase = () => {
    setEditingTestCase(null);
    setFormData({
      input: '',
      expectedOutput: '',
      sample: false,
      pvp: false
    });
    setShowForm(true);
  };

  const handleEditTestCase = (testCase) => {
    setEditingTestCase(testCase);
    setFormData({
      input: testCase.input,
      expectedOutput: testCase.expectedOutput,
      sample: testCase.sample,
      pvp: testCase.pvp
    });
    setShowForm(true);
  };

  const handleDeleteTestCase = async (testId) => {
    if (window.confirm('Are you sure you want to delete this test case?')) {
      try {
        await adminService.deleteTestCase(problemId, testId);
        setTestCases(testCases.filter(tc => tc.id !== testId));
      } catch (error) {
        console.error('Error deleting test case:', error);
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingTestCase) {
        await adminService.updateTestCase(problemId, editingTestCase.id, formData);
      } else {
        await adminService.addTestCase(problemId, formData);
      }
      setShowForm(false);
      fetchProblemAndTestCases();
    } catch (error) {
      console.error('Error saving test case:', error);
    }
  };

  if (loading && !problem) {
    return <div className="admin-container">Loading problem details...</div>;
  }

  return (
    <div className="admin-container">
      <div className="admin-header">
        <h1>Test Cases for Problem #{problemId}</h1>
        <div>
          <Link to="/admin/problems" className="admin-btn">Back to Problems</Link>
          <Link to="/admin" className="admin-btn">Back to Dashboard</Link>
        </div>
      </div>

      {problem && (
        <div className="problem-info">
          <h2>{problem.title}</h2>
          <p><strong>Difficulty:</strong> {problem.difficulty}</p>
          <p><strong>Topic:</strong> {problem.topic}</p>
        </div>
      )}

      <div className="admin-content">
        <button className="admin-btn" onClick={handleCreateTestCase}>
          Add New Test Case
        </button>

        {showForm && (
          <div className="admin-form-container">
            <h2>{editingTestCase ? 'Edit Test Case' : 'Create New Test Case'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Input:</label>
                <textarea
                  name="input"
                  value={formData.input}
                  onChange={handleInputChange}
                  rows="4"
                  required
                ></textarea>
              </div>
              
              <div className="form-group">
                <label>Expected Output:</label>
                <textarea
                  name="expectedOutput"
                  value={formData.expectedOutput}
                  onChange={handleInputChange}
                  rows="4"
                  required
                ></textarea>
              </div>
              
              <div className="form-group checkbox">
                <label>
                  <input
                    type="checkbox"
                    name="sample"
                    checked={formData.sample}
                    onChange={handleInputChange}
                  />
                  Is Sample Test Case
                </label>
              </div>

              <div className="form-group checkbox">
                <label>
                  <input
                    type="checkbox"
                    name="pvp"
                    checked={formData.pvp}
                    onChange={handleInputChange}
                  />
                  Use for PvP
                </label>
              </div>
              
              <div className="form-actions">
                <button type="submit" className="admin-btn">
                  {editingTestCase ? 'Update Test Case' : 'Create Test Case'}
                </button>
                <button 
                  type="button" 
                  className="admin-btn cancel"
                  onClick={() => setShowForm(false)}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Test cases table */}
        {testCases.length > 0 ? (
          <table className="admin-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Input</th>
                <th>Expected Output</th>
                <th>Sample</th>
                <th>PvP</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {testCases.map(testCase => (
                <tr key={testCase.id}>
                  <td>{testCase.id}</td>
                  <td>
                    <pre>{testCase.input.length > 50 
                      ? testCase.input.substring(0, 50) + '...' 
                      : testCase.input}
                    </pre>
                  </td>
                  <td>
                    <pre>{testCase.expectedOutput.length > 50 
                      ? testCase.expectedOutput.substring(0, 50) + '...' 
                      : testCase.expectedOutput}
                    </pre>
                  </td>
                  <td>{testCase.sample ? 'Yes' : 'No'}</td>
                  <td>{testCase.pvp ? 'Yes' : 'No'}</td>
                  <td>
                    <button 
                      className="admin-btn-small"
                      onClick={() => handleEditTestCase(testCase)}
                    >
                      Edit
                    </button>
                    <button 
                      className="admin-btn-small danger"
                      onClick={() => handleDeleteTestCase(testCase.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <p>No test cases available for this problem.</p>
        )}
      </div>
    </div>
  );
}

export default TestCaseManagement;