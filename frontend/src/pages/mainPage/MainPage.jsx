import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from './MainPage.module.css';
import { jwtDecode } from 'jwt-decode';
import CreateChatRoomModal from '../../CreateChatRoomModal';
import AddFriendModal from '../../AddFriendModal';
import SearchChatRoomModal from './SearchChatRoomModal';

import {
    createChatRoom,
    getChatRoomByCode,
    joinChatRoom
} from "../../api/chatRoomApi";

import { getChatRooms } from "../../api/chatRoomApi";
import {getMyChatRooms} from "../../api/memberApi";

const MainPage = () => {
    const [chatRooms, setChatRooms] = useState([]); // 채팅방 목록을 저장할 상태 변수, 초기값은 빈 배열
    const [loading, setLoading] = useState(true); // 데이터를 가져오는 동안 로딩 상태를 나타내는 변수, 초기값은 true(=데이터를 가져오는 동안 로딩 화면 표시)
    const [error, setError] = useState(null); // API 호출 시 오류 메세지를 저장하는 변수, 초기값은 null
    const navigate = useNavigate();
    const [isMember, setIsMember] = useState(false);
    const [currentPage, setCurrentPage] = useState(0); // 0부터 시작
    const [totalPages, setTotalPages] = useState(0);
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 열기/닫기 상태 관리
    const [code, setCode] = useState('');
    const [activeTab, setActiveTab] = useState('all'); // 채팅방 목록 탭 관리

    const [isPasswordInputModalOpen, setIsPasswordInputModal] = useState(false);
    const [selectedChatRoom, setSelectedChatRoom] = useState(null);
    const [passwordInput, setPasswordInput] = useState('');


    const [userInfo, setUserInfo] = useState(null);
    const [isAddFriendModalOpen, setIsAddFriendModalOpen] = useState(false);

    const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);

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
        const loadChatRooms = async () => {
            setLoading(true);

            try {
                let response;

                if (activeTab === 'my') {
                    response = await getMyChatRooms(currentPage, 20, 'joinedAt,desc');
                } else {
                    response = await getChatRooms(currentPage, 20, 'createdAt,desc');
                }

                const { content, totalPages } = response;

                setChatRooms(content);
                setTotalPages(totalPages);
            } catch (err) {
                console.error('API 호출 에러:', err);
                setError('채팅방 목록을 가져오는 데 실패했습니다.');
            } finally {
                setLoading(false);
            }
        };

        loadChatRooms().catch(err => {
            console.error('Unhandled error in loadChatRooms:', err);
        });
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

    const handleCreateRoom = async (chatRoom) => {
        try {
            const response = await createChatRoom(chatRoom);

            alert(`${response.name} 채팅방이 생성되었습니다!`);
            setIsModalOpen(false);
            setCurrentPage(0);
        } catch (error) {
            console.error('채팅방 생성 오류:', error);
            alert('채팅방 생성 실패');
        }
    };



    const handleEnterRoom = (chatRoom) => {
        window.open(`/chats/${chatRoom.id}`, '_blank');
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
                                onClick={() => setIsMember && setIsModalOpen(true)}
                            />
                            <img
                                className={styles.searchChatRoom}
                                alt="채팅방검색"
                                src="/search.png"
                                onClick={() => setIsSearchModalOpen(true)}
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
                    </div>

                    <ul className={styles.chatRoomList}>
                        {chatRooms.map((chatRoom, id) => (
                            <li key={id} className={styles.chatRoom}>
                                <p className={styles.chatRoomName}>{chatRoom.name}</p>
                                <button className={styles.enterChatRoomButton}
                                        onClick={() => handleEnterRoom(chatRoom)}>입장 >
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

            <SearchChatRoomModal
                isOpen={isSearchModalOpen}
                onClose={() => setIsSearchModalOpen(false)}
            />

        </div>
    );
};

export default MainPage;