import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import problemService from '../api/problems';
import codeService from '../api/code';
import './ProblemDetailsPage.css';
import { useAuth } from '../context/AuthContext';

function ProblemDetailsPage() {
  const { id } = useParams();
  const { isAuthenticated, user } = useAuth();
  const [problem, setProblem] = useState(null);
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('javascript');
  const [output, setOutput] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProblem = async () => {
      try {
        console.log("Fetching problem details for ID:", id);
        console.log("Authentication status:", isAuthenticated);
        console.log("Current user:", user);
        
        if (!isAuthenticated) {
          setError('Please log in to view problem details');
          setLoading(false);
          return;
        }
        
        // Add a small delay to ensure authentication is ready
        setTimeout(async () => {
          try {
            const response = await problemService.getProblemById(id);
            console.log("Problem details response:", response.data);
            setProblem(response.data);
            setCode(getInitialCode(language));
          } catch (err) {
            handleFetchError(err);
          } finally {
            setLoading(false);
          }
        }, 500);
      } catch (err) {
        handleFetchError(err);
        setLoading(false);
      }
    };
    
    const handleFetchError = (err) => {
      console.error("Error fetching problem details:", err);
      
      if (err.response) {
        console.error("Response data:", err.response?.data);
        console.error("Status code:", err.response?.status);
        console.error("Request URL:", err.config?.url);
        
        if (err.response?.status === 403) {
          if (err.config?.url.includes('/details')) {
            setError('Access denied. The endpoint may have changed to /api/problems/{id}/details.');
          } else {
            setError('Access denied. You do not have permission to view this problem.');
          }
        } else if (err.response?.status === 401) {
          setError('Authentication required. Please log in again.');
        } else if (err.response?.status === 404) {
          setError('Problem not found. It may have been deleted or the ID is incorrect.');
        } else {
          setError('Failed to load problem: ' + (err.response?.data?.message || err.message));
        }
      } else if (err.request) {
        setError('No response from server. Please check your network connection.');
      } else {
        setError('Request setup error: ' + err.message);
      }
    };

    fetchProblem();
  }, [id, isAuthenticated, user, language]);

  const getInitialCode = (lang) => {
    switch (lang) {
      case 'javascript':
        return '// Write your JavaScript code here\n';
      case 'python':
        return '# Write your Python code here\n';
      case 'cpp':
        return '#include <iostream>\n\nint main() {\n    // Write your C++ code here\n    return 0;\n}\n';
      case 'java':
        return 'public class Solution {\n    public static void main(String[] args) {\n        // Write your Java code here\n    }\n}\n';
      default:
        return '';
    }
  };

  const handleLanguageChange = (e) => {
    const newLanguage = e.target.value;
    setLanguage(newLanguage);
    setCode(getInitialCode(newLanguage));
  };

  const handleRunCode = async () => {
  try {
    setOutput('');
    setError('');
    const response = await codeService.runCode(
      user.id,
      id,
      language,
      code
    );
    
    if (response.data && response.data.results) {
      const results = response.data.results;
      let outputText = '';
      
      results.forEach((result, index) => {
        outputText += `--- Test Case ${index + 1} ---\n`;
        outputText += `Input:\n${result.input}\n\n`;
        outputText += `Your Output:\n${result.output}\n\n`;
        outputText += `Expected Output:\n${result.expected}\n\n`;
        outputText += `Status: ${result.passed ? 'PASSED ✅' : 'FAILED ❌'}\n`;
        outputText += `Execution Time: ${result.executionTimeMillis}ms\n\n`;
      });
      
      const passedCount = results.filter(r => r.passed).length;
      outputText += `Summary: ${passedCount}/${results.length} test cases passed\n`;
      
      setOutput(outputText);
    } else {
      setOutput('No test results returned');
    }
  } catch (err) {
    setError(err.response?.data?.message || 'Failed to run code');
  }
};

