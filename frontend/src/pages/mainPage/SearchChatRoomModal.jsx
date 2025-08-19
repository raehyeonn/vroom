import React, {useState} from 'react';
import { getChatRoomByCode } from "../../api/chatRoomApi";
import styles from './SearchChatRoomModal.module.css';

const SearchChatRoomModal = ({isOpen, onClose}) => {
    const [code, setCode] = useState('');
    const [result, setResult] = useState(null);
    const [isLoading, setIsLoading] = useState(false);

    const handleSearch = async () => {
        if(!code.trim()) {
            setResult(null);
            return;
        }

        setResult(null);
        setIsLoading(true);

        try {
            const response = await getChatRoomByCode(code.trim());

            if (response) {
                setResult(response);
            } else {
                setResult(null);
            }
        } catch (error) {
            console.error('검색 실패: ', error);
            setResult(null);
        } finally {
            setIsLoading(false);
        }
    }

    const handleEnterRoom = (chatRoomId) => {
        window.open(`/chats/${chatRoomId}`, '_blank');
    }

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    const handleClose = () => {
        setCode('');
        setResult(null);
        setIsLoading(false);
        onClose();
    };


    if (!isOpen) return null;

    return (
        <div className={styles.modalOverlay}>
            <div className={styles.modalContent}>
                <h2 className={styles.modalTitle}>채팅방 검색</h2>

                <div className={styles.inputGroup}>
                    <input
                        type="text"
                        value={code}
                        onChange={(e) => setCode(e.target.value)}
                        onKeyDown={handleKeyDown}
                        placeholder="채팅방 코드를 입력해주세요."
                        className={styles.input}
                        disabled={isLoading}
                    />
                    <button
                        onClick={handleSearch}
                        disabled={isLoading || !code.trim()}
                        className={styles.searchButton}
                    >
                        {isLoading ? '검색 중' : '검색'}
                    </button>
                </div>

                <div className={styles.resultSection}>
                    {result ? (
                        <div className={styles.resultItem}>
                            <div className={styles.chatRoomInfo}>
                                <span className={styles.chatRoomName}>{result.name}</span>
                                <button
                                    className={styles.enterButton}
                                    onClick={() => handleEnterRoom(result.id)}
                                >
                                    입장
                                </button>
                            </div>
                        </div>
                    ) : (

                        <p className={styles.noResult}>일치하는 채팅방이 없습니다.</p>
                    )}
                </div>

                <button className={styles.closeButton} onClick={handleClose}>
                    닫기
                </button>
            </div>
        </div>
    );
}

export default SearchChatRoomModal;