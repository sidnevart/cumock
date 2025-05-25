import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './HomePage.css';

function HomePage() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="homepage-container">
      <section className="hero-section">
        <h1>Train your algorithms</h1>
        <p>Compete, practice, and improve your coding skills.</p>
        {!isAuthenticated && (
          <Link to="/register" className="get-started-button">Get Started</Link>
        )}
      </section>

      <section className="features-section">
        <h2>Platform Features</h2>
        <div className="features-grid">
          <div className="feature-card">
            {/* Add icon here */}
            <h2>PvP Code Battles</h2>
            <p>Compete against other users in real-time coding challenges.</p>
          </div>
          <div className="feature-card">
            {/* Add icon here */}
            <h2>Smart Task Selection</h2>
            <p>Solve weaknesses to improve your problem-solving skills.</p>
          </div>
          <div className="feature-card">
            {/* Add icon here */}
            <h2>Ratings & Achievements</h2>
            <p>Keep track of your ranking and accomplishments.</p>
          </div>
          <div className="feature-card">
            {/* Add icon here */}
            <h2>Scheduler</h2>
            <p>Plan and manage your coding practice sessions.</p>
          </div>
        </div>
      </section>

      <section className="how-it-works-section">
        <h2>How it Works</h2>
        <div className="how-it-works-steps">
          <div className="step-card">
            <h3>Step 1</h3>
            <p>Select a task from the problem list.</p>
          </div>
          <div className="step-card">
            <h3>Step 2</h3>
            <p>Write and test your code in the editor.</p>
          </div>
          <div className="step-card">
            <h3>Step 3</h3>
            <p>Submit your solution and check the results.</p>
          </div>
          <div className="step-card">
            <h3>Step 4</h3>
            <p>Challenge other users in PvP battles.</p>
          </div>
        </div>
      </section>
    </div>
  );
}

export default HomePage; 