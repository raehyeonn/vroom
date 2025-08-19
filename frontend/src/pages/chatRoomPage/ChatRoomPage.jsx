import React, { useState, useEffect, useRef, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import { Client } from '@stomp/stompjs';
import styles from './ChatRoomPage.module.css';
import VerifyChatRoomPasswordModal from '../../VerifyChatRoomPasswordModal';
import RenameChatRoomModal from './RenameChatRoomModal';
import ChatRoomDrawer from "../../ChatRoomDrawer";
import {
    getChatRoomDetail,
    getChatRoomPasswordRequired,
    joinChatRoom, updateChatRoomName
} from "../../api/chatRoomApi";

const ChatRoomPage = () => {
    const { chatRoomId } = useParams();
    const numericChatRoomId = Number(chatRoomId);

    const [isWebSocketConnected, setIsWebSocketConnected] = useState(false); // 웹소켓 연결 여부 -> 중복 연결 방지 및 웹소켓 연결 후 채팅 가져오기위해

    const [name, setName] = useState('');
    const [code, setCode] = useState('');
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState('');

    const [passwordRequired, setPasswordRequired] = useState(false); // 채팅방 입장 시 비밀번호 필요 여부를 나타내는 상태 변수(기본 값 = false)
    const [passwordInputModalOpen, setPasswordInputModalOpen] = useState(false);
    const [passwordInput, setPasswordInput] = useState('');
    const [passwordVerified, setPasswordVerified] = useState(false);

    const [hasNext, setHasNext] = useState(true);
    const [nextCursor, setNextCursor] = useState(null);
    const [loadingPastMessages, setLoadingPastMessages] = useState(false);
    const chatBoxRef = useRef(); // 기존 div에 ref 연결

    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);
    const textAreaRef = useRef(null);

    const [isRenameChatRoomModalOpen, setIsRenameChatRoomModalOpen] = useState(false);
    const [newName, setNewName] = useState('');

    const [isDrawerOpen, setIsDrawerOpen] = useState(false); // 서랍 열림 여부
    const [drawerView, setDrawerView] = useState('menu'); // 'menu' | 'participants' 등

    const [initialScrollDone, setInitialScrollDone] = useState(false);

    const subscriptionRef = useRef(null);
    const infoSubscriptionRef = useRef(null);


    const connectWebSocket = useCallback(() => {
        if (stompClient.current?.connected || isWebSocketConnected) {
            console.log("🔁 이미 WebSocket에 연결되어 있음");
            return;
        }

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
                fetchChatRoomDetail();

                console.log("✅ 채팅방 지난 메시지 가져오기 시도");
                fetchPastMessages();

                setIsWebSocketConnected(true);

                if (!subscriptionRef.current) {
                    subscriptionRef.current = client.subscribe(`/sub/${numericChatRoomId}`, (message) => {
                        const parsedMessage = JSON.parse(message.body);
                        console.log('[WebSocket message received]', parsedMessage);
                        setMessages((prev) => [...prev, parsedMessage]);
                    });
                }

                if (!infoSubscriptionRef.current) {
                    infoSubscriptionRef.current = client.subscribe(`/sub/${numericChatRoomId}/info`, (message) => {
                        const parsedMessage = JSON.parse(message.body);
                        console.log('[WebSocket message received]', parsedMessage);
                        setName(parsedMessage.name);
                    });
                }
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
        if (subscriptionRef.current) {
            subscriptionRef.current.unsubscribe();
            subscriptionRef.current = null;
        }
        if (infoSubscriptionRef.current) {
            infoSubscriptionRef.current.unsubscribe();
            infoSubscriptionRef.current = null;
        }
        stompClient.current?.deactivate();
        setIsWebSocketConnected(false);
    };

    useEffect(() => {
        const checkPasswordRequired = async () => {
            try {
                const response = await getChatRoomPasswordRequired(chatRoomId);
                setPasswordRequired(response);

                if (response) {
                    setPasswordInputModalOpen(true);
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
        try {
            const response = await joinChatRoom(chatRoomId);

            if(response) {
                setPasswordVerified(true);
                connectWebSocket();
            } else {
                alert("입장에 실패했습니다.");
            }
        } catch (error) {
            console.error(error);
            alert("네트워크 오류입니다.");
        }
    };

    const verifyPasswordAndEnter = async (password) => {
        try {
            const response = await joinChatRoom(chatRoomId, password);

            if (response) {
                setPasswordVerified(true);
                setPasswordInputModalOpen(false);
                connectWebSocket();
            } else {
                alert("입장에 실패했습니다.");
            }
        } catch (error) {
            console.error(error);
            alert("네트워크 오류입니다.");
        }
    };





    // 채팅방 정보 가져오기
    const fetchChatRoomDetail = async () => {
        try {
            const response = await getChatRoomDetail(chatRoomId);
            setCode(response.code);
            setName(response.name);
        } catch (err) {
            console.error(err);
        }
    };

    const changeRoomName = async (newName) => {

        if (!newName.trim()) {
            alert("방 제목을 입력하세요.");
            return;
        }

        try {
            const response = await updateChatRoomName(chatRoomId, newName);

            if (response) {
                alert("방 이름이 변경되었습니다.");
                setName(response) // ✅ UI 반영
                setIsRenameChatRoomModalOpen(false);
                setNewName('');
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
        if (!isWebSocketConnected) return;

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
    }, [isWebSocketConnected, fetchScrollPastMessages]);

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
                isOpen={passwordInputModalOpen}
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
                <span className={styles.chatRoomName}>{name}</span>
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

            {isRenameChatRoomModalOpen && (
                <RenameChatRoomModal
                    isOpen={isRenameChatRoomModalOpen}
                    value={newName} // 부모의 현재 상태값을 넘겨줌 (빈문자열)
                    onChange={setNewName} // 자식에서 입력한 이름으로 변경
                    onClose={() => setIsRenameChatRoomModalOpen(false)} // 모달에서 renameModalOpen 이걸 true -> false로 바꿀수있는 함수 전달
                    onConfirm={changeRoomName} // 모달에서 확인 버튼을 누르면 changeRoomName함수 전달

                />
            )}

            <ChatRoomDrawer
                chatRoomId={chatRoomId}
                isDrawerOpen={isDrawerOpen}
                setIsDrawerOpen={setIsDrawerOpen}
                drawerView={drawerView}
                setDrawerView={setDrawerView}
                newRoomName={newName}
                setNewRoomName={setNewName}
                changeRoomName={changeRoomName}
            />
        </div>
    );
};

export default ChatRoomPage;