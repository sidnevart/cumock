import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import problemService from '../api/problems';
import codeService from '../api/code';
import pvpService from '../api/pvp';
import websocketService from '../api/websocket';
import Editor from '@monaco-editor/react';
import './PvPContestPage.css';
// import SockJS from 'sockjs-client'; // Потребуется для WebSockets
// import Stomp from 'stompjs'; // Потребуется для WebSockets

function PvPContestPage() {
  const { contestId } = useParams();
  const { user } = useAuth();
  const userId = user?.id;

  const [contest, setContest] = useState(null);
  const [problem1, setProblem1] = useState(null); // Задача Challenger'а
  const [problem2, setProblem2] = useState(null); // Задача Challenged'а
  const [userProblemId, setUserProblemId] = useState(null); // ID задачи текущего пользователя в матче
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('javascript');
  const [loading, setLoading] = useState(true);
  const [runResults, setRunResults] = useState(null);
  const [submissionResult, setSubmissionResult] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [pvpProgress, setPvpProgress] = useState({}); // Прогресс матча (из WebSockets)
  const [wsConnected, setWsConnected] = useState(false);
  const [output, setOutput] = useState('');
  const [error, setError] = useState('');

  const defaultTemplates = {
    javascript: '',
    python: '',
    cpp: `#include <iostream>
#include <vector>
#include <string>

using namespace std;

int main() {
    // Your code here
    
    return 0;
}`,
    java: `import java.util.*;

public class Solution {
    public static void main(String[] args) {
        // Your code here
        
    }
}`
  };

  const handleLanguageChange = (newLanguage) => {
    setLanguage(newLanguage);
    if (!code.trim()) {  // Only set default template if the editor is empty
      setCode(defaultTemplates[newLanguage]);
    }
  };

  useEffect(() => {
    if (!userId || !contestId) return;

    const setupWebSocket = async () => {
      try {
        await websocketService.connect();
        setWsConnected(true);

        // Subscribe to contest progress updates
        websocketService.subscribe(`/topic/pvp-progress/${contestId}`, (data) => {
          setPvpProgress(data);
        });
      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
      }
    };

    setupWebSocket();
    fetchContestData();

    // Cleanup function
    return () => {
      websocketService.unsubscribe(`/topic/pvp-progress/${contestId}`);
      websocketService.disconnect();
    };
  }, [contestId, userId]);

  const fetchContestData = async () => {
    setLoading(true);
    try {
      const contestResponse = await pvpService.getContestDetails(contestId); // Нужен новый эндпоинт на бекенде для получения деталей контеста по ID
      setContest(contestResponse.data);

      // Определяем, какая задача принадлежит текущему пользователю
      const p1 = await problemService.getProblemById(contestResponse.data.problem1Id);
      const p2 = await problemService.getProblemById(contestResponse.data.problem2Id);

      setProblem1(p1.data);
      setProblem2(p2.data);

      if (contestResponse.data.user1Id === userId) {
          setUserProblemId(contestResponse.data.problem1Id);
      } else if (contestResponse.data.user2Id === userId) {
          setUserProblemId(contestResponse.data.problem2Id);
      }

      if (contestResponse.data.problem) {
        setCode(getInitialCode(language, contestResponse.data.problem.initialCode));
      } else {
        setCode(getInitialCode(language, ''));
      }

    } catch (error) {
      console.error('Error fetching contest data:', error);
      setContest(null);
    } finally {
      setLoading(false);
    }
  };

  const getInitialCode = (lang, initialCode) => {
    if (initialCode) return initialCode;

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

  const handleRunCode = async () => {
    if (!userId || !userProblemId || !contestId) { // Проверяем необходимые данные
        alert("Недостаточно данных для запуска кода в матче.");
        return;
    }
    setLoading(true); // Возможно, лучше использовать отдельное состояние для загрузки запуска
    setRunResults(null);
    setSubmissionResult(null);
    try {
      setOutput('');
      setError('');
      const response = await codeService.runCode(userId, userProblemId, language, code, contestId); // Передаем contestId
      setRunResults(response.data);
      setOutput(response.data.output || 'No output');
    } catch (error) {
      console.error('Error running code:', error);
      setError(error.response?.data?.message || 'Failed to run code');
    } finally {
      setLoading(false); // Сброс состояния загрузки
    }
  };

  const handleSubmitCode = async () => {
     if (!userId || !userProblemId || !contestId) { // Проверяем необходимые данные
        alert("Недостаточно данных для отправки решения в матче.");
        return;
    }
    setIsSubmitting(true);
    setRunResults(null);
    setSubmissionResult(null);
    try {
      setOutput('');
      setError('');
      const response = await codeService.submitCode(userId, userProblemId, language, code, contestId); // Передаем contestId
      setSubmissionResult(response.data);
      setOutput(response.data.message || 'Submission successful');
      // При успешной отправке, возможно, обновить состояние матча или ждать обновления через WebSockets
    } catch (error) {
      console.error('Error submitting code:', error);
      setError(error.response?.data?.message || 'Failed to submit code');
      // Обработка ошибок отправки
    } finally {
      setIsSubmitting(false);
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

  if (!contest) {
    return <div>Матч не найден.</div>;
  }

  // Определяем, кто есть кто в матче
  const challenger = contest.user1Id === userId ? 'Вы' : 'Противник';
  const challenged = contest.user2Id === userId ? 'Вы' : 'Противник';
  const yourProblem = contest.user1Id === userId ? problem1 : problem2;
  const opponentProblem = contest.user2Id === userId ? problem1 : problem2;

  return (
    <div className="pvp-contest">
      <div className="contest-header">
        <h1>PvP Матч #{contest.id}</h1>
        <div className="contest-meta">
          <span className="status">Статус: {contest.status}</span>
          {!wsConnected && <p className="text-warning">Подключение к серверу обновлений...</p>}
        </div>
      </div>

      <div className="contest-content">
        <div className="problem-section">
          <h2>Ваша задача ({challenger === 'Вы' ? problem1?.title : problem2?.title})</h2>
          {yourProblem && (
              <div>
                   <p>Сложность: {yourProblem.difficulty}</p>
                   <p>Тема: {yourProblem.topic}</p>
                   <div>
                        <h3>Описание</h3>
                        <p>{yourProblem.description}</p>
                   </div>
              </div>
          )}

          <h2>Задача противника ({challenged === 'Вы' ? problem1?.title : problem2?.title})</h2>
           {opponentProblem && (
              <div>
                   <p>Сложность: {opponentProblem.difficulty}</p>
                   <p>Тема: {opponentProblem.topic}</p>
              </div>
          )}
        </div>

        {pvpProgress && pvpProgress.contestId === parseInt(contestId) && (
          <div className="progress-section">
            <h3>Прогресс матча:</h3>
            <div className="progress-container">
              <div className="player-progress">
                <h4>{challenger}</h4>
                <p>Пройдено тестов: {pvpProgress.user1Progress?.passed || 0}/{pvpProgress.user1Progress?.total || 0}</p>
                <p>Попыток: {pvpProgress.user1Progress?.attempts || 0}</p>
                <p>Статус: {pvpProgress.user1Progress?.solved ? 'Решено' : 'В процессе'}</p>
              </div>
              <div className="player-progress">
                <h4>{challenged}</h4>
                <p>Пройдено тестов: {pvpProgress.user2Progress?.passed || 0}/{pvpProgress.user2Progress?.total || 0}</p>
                <p>Попыток: {pvpProgress.user2Progress?.attempts || 0}</p>
                <p>Статус: {pvpProgress.user2Progress?.solved ? 'Решено' : 'В процессе'}</p>
              </div>
            </div>
          </div>
        )}

        <div className="code-editor-section">
          <div className="editor-header">
            <select value={language} onChange={(e) => handleLanguageChange(e)}>
              <option value="javascript">JavaScript</option>
              <option value="python">Python</option>
              <option value="cpp">C++</option>
              <option value="java">Java</option>
            </select>
            <div className="editor-buttons">
              <button onClick={handleRunCode} disabled={loading}>Run Code</button>
              <button onClick={handleSubmitCode} disabled={isSubmitting}>Submit</button>
            </div>
          </div>

          <div className="code-editor">
            <Editor
              height="300px"
              defaultLanguage={getMonacoLanguage(language)}
              language={getMonacoLanguage(language)}
              value={code}
              onChange={setCode}
              theme="vs-dark"
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

        {runResults && runResults.results && runResults.results.length > 0 && (
          <div className="results-section">
            <h3>Результаты выполнения (Sample Tests):</h3>
            {runResults.results.map((result, index) => (
              <div key={index} className={`test-result ${result.passed ? 'passed' : 'failed'}`}>
                <p>Input: {result.input}</p>
                <p>Your Output: {result.yourOutput}</p>
                <p>Expected Output: {result.expectedOutput}</p>
                <p>Passed: {result.passed ? 'Yes' : 'No'}</p>
              </div>
            ))}
          </div>
        )}

        {submissionResult && (
          <div className="results-section">
            <h3>Результаты отправки:</h3>
            <p>Вердикт: {submissionResult.verdict}</p>
            <p>Пройдено тестов: {submissionResult.passed}/{submissionResult.total}</p>
            <p>Время выполнения: {submissionResult.totalTime}ms</p>
          </div>
        )}

        {contest.status !== 'ONGOING' && (
            <div>
                <p>Матч завершен или отменен.</p>
                {contest.status === 'FINISHED' && contest.winnerId && (
                    <p>Победитель: {contest.winnerId === userId ? 'Вы' : 'Противник'}</p>
                )}
            </div>
        )}
      </div>
    </div>
  );
}

export default PvPContestPage; 