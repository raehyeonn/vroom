import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import './App.css';
import ChatRoomList from './ChatRoomList';  // ChatRoomList 컴포넌트를 임포트
import ChatRoom from './ChatRoom';
import Login from './Login';
import Join from './Join';
import PrivateRoute from "./PrivateRoute";
import FollowerList from "./FollowerList";

function App() {
  return (
      <Router>
          <div className="body">
              <Routes>
                  <Route path="/" element={<ChatRoomList />} />
                  <Route path="/chats/:chatRoomId" element={<PrivateRoute><ChatRoom /></PrivateRoute>} />
                  <Route path="/login" element={<Login />} />
                  <Route path="/join" element={<Join />} />
                  <Route path="/follow" element={<PrivateRoute><FollowerList /></PrivateRoute>} />
              </Routes>
          </div>
      </Router>
  );
}

export default App;