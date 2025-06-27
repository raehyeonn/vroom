import React, { useState, useEffect, useRef, useCallback  } from 'react';
import { useParams } from 'react-router-dom'; // URL 파라미터 가져오기
import { Client } from '@stomp/stompjs'; // WebSocket을 위한 Stomp.js 사용
import styles from './ChatRoom.module.css';

const ChatRoom = () => {
    const { chatRoomId } = useParams();
    const numericChatRoomId = Number(chatRoomId);
    const [chatRoomName, setChatRoomName] = useState('');

    useEffect(() => {
        console.log("chatRoomId:", chatRoomId);  // chatRoomId 확인
        console.log("numericChatRoomId:", numericChatRoomId);  // numericRoomId 확인
    }, [chatRoomId]);

    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState("");
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);
    const textAreaRef = useRef(null);

    const connectWebSocket = useCallback(() => {
        // 로컬 스토리지에서 JWT 토큰을 가져와 WebSocket 연결 시 인증에 사용된다.
        const token = localStorage.getItem('accessToken');

        // 토큰이 없다면 에러 출력
        if (!token) {
            console.error("JWT 토큰이 없습니다. 로그인을 먼저 해주세요.");
            return;
        }

        // STOMP WebSocket 클라이언트 객체 생성
        const client = new Client({
            // WebSocket 서버의 URL
            brokerURL: "ws://localhost:8080/ws",

            // WebSocket 연결이 끊어졌을 때, 5초마다 재연결을 시도
            reconnectDelay: 5000,

            // WebSocket 연결을 시작할 때, Authorization 헤더에 Bearer ${token} 형태로 JWT 토큰을 넣어 서버에 인증 정보를 보냄
            connectHeaders: { Authorization: `Bearer ${token}` },

            // WebSocket 연결이 성공적으로 이루어졌을 때 실행되는 함수
            onConnect: () => {
                console.log("✅ WebSocket 연결 성공");

                // WebSocket 구독을 설정. 채팅방(chatRoomId)에 해당하는 메시지를 수신.
                client.subscribe(`/sub/${numericChatRoomId}`, (message) => {
                    const body = JSON.parse(message.body);
                    console.log('💬 받은 메시지:', body);
                    setMessages((prev) => [...prev, body]);
                });
            },

            // STOMP 프로토콜에서 에러가 발생했을 때 실행되는 콜백 함수
            onStompError: (frame) => {
                console.error("STOMP 에러:", frame.headers["message"]);
                console.error("상세:", frame.body);
            },
        });

        // STOMP 클라이언트를 활성화하여 WebSocket 연결을 시도
        client.activate();
        stompClient.current = client;
    }, [numericChatRoomId]);

    useEffect(() => {
        connectWebSocket();
        return () => disconnectWebSocket();
    }, [numericChatRoomId]);

    // WebSocket 해제 함수
    const disconnectWebSocket = () => {
        stompClient.current?.deactivate();
    };

    useEffect(() => {
        const fetchChatRoomName = async () => {
            const token = localStorage.getItem('accessToken'); // 토큰 가져오기
            if (!token) {
                console.error("❌ JWT 토큰이 없습니다. 로그인 먼저 해주세요.");
                return;
            }

            try {
                const response = await fetch(`http://localhost:8080/api/chat-rooms/${chatRoomId}`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    credentials: 'include'
                });

                if (!response.ok) {
                    throw new Error("채팅방 정보를 가져오는 데 실패했습니다.");
                }

                const data = await response.json();
                setChatRoomName(data.roomName);
            } catch (error) {
                console.error('❌ 채팅방 이름 조회 실패:', error);
            }
        };

        fetchChatRoomName();
    }, [chatRoomId]);

    const sendMessage = () => {
        console.log("✅ [sendMessage 호출됨]");
        console.log("입력한 메시지:", message);
        console.log("WebSocket 연결 상태:", stompClient.current?.connected);

        if (!message.trim() || !stompClient.current?.connected) return;

        const payload = { content: message };
        const destination = `/pub/api/chat-rooms/${chatRoomId}`;
        console.log("📦 전송할 destination:", destination);
        console.log("📨 전송할 payload:", JSON.stringify(payload));

        try {
            stompClient.current.publish({
                destination,
                body: JSON.stringify(payload),
            });
            console.log("✅ 메시지 publish 완료");
            setMessage("");

            if (textAreaRef.current) {
                textAreaRef.current.style.height = '40px'; // 정확히 2줄 높이로 초기화
            }
        } catch (err) {
            console.error("❌ 메시지 전송 에러:", err);
        }
    };

    const currentMemberId = localStorage.getItem('memberId');

    const handleInputChange = (e) => {
        const textarea = textAreaRef.current;
        setMessage(e.target.value);

        // 줄 수 계산
        const lineCount = e.target.value.split('\n').length;

        // 줄당 높이
        const lineHeight = 20; // px
        const maxLines = 5;

        // 높이 계산: 줄 수 × 줄당 높이
        const newHeight = Math.min(lineCount, maxLines) * lineHeight;

        textarea.style.height = 'auto';
        textarea.style.height = `${newHeight}px`;
    };

    return (
        <div className={styles.chatContainer}>

            <header className={styles.chatRoomHeader}>
                <a href="/">&lt;</a>
                <span className={styles.chatRoomName}>채팅방: {chatRoomName}</span>
                <img className={styles.infoImg} alt="안내표" src="/info_Icon.png"
                     onClick={() => alert(`채팅방 이름: ${chatRoomName}`)}></img>
            </header>

            <div className={styles.chatBox}>
                {messages.map((msg, idx) => {
                    const isMine = msg.senderId === Number(currentMemberId);
                    const sentAt = msg.sentAt;  // 예: "2025-05-06T14:00:00+09:00"
                    const time = sentAt.slice(11, 19);  // 결과: "14:00:00"
                    console.log("✅ sentAt 원본:", msg.sentAt);

                    return (
                        <div key={idx} className={styles.messageWrapper}>
                            {!isMine && (<span
                                className={styles.senderNickname}>{msg.senderNickname}</span>)}
                            <div className={`${styles.chatMessage} ${isMine
                                ? styles.mine : styles.others}`}>
                                {msg.content}
                            </div>
                            <span className={`${styles.sendTime} ${isMine
                                ? styles.mine
                                : styles.others}`}>{time}</span>
                        </div>
                    );
                })}
                <div ref={messagesEndRef}/>
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
