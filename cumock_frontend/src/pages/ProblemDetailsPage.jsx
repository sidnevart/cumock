import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import Editor from '@monaco-editor/react';
import problemService from '../api/problems';
import codeService from '../api/code';
import './ProblemDetailsPage.css';

function ProblemDetailsPage() {
  const { id } = useParams();
  const [problem, setProblem] = useState(null);
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('javascript');
  const [output, setOutput] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProblem = async () => {
      try {
        const response = await problemService.getProblemById(id);
        setProblem(response.data);
        setCode(getInitialCode(language));
      } catch (err) {
        setError('Failed to load problem');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchProblem();
  }, [id]);

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
      const response = await codeService.runCode({
        code,
        language,
        problemId: id
      });
      setOutput(response.data.output || 'No output');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to run code');
    }
  };

  const handleSubmitCode = async () => {
    try {
      setOutput('');
      setError('');
      const response = await codeService.submitCode({
        code,
        language,
        problemId: id
      });
      setOutput(response.data.message || 'Submission successful');
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
    return <div className="loading">Loading...</div>;
  }

  if (error) {
    return <div className="error">{error}</div>;
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
          <span className="category">Category: {problem.category}</span>
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