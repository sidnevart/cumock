import { Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage'; // Создадим эту страницу позже
import LoginPage from './pages/LoginPage'; // Создадим эту страницу позже
import RegisterPage from './pages/RegisterPage'; // Создадим эту страницу позже
import NotFoundPage from './pages/NotFoundPage'; // Опционально: страница 404
import Layout from './components/Layout'; // Создадим компонент обертки (хедер, футер и т.п.)
import ProtectedRoute from './components/ProtectedRoute';
import ProblemsPage from './pages/ProblemsPage'; // Импорт страницы списка проблем
import ProblemDetailsPage from './pages/ProblemDetailsPage'; // Импорт страницы деталей проблемы
import BattlePage from './pages/BattlePage'; // Импорт страницы Battle
import PvPContestPage from './pages/PvPContestPage'; // Импорт страницы матча PvP

function App() {
  return (
    <Layout> {/* Компонент обертки */}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Защищенный маршрут для списка проблем */}
        <Route path="/problems" element={<ProtectedRoute element={<ProblemsPage />} />} />

        {/* Маршрут для деталей проблемы (также защищенный) */}
        <Route path="/problems/:id" element={<ProtectedRoute element={<ProblemDetailsPage />} />} />

        {/* Маршрут для BattlePage */}
        <Route path="/battle" element={<ProtectedRoute element={<BattlePage />} />} />

        {/* Маршрут для активного матча PvP */}
        <Route path="/pvp/contest/:contestId" element={<ProtectedRoute element={<PvPContestPage />} />} />

        {/* Добавьте другие маршруты здесь по мере их реализации */}
        <Route path="*" element={<NotFoundPage />} /> {/* Маршрут для 404 */}
      </Routes>
    </Layout>
  );
}

export default App;