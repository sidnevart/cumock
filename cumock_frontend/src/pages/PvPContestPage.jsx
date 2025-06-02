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
  const [now, setNow] = useState(new Date());
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

  const PlayerProgress = ({ isYou, playerData, problemData }) => {
    const progressPercent = playerData?.total ? (playerData.passed / playerData.total) * 100 : 0;
    
    return (
      <div className={`player-progress ${isYou ? 'you' : 'opponent'}`}>
        <h4>{isYou ? 'Вы' : 'Противник'}</h4>
        <div className="problem-info">
          <span className="problem-title">{problemData?.title || 'Загрузка...'}</span>
          <span className="problem-difficulty">{problemData?.difficulty || ''}</span>
        </div>
        
        <div className="progress-stats">
          <div className="progress-bar-container">
            <div 
              className="progress-bar" 
              style={{ width: `${progressPercent}%` }}
              title={`${playerData?.passed || 0}/${playerData?.total || 0} тестов`}
            ></div>
          </div>
          <span className="progress-text">
            Пройдено тестов: {playerData?.passed || 0}/{playerData?.total || 0}
          </span>
        </div>
        
        <div className="player-stats">
          <div className="stat-item">
            <span className="stat-label">Попыток:</span>
            <span className="stat-value">{playerData?.attempts || 0}</span>
          </div>
          <div className="stat-item">
            <span className="stat-label">Статус:</span>
            <span className={`stat-value ${playerData?.solved ? 'solved' : ''}`}>
              {playerData?.solved ? '✅ Решено' : '⏳ В процессе'}
            </span>
          </div>
          {playerData?.lastSubmissionTime && (
            <div className="stat-item">
              <span className="stat-label">Последняя отправка:</span>
              <span className="stat-value">
                {new Date(playerData.lastSubmissionTime).toLocaleTimeString()}
              </span>
            </div>
          )}
        </div>
      </div>
    );
  };

  const handleLanguageChange = (e) => {
    const newLang = e.target.value;
    setLanguage(newLang);
    if (!code.trim()) {
      setCode(getInitialCode(newLang, ''));
    }
  };

    function formatTimeLeft(contest) {
      if (!contest || !contest.startTime) return "Time unknown";
      
      const startTime = new Date(contest.startTime);
      const endTime = new Date(startTime.getTime() + 30 * 60 * 1000);
      
      const diffMs = endTime - now;
      if (diffMs <= 0) return "Time expired";

      const minutes = Math.floor(diffMs / 60000);
      const seconds = Math.floor((diffMs % 60000) / 1000);
      const isUrgent = minutes < 5;
      
      return (
        <span className={`time-left ${isUrgent ? 'urgent' : ''}`}>
          {minutes}м {seconds}с
        </span>
      );
    }


  useEffect(() => {
    if (!userId || !contestId) return;

    const setupWebSocket = async () => {
      try {
        await websocketService.connect();
        setWsConnected(true);

        // Subscribe to contest progress updates
        websocketService.subscribe(`/topic/pvp-progress/${contestId}`, (data) => {
          console.log("[PvPContestPage] WebSocket message:", data);
          setPvpProgress(data);
        });

      } catch (error) {
        console.error('Failed to connect to WebSocket:', error);
      }
    };

    setupWebSocket();
    fetchContestData();
    const interval = setInterval(() => {
      setNow(new Date());
    }, 1000);
    // Cleanup function
    return () => {
      websocketService.unsubscribe(`/topic/pvp-progress/${contestId}`);
      websocketService.disconnect();
      clearInterval(interval);
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

  // Replace the banner code with a cleaner approach
  const handleRunCode = async () => {
    try {
      setOutput('');
      setError('');
      setRunResults(null);
      setLoading(true);
      console.log('CODE: ', code);
      const response = await codeService.runCode(
        userId,
        userProblemId,
        language,
        code,
        contestId
      );
      
      // Process results and show them in the output area
      if (response.data && response.data.results) {
        const results = response.data.results;
        let outputText = '';
        
        results.forEach((result, index) => {
          outputText += `--- Test Case ${index + 1} ---\n`;
          outputText += `Input:\n${result.input || ''}\n\n`;
          outputText += `Your Output:\n${result.output || ''}\n\n`;
          outputText += `Expected Output:\n${result.expected || ''}\n\n`;
          outputText += `Status: ${result.passed ? 'PASSED ✅' : 'FAILED ❌'}\n`;
          outputText += `Execution Time: ${result.executionTimeMillis || 0}ms\n\n`;
        });
        
        const passedCount = results.filter(r => r.passed).length;
        outputText += `Summary: ${passedCount}/${results.length} test cases passed\n`;
        
        setOutput(outputText);
        setRunResults(response.data);
      } else {
        setOutput(response.data?.output || 'No output or test results returned');
      }
    } catch (err) {
      console.error('Error running code:', err);
      setError(err.response?.data?.message || 'Failed to run code');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmitCode = async () => {
    try {
      setOutput('');
      setError('');
      setSubmissionResult(null);
      setIsSubmitting(true);
      
      const response = await codeService.submitCode(
        userId,
        userProblemId,
        language,
        code,
        contestId  // Передаём ID соревнования
      );
      
      let outputText = '';
      
      if (response.data) {
        const result = response.data;
        setSubmissionResult(result);
        
        outputText += `Submission Results:\n\n`;
        outputText += `Tests Passed: ${result.passed}/${result.total}\n`;
        outputText += `Tests Failed: ${result.failed}/${result.total}\n`;
        outputText += `Verdict: ${result.verdict}\n`;
        outputText += `Execution Time: ${result.executionTimeMillis || 0}ms\n`;
        
        if (result.verdict === "OK") {
          outputText += `\n🎉 Поздравляем! Ваше решение прошло все тесты.`;
        } else {
          outputText += `\n⚠️ Ваше решение не прошло все тесты. Пожалуйста, попробуйте снова.`;
        }
      }
      
      setOutput(outputText);
    } catch (err) {
      console.error('Error submitting code:', err);
      setError(err.response?.data?.message || 'Failed to submit code');
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
        {contest.status !== 'ONGOING' && (
          <div className="contest-ended-banner">
            <div className="banner-content">
              <h2>Соревнование завершено</h2>
              <p className="winner-info">
                {contest.winnerId ? 
                  (contest.winnerId === userId ? 
                    "Поздравляем! Вы победили в этом соревновании." : 
                    "К сожалению, вы проиграли в этом соревновании.") : 
                  "Соревнование завершилось без определения победителя."
                }
              </p>
              <p className="contest-status">Статус: {contest.status}</p>
            </div>
          </div>
        )}
      <div className="contest-header">
        <h1>PvP Матч #{contest.id}</h1>
        <div className="contest-meta">
          <span className="status">Статус: {contest.status}</span>
          {!wsConnected && <p className="text-warning">Подключение к серверу обновлений...</p>}
        </div>
        <span className="time-left">Осталось: {formatTimeLeft(contest)}</span>
      </div>

      <div className="contest-content">
        {pvpProgress && (
          <div className="progress-section">
            <h3>Прогресс соревнования</h3>
            <div className="progress-container">
              <PlayerProgress 
                isYou={contest.user1Id === userId}
                playerData={pvpProgress.user1Progress}
                problemData={contest.user1Id === userId ? yourProblem : opponentProblem}
              />
              <PlayerProgress 
                isYou={contest.user2Id === userId}
                playerData={pvpProgress.user2Progress}
                problemData={contest.user2Id === userId ? yourProblem : opponentProblem}
              />
            </div>
          </div>
        )}
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
              <button onClick={handleRunCode} disabled={loading || contest.status !== 'ONGOING'}>
                Run Code
              </button>
              <button onClick={handleSubmitCode} disabled={isSubmitting || contest.status !== 'ONGOING'}>
                Submit
              </button>
            </div>
          </div>

          <div className="code-editor">
            <Editor
              height="300px"
              defaultLanguage={getMonacoLanguage(language)}
              language={getMonacoLanguage(language)}
              value={code}
              onChange={contest.status === 'ONGOING' ? setCode : () => {}}
              theme="vs-dark"
              options={{
                minimap: { enabled: false },
                fontSize: 14,
                lineNumbers: 'on',
                roundedSelection: false,
                scrollBeyondLastLine: false,
                automaticLayout: true,
                readOnly: contest.status !== 'ONGOING' // Блокировать ввод если не ONGOING
              }}
            />
          </div>

          <div className="output-section">
            <h3>Output</h3>
            {error && <div className="error-message">{error}</div>}
            {output && <pre className="output-content">{output}</pre>}
          </div>
        </div>

        

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