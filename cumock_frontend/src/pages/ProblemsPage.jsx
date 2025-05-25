import React, { useState, useEffect } from 'react';
import problemService from '../api/problems';
import { Link } from 'react-router-dom';
import './ProblemsPage.css';

function ProblemsPage() {
  const [problems, setProblems] = useState([]);
  const [pagination, setPagination] = useState({});
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    page: 0,
    size: 10,
    sortBy: 'id',
    topic: '',
    title: '',
    difficulty: '',
  });
  const [error, setError] = useState('');

  useEffect(() => {
    fetchProblems();
  }, [filters]);

  const fetchProblems = async () => {
    setLoading(true);
    try {
      const response = await problemService.getPagedProblems(filters.page, filters.size, filters.sortBy);
      setProblems(response.data.content);
      setPagination({
        number: response.data.number,
        size: response.data.size,
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        last: response.data.last,
      });
    } catch (err) {
      setError('Failed to load problems');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    setFilters({
      ...filters,
      [e.target.name]: e.target.value,
      page: 0,
    });
  };

  const handlePageChange = (newPage) => {
    setFilters({
      ...filters,
      page: newPage,
    });
  };

  if (loading) {
    return <div className="loading">Loading...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
  }

  return (
    <div className="problems-container">
      <div className="problems-header">
        <h1>Список задач</h1>
      </div>

      <div>
        <input
          type="text"
          name="title"
          placeholder="Название задачи"
          value={filters.title}
          onChange={handleFilterChange}
        />
        <input
          type="text"
          name="topic"
          placeholder="Тема"
          value={filters.topic}
          onChange={handleFilterChange}
        />
        <input
          type="text"
          name="difficulty"
          placeholder="Сложность"
          value={filters.difficulty}
          onChange={handleFilterChange}
        />
      </div>

      <div className="problems-list">
        {problems.map((problem) => (
          <div key={problem.id} className="problem-item">
            <Link to={`/problems/${problem.id}`}>
              <h2>{problem.title}</h2>
              <div className="problem-meta">
                <span>Difficulty: {problem.difficulty}</span>
                <span>Topic: {problem.topic}</span>
              </div>
            </Link>
          </div>
        ))}
      </div>

      <div className="pagination-controls">
        <button onClick={() => handlePageChange(pagination.number - 1)} disabled={pagination.number === 0}>
          Предыдущая
        </button>
        <span>
          Страница {pagination.number + 1} из {pagination.totalPages}
        </span>
        <button onClick={() => handlePageChange(pagination.number + 1)} disabled={pagination.last}>
          Следующая
        </button>
      </div>
    </div>
  );
}

export default ProblemsPage; 