import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './App.css';
import MainPage from './pages/mainPage/MainPage';  // MainPage 컴포넌트를 임포트
import ChatRoomPage from './pages/chatRoomPage/ChatRoomPage';
import Login from './Login';
import Join from './Join';
import PrivateRoute from "./PrivateRoute";
import FollowListPage from "./pages/followListPage/FollowListPage";

function App() {
  return (
      <Router>
          <div className="body">
              <Routes>
                  <Route path="/" element={<MainPage />} />
                  <Route path="/chats/:chatRoomId" element={<PrivateRoute><ChatRoomPage /></PrivateRoute>} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/join" element={<Join />} />
                  <Route path="/follow" element={<PrivateRoute><FollowListPage /></PrivateRoute>} />
              </Routes>
          </div>
      </Router>
  );
}

export default App;