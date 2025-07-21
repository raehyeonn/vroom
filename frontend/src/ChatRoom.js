import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Client } from '@stomp/stompjs';
import styles from './ChatRoom.module.css';
import VerifyChatRoomPasswordModal from './VerifyChatRoomPasswordModal';
import ChangeRoomNameModal from './ChangeRoomNameModal';
import ChatRoomDrawer from "./ChatRoomDrawer";

const ChatRoom = () => {
    const { chatRoomId } = useParams();
    const numericChatRoomId = Number(chatRoomId);

    const [chatRoomName, setChatRoomName] = useState('');
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState('');

    const [passwordRequired, setPasswordRequired] = useState(false); // 채팅방 입장 시 비밀번호 필요 여부를 나타내는 상태 변수(기본 값 = false)
    const [passwordVerified, setPasswordVerified] = useState(false);
    const [passwordModalOpen, setPasswordModalOpen] = useState(false);
    const [passwordInput, setPasswordInput] = useState('');

    const [hasNext, setHasNext] = useState(true);
    const [nextCursor, setNextCursor] = useState(null);
    const [loadingPastMessages, setLoadingPastMessages] = useState(false);
    const chatBoxRef = useRef(); // 기존 div에 ref 연결

    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);
    const textAreaRef = useRef(null);

    const [renameModalOpen, setRenameModalOpen] = useState(false);
    const [newRoomName, setNewRoomName] = useState('');

    const [isDrawerOpen, setIsDrawerOpen] = useState(false); // 서랍 열림 여부
    const [drawerView, setDrawerView] = useState('menu'); // 'menu' | 'participants' 등

    const [initialScrollDone, setInitialScrollDone] = useState(false);
    const [webSocketConnected, setWebSocketConnected] = useState(false);

    const connectWebSocket = useCallback(() => {
        console.log("✅ WebSocket 연결 시도");

        // 브라우저 localStorage에서 로그인 토큰을 가져옴
        const accessToken = localStorage.getItem('accessToken');

        // accessToken이 없는 경우
        if (!accessToken) {
            console.error("JWT 토큰이 없습니다."); // 콘솔에 에러 메시지 출력
            return; // 웹소켓 연결 작업 중단
        }

        // 클라이언트 준비
        const client = new Client({
            brokerURL: "ws://localhost:8080/ws", // WebSocket 서버 주소
            reconnectDelay: 5000, // 웹소켓 연결이 끊어진 경우, 자동으로 5초마다 재연결 시도
            connectHeaders: { Authorization: `Bearer ${accessToken}` // 웹소켓 서버에 연결할 때 같이보내는 헤더 정보
            },

            // STOMP 클라이언트가 서버와 웹소켓 연결을 성공적으로 맺었을 때 호출되는 함수
            onConnect: () => {
                console.log("✅ WebSocket 연결 성공");

                console.log("✅ 채팅방 정보 가져오기 시도");
                fetchChatRoomName();

                console.log("✅ 채팅방 지난 메시지 가져오기 시도");
                fetchPastMessages();

                setWebSocketConnected(true);

                client.subscribe(`/sub/${numericChatRoomId}`, (message) => {
                    // 서버로부터 받은 JSON 문자열을 JavaScript 객체로 변환
                    const parsedMessage = JSON.parse(message.body);

                    console.log('[WebSocket message received]', parsedMessage);

                    /*
                    setMessages 호출되면서 내부 함수가 실행되고,
                    기존 messages 배열의 요소들을 복사해 새로운 messages 배열에 넣은 뒤
                    마지막에 parsedMessage 를 추가하여 새로운 배열 생성 마무리
                    */
                    setMessages((prev) => [...prev, parsedMessage]);
                });

                client.subscribe(`/sub/${numericChatRoomId}/info`, (message) => {
                    const parsedMessage = JSON.parse(message.body);

                    console.log('[WebSocket message received]', parsedMessage);
                    setChatRoomName(parsedMessage.roomName);

                });
            },

            onStompError: (frame) => {
                console.error("STOMP 에러:", frame.headers["message"]);
            }
        });

        // 클라이언트 실행
        client.activate();


        stompClient.current = client;
    }, [numericChatRoomId]);

    const disconnectWebSocket = () => {
        stompClient.current?.deactivate();
    };

    // 채팅방 입장 시 비밀번호 여부 확인
    // 비밀번호 설정된 채팅방이라면 비밀번호 입력
    useEffect(() => {
        console.log("🔁 첫번째 useEffect 진입", chatRoomId, connectWebSocket);
        // 브라우저 localStorage에서 로그인 토큰을 가져옴
        const accessToken = localStorage.getItem('accessToken');

        // accessToken이 없는 경우
        if (!accessToken) {
            console.error("JWT 토큰이 없습니다."); // 콘솔에 에러 메시지 출력
            alert("로그인이 필요한 기능입니다.");
            // window.location.href = "/login";
            return; // 웹소켓 연결 작업 중단
        }

        // 채팅방이 필요한지 확인하는 비동기 함수
        const checkPasswordRequired = async () => {
            try {
                // axios.get은 비동기 함수이기 때문에 await 키워드를 붙여서 응답이 올 때까지 기다린다.
                // await가 없으면 아래 isRequired에서 에러 또는 undefined가됨.
                const response = await axios.get(`http://localhost:8080/api/chat-rooms/${chatRoomId}/passwordRequired`,{
                        headers: {
                            Authorization: `Bearer ${accessToken}`
                        },
                        withCredentials: true
                });

                const isRequired = response.data;
                setPasswordRequired(isRequired); // 상태 값을 isRequired 에 맞추어 변경, 채팅방 비밀번호 입력 모달을 띄우기 위한 조건으로 사용됨.

                if (isRequired) {
                    setPasswordModalOpen(true); // 채팅방 비밀번호 입력 모달 열기
                } else {
                    await enterWithoutPassword();
                }
            } catch (err) {
                console.error('비밀번호 필요 여부 확인 실패:', err);
            }
        };

        console.log("✅ checkPasswordRequired 실행됨");
        checkPasswordRequired();
        return () => disconnectWebSocket();
    }, [chatRoomId, connectWebSocket]);

    const enterWithoutPassword = async () => {
        console.log('enterWithoutPassword 호출됨');

        // 브라우저의 localStorage에서 accessToken이라는 이름으로 저장된 값을 가져온다.
        const accessToken = localStorage.getItem('accessToken');

        console.log('accessToken:', accessToken);

        if (!accessToken) {
            console.log('accessToken 없음, 함수 종료');
            return;
        }

        console.log('axios 요청 시작');
        try {
            const response = await axios.post(`http://localhost:8080/api/chat-rooms/${chatRoomId}/enter`,{}, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true
            });

            const data = response.data;
            console.log('axios 성공:', response.data);

            if (data) {
                setPasswordVerified(true);
                connectWebSocket();
            } else {
                console.error("입장 실패: 서버에서 false 반환");
            }
        } catch (e) {
            console.error("비밀번호 없는 채팅방 입장 실패:", e);
        }
    };

    // 비밀번호 검증 시
    const verifyPasswordAndEnter = async () => {
        console.log("✅ verifyPasswordAndEnter 시도");

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
                console.log("✅ verifyPasswordAndEnter 성공");
                connectWebSocket(); // ✅ 인증 후 WebSocket 연결
            } else {
                alert('비밀번호가 틀렸습니다.');
            }
        } catch (err) {
            console.error('비밀번호 검증 실패:', err);
        }
    };

    // 채팅방 정보 가져오기
    const fetchChatRoomName = async () => {
        // 브라우저 localStorage에서 로그인 토큰을 가져옴
        const accessToken = localStorage.getItem('accessToken');

        // accessToken이 없는 경우
        if (!accessToken) {
            console.error("JWT 토큰이 없습니다."); // 콘솔에 에러 메시지 출력
            return; // 웹소켓 연결 작업 중단
        }

        try {
            const response = await axios.get(`http://localhost:8080/api/chat-rooms/${chatRoomId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
                withCredentials: true
            });
            console.log("✅ 채팅방 정보 가져오기 성공");
            setChatRoomName(response.data.roomName);
        } catch (err) {
            console.error(err);
        }
    };

    const changeRoomName = async (roomName) => {
        const accessToken = localStorage.getItem('accessToken');
        if (!accessToken) {
            console.error("JWT 토큰이 없습니다.");
            return;
        }

        if (!roomName.trim()) {
            alert("방 제목을 입력하세요.");
            return;
        }

        try {
            const response = await axios.post(
                `http://localhost:8080/api/chat-rooms/${chatRoomId}/change-name`,
                { roomName: roomName }, // ✅ 서버 DTO 맞춤
                {
                    headers: { Authorization: `Bearer ${accessToken}` },
                    withCredentials: true,
                }
            );

            if (response.status === 200) {
                alert("방 이름이 변경되었습니다.");
                setChatRoomName(roomName); // ✅ UI 반영
                setRenameModalOpen(false);
                setNewRoomName(''); // 입력 초기화
            }
        } catch (err) {
            console.error("방 이름 변경 실패:", err);
            alert("방 이름 변경 중 오류가 발생했습니다.");
        }
    };

    // 채팅방 입장 시 과거 메시지 최대 20개 가져오기
    const fetchPastMessages = async () => {
        const token = localStorage.getItem('accessToken');

        try {
            const response = await axios.get(
                `http://localhost:8080/api/chat-rooms/${chatRoomId}/messages`,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                    withCredentials: true,
                }
            );

            console.log("✅ 채팅방 지난 메시지 가져오기 성공");
            const data = response.data;
            const messagesFromServer = data.chatMessages.reverse();

            setMessages(messagesFromServer);
            setHasNext(data.hasNext);
            setNextCursor(data.nextCursor);

            setTimeout(() => {
                if (!initialScrollDone && messagesEndRef.current) {
                    messagesEndRef.current.scrollIntoView({ behavior: 'auto' });
                    setInitialScrollDone(true); // 한 번만 실행되도록 설정
                }
            }, 100);
        } catch (err) {
            console.error('❌ 과거 메시지 불러오기 실패:', err);
        }
    };

    const fetchScrollPastMessages = useCallback(async () => {

        console.log('[fetchPastMessages] called')
        console.log('[fetchPastMessages]', new Date().toLocaleTimeString());

        console.log('[scroll event]', chatBoxRef.current.scrollTop);
        if (!hasNext || loadingPastMessages) return;

        setLoadingPastMessages(true);
        const token = localStorage.getItem('accessToken');

        try {
            const response = await axios.get(`http://localhost:8080/api/chat-rooms/${chatRoomId}/messages`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                params: nextCursor ? { cursor: nextCursor } : {},
                withCredentials: true
            });

            const data = response.data;
            const reversed = data.chatMessages.reverse(); // 최신 -> 과거 순이니까 역순으로

            console.log('[fetchPastMessages] 기존 메시지 수:', messages.length);
            console.log('[fetchPastMessages] 추가할 메시지 수:', reversed.length);

            setMessages((prev) => [...reversed, ...prev]);
            setHasNext(data.hasNext);
            setNextCursor(data.nextCursor);
        } catch (err) {
            console.error("과거 메시지 불러오기 실패", err);
        } finally {
            setLoadingPastMessages(false);
        }
    }, [chatRoomId, nextCursor, hasNext, loadingPastMessages]);

    // 위로 스크롤 하면 과거 채팅방 메시지 가져오기
    useEffect(() => {
        if (!webSocketConnected) return;

        const chatBox = chatBoxRef.current; // chatBoxRef.current는 className={styles.chatBox}이거를 가르키고 있음

        // DOM 요소인지 확인
        if (!(chatBox instanceof HTMLElement)) {
            return;
        }

        const handleScroll = () => {
            if (chatBox.scrollTop <= 30) {
                fetchScrollPastMessages();
            }
        };

        // DOM 요소에서 스크롤 이벤트 발생 시 handleScroll 함수 실행
        chatBox.addEventListener('scroll', handleScroll);

        // 컴포넌트가 언마운트 되거나 useEffect 다시 실행되기 전에 동작되는 클린업 함수
        return () => {
            // DOM 요소에 등록되었던 이벤트 제거
            chatBox.removeEventListener('scroll', handleScroll)
        };
    }, [webSocketConnected, fetchScrollPastMessages]);

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
                <span className={styles.chatRoomName}>{chatRoomName}</span>
                <img
                    className={styles.infoImg}
                    alt="안내표"
                    src="/hamburger_menu.png"
                    onClick={() => setIsDrawerOpen(true)}
                />
            </header>

            <div className={styles.chatBox} ref={chatBoxRef}>
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

            {renameModalOpen && (
                <ChangeRoomNameModal
                    isOpen={renameModalOpen}
                    onClose={() => setRenameModalOpen(false)}
                    onConfirm={changeRoomName}
                    value={newRoomName}
                    onChange={setNewRoomName}
                />
            )}

            <ChatRoomDrawer
                chatRoomId={chatRoomId}
                isDrawerOpen={isDrawerOpen}
                setIsDrawerOpen={setIsDrawerOpen}
                drawerView={drawerView}
                setDrawerView={setDrawerView}
                newRoomName={newRoomName}
                setNewRoomName={setNewRoomName}
                changeRoomName={changeRoomName}
            />
        </div>
    );
};

export default ChatRoom;