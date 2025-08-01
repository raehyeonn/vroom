import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from './ChatRoomList.module.css';
import { jwtDecode } from 'jwt-decode';
import CreateChatRoomModal from './CreateChatRoomModal';
import AddFriendModal from './AddFriendModal';

const ChatRoomList = () => {
    const [chatRooms, setChatRooms] = useState([]); // 채팅방 목록을 저장할 상태 변수, 초기값은 빈 배열
    const [loading, setLoading] = useState(true); // 데이터를 가져오는 동안 로딩 상태를 나타내는 변수, 초기값은 true(=데이터를 가져오는 동안 로딩 화면 표시)
    const [error, setError] = useState(null); // API 호출 시 오류 메세지를 저장하는 변수, 초기값은 null
    const navigate = useNavigate();
    const [isMember, setIsMember] = useState(false);
    const [currentPage, setCurrentPage] = useState(0); // 0부터 시작
    const [totalPages, setTotalPages] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 열기/닫기 상태 관리
    const [searchCode, setSearchCode] = useState('');
    const [activeTab, setActiveTab] = useState('all'); // 채팅방 목록 탭 관리

    const [userInfo, setUserInfo] = useState(null);
    const [isAddFriendModalOpen, setIsAddFriendModalOpen] = useState(false);

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

    useEffect(() => {
        const token = localStorage.getItem('accessToken');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                const roles = decoded.authorities || [];
                console.log('Decoded roles:', roles);

                if (roles.includes('ROLE_MEMBER')) {
                    setIsMember(true);
                }
            } catch (err) {
                console.error('JWT decode error:', err);
            }
        }
    }, []);

    useEffect(() => {
        const fetchChatRoomsByPage = async () => {
            setLoading(true);

            try {
                let response;

                if (activeTab === 'my') {
                    const token = localStorage.getItem('accessToken');

                    if (!token) {
                        alert('로그인이 필요합니다!');
                        setLoading(false);
                        return;
                    }

                    response = await axios.get('http://localhost:8080/api/chat-rooms/me', {
                        headers: { Authorization: `Bearer ${token}` },
                        withCredentials: true,
                        params: { page: currentPage, size: 10, sort: 'enteredAt,desc' },
                    });
                } else {
                    response = await axios.get('http://localhost:8080/api/chat-rooms', {
                        params: { page: currentPage, size: 10, sort: 'createdAt,desc' },
                    });
                }

                const { content, totalPages } = response.data;

                setChatRooms(content.filter(room => room.name));
                setTotalPages(totalPages);
            } catch (err) {
                console.error('API 호출 에러:', err);
                setError('채팅방 목록을 가져오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        fetchChatRoomsByPage();
    }, [activeTab, currentPage]);

    useEffect(() => {
        setCurrentPage(0);
    }, [activeTab]);

    useEffect(() => {
        const fetchUserInfo = async () => {
            const token = localStorage.getItem('accessToken');
            if (!token) return;

            try {
                const response = await axios.get('http://localhost:8080/api/members/me', {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    withCredentials: true
                });

                setUserInfo(response.data);
            } catch (err) {
                console.error('사용자 정보 조회 실패:', err);
            }
        };

        fetchUserInfo();
    }, []);

    const handlePageChange = (pageIndex) => {
        setCurrentPage(pageIndex);
    };

    const handleCreateRoom = async (roomData) => {
        try {
            // 로컬 스토리지에서 토큰 가져오기
            const token = localStorage.getItem('accessToken');

            if (!token) {
                alert('로그인이 필요합니다!');
                return;
            }

            // 헤더에 Authorization 토큰을 추가하여 API 호출
            const response = await axios.post(
                'http://localhost:8080/api/chat-rooms',
                { name: roomData.name,
                    hidden: roomData.hidden,
                    passwordRequired: roomData.passwordRequired,
                    password: roomData.password }, // 채팅방 이름
                {
                    headers: {
                        'Authorization': `Bearer ${token}`, // Authorization 헤더에 JWT 토큰 추가
                    },
                    withCredentials: true
                }
            );

            // 성공적으로 생성되면 알림
            alert(`${response.data.name} 채팅방이 생성되었습니다!`);
            setIsModalOpen(false); // 모달 닫기

            // 채팅방 목록을 갱신 (추가 요청 또는 새로고침)
            setCurrentPage(0);
        } catch (err) {
            console.error('채팅방 생성 오류:', err);
            alert('채팅방 생성 실패');
        }
    };

    const handleSearch = async () => {

        const token = localStorage.getItem('accessToken');

        if (!token) {
            alert('로그인이 필요합니다!');
            return;
        }

        if (!searchCode) {
            alert('코드를 입력해주세요!');
            return;
        }

        try {
            const response = await axios.get(
                `http://localhost:8080/api/chat-rooms/by-code/${searchCode}`,
                {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    withCredentials: true
                }
            );
            const chatRoom = response.data;

            setChatRooms([chatRoom]); // 배열로 덮어씌움
            setTotalPages(1);         // 페이지네이션 숨기거나 고정
            setCurrentPage(0);
        } catch (err) {
            console.error('채팅방 검색 오류:', err);
            alert('해당 코드의 채팅방을 찾을 수 없습니다.');
        }
    };

    const handleEnterRoom = (room) => {
        window.open(`/chats/${room.id}`, '_blank');
    };

    // 로딩 중일 때 표시할 메시지
    if (loading) {
        return <div>로딩 중...</div>;
    }

    // 에러가 있을 경우 표시할 메시지
    if (error) {
        return <div>{error}</div>;
    }

    return (
        <div className={styles.pageContainer}>
            <div className={styles.pageHeader}>
                <span className={styles.logo}>VROOM</span>

                {localStorage.getItem('accessToken')
                    ? (<button className={styles.logoutButton} onClick={handleLogout}>로그아웃</button>)
                    : (<button className={styles.loginButton} onClick={() => navigate('/login')}>로그인 / 회원가입</button>)
                }
            </div>

            <div className={styles.pageBody}>
                {userInfo && (
                    <div>
                        <div className={styles.addButtons}>
                            <img
                                className={styles.addFriends}
                                alt="친구추가"
                                src="/add_friend_icon.png"
                                onClick={() => setIsAddFriendModalOpen(true)}
                            />
                            <img
                                className={styles.addChatRooms}
                                alt="채팅방추가"
                                src="/add_chat_icon.png"
                            />
                        </div>

                        <div className={styles.profile}>
                            <p className={styles.nickname}>{userInfo.nickname}</p>
                            <div className={styles.followWrap}>
                                <div className={styles.follower}>
                                    <p>팔로워</p>
                                    <p className={styles.count} onClick={() => navigate('/follow?tab=followers')}>{userInfo.followerCount}</p>
                                </div>
                                <div className={styles.following}>
                                    <p>팔로잉</p>
                                    <p className={styles.count} onClick={() => navigate('/follow?tab=following')}>{userInfo.followingCount}</p>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <div className={styles.chatRoomListSection}>
                    <input type="text"
                           placeholder="코드를 입력하세요"
                           value={searchCode}
                           onChange={(e) => setSearchCode(
                               e.target.value)}></input>
                    <button onClick={handleSearch}>검색</button>

                    <div className={styles.chatRoomListTop}>
                        <div className={styles.tabButtons}>
                            <button
                                className={activeTab === 'all'
                                    ? styles.activeTab : styles.inactiveTab}
                                onClick={() => setActiveTab('all')}
                            >
                                전체 채팅방
                            </button>
                            <button
                                className={activeTab === 'my' ? styles.activeTab
                                    : styles.inactiveTab}
                                onClick={() => setActiveTab('my')}
                            >
                                나의 채팅방
                            </button>
                        </div>
                        {isMember && (
                            <button className={styles.createChatRoomButton}
                                    onClick={() => setIsModalOpen(true)}>채팅방
                                만들기</button>)}
                    </div>

                    <ul className={styles.chatRoomList}>
                        {chatRooms.map((room, index) => (
                            <li key={index} className={styles.chatRoom}>
                                <p className={styles.chatRoomName}>{room.name}</p>
                                <button className={styles.enterChatRoomButton}
                                        onClick={() => handleEnterRoom(room)}>입장 >
                                </button>
                            </li>
                        ))}
                    </ul>

                    <div className={styles.pagination}>
                        {Array.from({length: totalPages}, (_, i) => (
                            <button
                                key={i}
                                onClick={() => handlePageChange(i)}
                                className={currentPage === i
                                    ? styles.activePageButton
                                    : styles.pageButton}
                            >
                                {i + 1}
                            </button>
                        ))}
                    </div>
                </div>
            </div>

            <CreateChatRoomModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)} // 모달 닫기
                onCreate={handleCreateRoom} // 방 생성 함수
            />

            <AddFriendModal
                isOpen={isAddFriendModalOpen}
                onClose={() => setIsAddFriendModalOpen(false)}
            />

        </div>
    );
};

export default ChatRoomList;