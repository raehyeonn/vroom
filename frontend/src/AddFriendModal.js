import React, { useState } from 'react';
import axios from 'axios';
import styles from './AddFriendModal.module.css';

const AddFriendModal = ({isOpen, onClose}) => {
    const [nickname, setNickname] = useState('');
    const [result, setResult] = useState(null);
    const [errorMessage, setErrorMessage] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleSearch = async () => {
        const accessToken = localStorage.getItem('accessToken');

        if(!nickname) {
            setResult(null);
            setErrorMessage('닉네임을 입력해주세요.');
            return;
        }

        setIsLoading(true);
        setErrorMessage('');
        setResult(null);

        try {
            const response = await axios.get(`http://localhost:8080/api/members/search`, {
                headers: {
                    'Authorization': `Bearer ${accessToken}`
                },
                params: {
                    nickname: nickname
                },
                withCredentials: true
            });

            const data = response.data;

            if(data && data.nickname) {
                setResult([data]);
                setErrorMessage('');
            } else {
                setResult([]);
            }
        } catch (error) {
            console.error('검색 실패: ', error);
            setResult([]);

            if(error.response?.status === 401) {
                setErrorMessage('로그인이 필요합니다.');
            } else {
                setErrorMessage('검색 중 오류가 발생했습니다. 다시 시도해주세요.');
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    const handleFollow = async (nicknameToFollow) => {
        const accessToken = localStorage.getItem('accessToken');

        try {
            await axios.post(`http://localhost:8080/api/follow`,
                { nickname: nicknameToFollow },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                    withCredentials: true
                });

            setResult(prev => prev.map(
                user => user.nickname === nicknameToFollow ? {...user, isFollowing: true} : user
            ));
        } catch (error) {
            setErrorMessage('팔로우 중 오류가 발생했습니다.');
        }

    }

    const resetModal = () => {
        setNickname('');
        setResult(null);
        setErrorMessage('');
        setIsLoading(false);
    };

    const handleClose = () => {
        resetModal();
        onClose();
    };

    if(!isOpen) {
        return null;
    }

    return (
        <div className={styles.modalOverlay}>
            <div className={styles.modalContent}>
                <button className={styles.closeButton} onClick={handleClose}>X
                </button>

                <h2>친구 검색</h2>

                <div className={styles.inputGroup}>
                    <input
                        type="text"
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="닉네임을 입력하세요"
                        disabled={isLoading}
                    />
                    <button
                        onClick={handleSearch}
                        disabled={isLoading || !nickname.trim()}
                    >
                        {isLoading ? '검색 중...' : '검색'}
                    </button>
                </div>

                <div className={styles.resultBox}>
                    {errorMessage && <p
                        className={styles.errorMessage}>{errorMessage}</p>}

                    {result && result.length > 0 && (
                        <ul className={styles.resultList}>
                            {result.map((user, index) => (
                                <li key={index} className={styles.resultItem}>
                                    <div className={styles.userInfo}>
                                        <span className={styles.nickname}>{user.nickname}</span>
                                        {user.isFollowing
                                            ? (<button className={styles.followingButton} disabled>팔로잉</button>)
                                            : (<button className={styles.followButton} onClick={() => handleFollow(user.nickname)}>팔로우</button>)
                                        }

                                    </div>
                                </li>
                            ))}
                        </ul>
                    )}

                    {result && result.length === 0 && !errorMessage && (
                        <p className={styles.noResult}>검색 결과가 없습니다.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default AddFriendModal;