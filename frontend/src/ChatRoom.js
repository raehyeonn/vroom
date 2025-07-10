import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import styles from './ChatRoom.module.css';
import VerifyChatRoomPasswordModal from './VerifyChatRoomPasswordModal';

const ChatRoom = () => {
    const { chatRoomId } = useParams();
    const numericChatRoomId = Number(chatRoomId);

    const [chatRoomName, setChatRoomName] = useState('');
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState('');

    const [passwordRequired, setPasswordRequired] = useState(false);
    const [passwordVerified, setPasswordVerified] = useState(false);
    const [passwordModalOpen, setPasswordModalOpen] = useState(false);
    const [passwordInput, setPasswordInput] = useState('');

    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);
    const textAreaRef = useRef(null);

    const connectWebSocket = useCallback(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) {
            console.error("JWT 토큰이 없습니다.");
            return;
        }

        const client = new Client({
            brokerURL: "ws://localhost:8080/ws",
            reconnectDelay: 5000,
            connectHeaders: { Authorization: `Bearer ${token}` },
            onConnect: () => {
                console.log("✅ WebSocket 연결 성공");

                client.subscribe(`/sub/${numericChatRoomId}`, (message) => {
                    const body = JSON.parse(message.body);
                    setMessages((prev) => [...prev, body]);
                });
            },
            onStompError: (frame) => {
                console.error("STOMP 에러:", frame.headers["message"]);
            },
        });

        client.activate();
        stompClient.current = client;
    }, [numericChatRoomId]);

    const disconnectWebSocket = () => {
        stompClient.current?.deactivate();
    };

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (!token) return;

        const checkPasswordRequired = async () => {
            try {
                const res = await fetch(`http://localhost:8080/api/chat-rooms/${chatRoomId}/passwordRequired`, {
                    headers: { Authorization: `Bearer ${token}` },
                    credentials: 'include'
                });
                const isRequired = await res.json();
                setPasswordRequired(isRequired);

                if (isRequired) {
                    setPasswordModalOpen(true); // ✅ 모달 열기
                } else {
                    enterWithoutPassword();
                }
            } catch (err) {
                console.error('비밀번호 필요 여부 확인 실패:', err);
            }
        };

        checkPasswordRequired();
        return () => disconnectWebSocket();
    }, [chatRoomId, connectWebSocket]);

    useEffect(() => {
        const fetchChatRoomName = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) return;

            try {
                const res = await fetch(`http://localhost:8080/api/chat-rooms/${chatRoomId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                    credentials: 'include',
                });
                if (!res.ok) throw new Error('채팅방 정보를 가져오지 못했습니다.');
                const data = await res.json();
                setChatRoomName(data.roomName);
            } catch (err) {
                console.error(err);
            }
        };

        fetchChatRoomName();
    }, [chatRoomId]);

    const enterWithoutPassword = async () => {
        const token = localStorage.getItem('accessToken');
        if (!token) return;

        try {
            const enterRes = await fetch(`http://localhost:8080/api/chat-rooms/${chatRoomId}/enter`, {
                method: 'POST',
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                credentials: 'include'
            });

            const data = await enterRes.json();

            if (data.result) {
                setPasswordVerified(true);
                connectWebSocket(); // ✅ 인증 성공 후 웹소켓 연결
            } else {
                console.error("입장 실패: 서버에서 false 반환");
            }
        } catch (e) {
            console.error("비밀번호 없는 채팅방 입장 실패:", e);
        }
    };

    // ✅ 비밀번호 검증 시
    const verifyPasswordAndEnter = async () => {
        const token = localStorage.getItem('accessToken');
        if (!token) return;

        try {
            const res = await fetch(`http://localhost:8080/api/chat-rooms/${chatRoomId}/enter-with-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                },
                credentials: 'include',
                body: JSON.stringify({ password: passwordInput }),
            });

            const data = await res.json();
            if (data.result) {
                setPasswordModalOpen(false);
                setPasswordVerified(true);
                connectWebSocket(); // ✅ 인증 후 WebSocket 연결
            } else {
                alert('비밀번호가 틀렸습니다.');
            }
        } catch (err) {
            console.error('비밀번호 검증 실패:', err);
        }
    };

    const currentMemberId = localStorage.getItem('memberId');

    const sendMessage = () => {
        if (!message.trim() || !stompClient.current?.connected) return;

        const payload = { content: message };
        stompClient.current.publish({
            destination: `/pub/api/chat-rooms/${chatRoomId}`,
            body: JSON.stringify(payload),
        });
        setMessage('');
        if (textAreaRef.current) textAreaRef.current.style.height = '40px';
    };

    const handleInputChange = (e) => {
        const textarea = textAreaRef.current;
        setMessage(e.target.value);

        const lineCount = e.target.value.split('\n').length;
        const lineHeight = 20;
        const maxLines = 5;
        const newHeight = Math.min(lineCount, maxLines) * lineHeight;

        textarea.style.height = 'auto';
        textarea.style.height = `${newHeight}px`;
    };

    if (passwordRequired && !passwordVerified) {
        return (
            <VerifyChatRoomPasswordModal
                isOpen={passwordModalOpen}
                onClose={() => window.close()}
                onConfirm={verifyPasswordAndEnter}
                password={passwordInput}
                setPassword={setPasswordInput}
            />
        );
    }

    return (
        <div className={styles.chatContainer}>
            <header className={styles.chatRoomHeader}>
                <a href="/">&lt;</a>
                <span className={styles.chatRoomName}>채팅방: {chatRoomName}</span>
                <img
                    className={styles.infoImg}
                    alt="안내표"
                    src="/info_Icon.png"
                    onClick={() => alert(`채팅방 이름: ${chatRoomName}`)}
                />
            </header>

            <div className={styles.chatBox}>
                {messages.map((msg, idx) => {
                    const isMine = msg.senderId === Number(currentMemberId);
                    const time = msg.sentAt?.slice(11, 19);
                    return (
                        <div key={idx} className={styles.messageWrapper}>
                            {!isMine && <span className={styles.senderNickname}>{msg.senderNickname}</span>}
                            <div className={`${styles.chatMessage} ${isMine ? styles.mine : styles.others}`}>
                                {msg.content}
                            </div>
                            <span className={`${styles.sendTime} ${isMine ? styles.mine : styles.others}`}>
                                {time}
                            </span>
                        </div>
                    );
                })}
                <div ref={messagesEndRef} />
            </div>

            <div className={styles.chatInput}>
                <textarea
                    ref={textAreaRef}
                    placeholder="메시지를 입력하세요"
                    value={message}
                    onChange={handleInputChange}
                    onKeyDown={(e) => {
                        if (e.key === 'Enter' && !e.shiftKey) {
                            e.preventDefault();
                            sendMessage();
                        }
                    }}
                    className={styles.textarea}
                />
                <button onClick={sendMessage}>전송</button>
            </div>
        </div>
    );
};

export default ChatRoom;