const handleSubmitCode = async () => {
  try {
    setOutput('');
    setError('');
    const response = await codeService.submitCode(
      user.id,
      id,
      language,
      code
    );
    
    let outputText = '';
    
    if (response.data) {
      const result = response.data;
      outputText += `Submission Results:\n\n`;
      outputText += `Tests Passed: ${result.passed}/${result.total}\n`;
      outputText += `Tests Failed: ${result.failed}/${result.total}\n`;
      outputText += `Verdict: ${result.verdict}\n`;
      outputText += `Execution Time: ${result.executionTimeMillis}ms\n`;
      
      if (result.verdict === "OK") {
        outputText += `\n🎉 Congratulations! Your solution passed all test cases.`;
      } else {
        outputText += `\n⚠️ Your solution didn't pass all test cases. Please try again.`;
      }
    }
    
    setOutput(outputText);
  } catch (err) {
    setError(err.response?.data?.message || 'Failed to submit code');
  }
};



  const getMonacoLanguage = (lang) => {
    switch (lang) {
      case 'javascript':
        return 'javascript';
      case 'python':
        return 'python';
      case 'cpp':
        return 'cpp';
      case 'java':
        return 'java';
      default:
        return 'javascript';
    }
  };

  if (loading) {
    return <div className="problem-details loading">Loading problem details...</div>;
  }

  if (error) {
    return (
      <div className="problem-details">
        <div className="error">
          <h2>Error</h2>
          <p>{error}</p>
          {(!isAuthenticated && error.includes('log in')) && (
            <Link to="/login" className="login-button">Log In</Link>
          )}
          <Link to="/problems" className="back-button">Back to Problems</Link>
        </div>
      </div>
    );
  }

  if (!problem) {
    return <div className="error">Problem not found</div>;
  }

  return (
    <div className="problem-details">
      <div className="problem-header">
        <h1>{problem.title}</h1>
        <div className="problem-meta">
          <span className="difficulty">Difficulty: {problem.difficulty}</span>
          <span className="category">Category: {problem.topic}</span>
        </div>
      </div>

      <div className="problem-content">
        <div className="problem-description">
          <h2>Description</h2>
          <p>{problem.description}</p>
          
          <h3>Input Format</h3>
          <p>{problem.inputFormat}</p>
          
          <h3>Output Format</h3>
          <p>{problem.outputFormat}</p>
          
          <h3>Examples</h3>
          {problem.examples?.map((example, index) => (
            <div key={index} className="example">
              <h4>Example {index + 1}</h4>
              <div className="example-input">
                <strong>Input:</strong>
                <pre>{example.input}</pre>
              </div>
              <div className="example-output">
                <strong>Output:</strong>
                <pre>{example.output}</pre>
              </div>
            </div>
          ))}
        </div>

        <div className="code-editor-section">
          <div className="editor-header">
            <select value={language} onChange={handleLanguageChange}>
              <option value="javascript">JavaScript</option>
              <option value="python">Python</option>
              <option value="cpp">C++</option>
              <option value="java">Java</option>
            </select>
            <div className="editor-buttons">
              <button onClick={handleRunCode}>Run Code</button>
              <button onClick={handleSubmitCode}>Submit</button>
            </div>
          </div>

          <div className="code-editor">
            <Editor
              height="300px"
              defaultLanguage={getMonacoLanguage(language)}
              language={getMonacoLanguage(language)}
              value={code}
              onChange={setCode}
              theme="vs-light"
              options={{
                minimap: { enabled: false },
                fontSize: 14,
                lineNumbers: 'on',
                roundedSelection: false,
                scrollBeyondLastLine: false,
                automaticLayout: true,
              }}
            />
          </div>

          <div className="output-section">
            <h3>Output</h3>
            {error && <div className="error-message">{error}</div>}
            {output && <pre className="output-content">{output}</pre>}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProblemDetailsPage; 