import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from './ChatRoomList.module.css';
import { jwtDecode } from 'jwt-decode';

const ChatRoomList = () => {
    const [chatRooms, setChatRooms] = useState([]); // 채팅방 목록을 저장할 상태 변수, 초기값은 빈 배열
    const [loading, setLoading] = useState(true); // 데이터를 가져오는 동안 로딩 상태를 나타내는 변수, 초기값은 true(=데이터를 가져오는 동안 로딩 화면 표시)
    const [error, setError] = useState(null); // API 호출 시 오류 메세지를 저장하는 변수, 초기값은 null
    const navigate = useNavigate();

    const [isMember, setIsMember] = useState(false);

    const handleLogout = async () => {
        try {
            localStorage.removeItem('accessToken');
            localStorage.removeItem('memberId');

            await axios.post(
                'http://localhost:8080/api/auth/logout',
                {},
                { withCredentials: true }
            );

            alert('로그아웃 되었습니다.');
            window.location.reload(); // 현재 페이지 리로딩
        } catch (err) {
            console.error('로그아웃 오류:', err);
            alert('로그아웃 실패');
        }
    };

    // useEffect는 컴포넌트가 렌더링된 후에 실행되는 부수 효과를 처리하는 데 사용
    // 여기서 [] 빈 배열을 두 번째 인자로 주면 컴포넌트가 처음 렌더링될 때 한 번만 실행됩니다.
    useEffect(() => {
        console.log('useEffect 실행됨');

        const token = localStorage.getItem('accessToken'); // 토큰을 localStorage에서 가져오기
        if (token) {
            try {
                const decoded = jwtDecode(token); // JWT 디코딩
                const roles = decoded.authorities || []; // roles가 배열로 존재한다고 가정
                console.log('Decoded roles:', roles);

                if (roles.includes('ROLE_MEMBER')) { // 권한에 ROLE_MEMBER가 포함되어 있는지 확인
                    setIsMember(true); // 권한이 있다면 채팅방 만들기 버튼 보이기
                }
            } catch (err) {
                console.error('JWT decode error:', err);
            }
        }


        // 채팅방 목록을 가져오는 비동기 함수(async 키워드가 붙어있어 비동기적으로 처리)
        const fetchChatRooms = async () => {
            try {
                // axios.get()은 http://localhost:8080/api/chats/room에서 채팅방 목록을 가져옴
                // await를 사용하여 비동기적으로 데이터를 기다림.
                const response = await axios.get('http://localhost:8080/api/chat-rooms', {
                    params: {
                        page: 0, // 첫 페이지
                        size: 10, // 한 페이지에 보여줄 채팅방 수
                    },
                });

                console.log('API 응답:', response.data);

                setChatRooms(response.data.content.filter(room => room.name)); // 각 채팅방 객체에서 roomName이 존재하는 채팅방만 필터링하여 chatRooms 상태에 저장합니다.
            } catch (err) {
                console.error('API 호출 에러:', err);
                setError('채팅방 목록을 가져오는 데 실패했습니다.');
            } finally {
                setLoading(false); // loading 상태를 false로 설정하여 로딩이 끝났음을 표시합니다.
            }
        };

        fetchChatRooms(); // fetchChatRooms 함수를 호출하여 채팅방 목록을 가져옵니다.
    }, []);

    // 로딩 중일 때 표시할 메시지
    if (loading) {
        return <div>Loading...</div>;
    }

    // 에러가 있을 경우 표시할 메시지
    if (error) {
        return <div>{error}</div>;
    }

    return (
        <div className={styles.chatRoomListContainer}>
            <header className={styles.header}>
                <span className={styles.headerTitle}>VROOM</span>

                {localStorage.getItem('accessToken')
                    ? (<button className={styles.logoutButton} onClick={handleLogout}>로그아웃</button>)
                    : (<button className={styles.loginButton} onClick={() => navigate('/login')}>로그인 / 회원가입</button>)
                }
            </header>


            <div>
                <div>
                    <p className={styles.list}>현재 채팅방 목록</p>
                    {isMember && (<button onClick={() => alert('채팅방 만들기!')}>채팅방 만들기</button>)}
                </div>

                <ul className={styles.chatRoomList}>
                    {chatRooms.map((room, index) => (
                        <li key={index} className={styles.chatRoom}>
                            <p className={styles.chatRoomName}>{room.name}</p>
                            <button className={styles.enterButton}
                                    onClick={() => navigate(
                                        `/chats/${room.id}`)}>입장 >
                            </button>
                        </li>
                    ))}
                </ul>
            </div>
        </div>
    );
};

export default ChatRoomList;