import React, { useState } from 'react';
import styles from './CreateChatRoomModal.module.css';

const CreateChatRoomModal = ({ isOpen, onClose, onCreate }) => {
    const [roomTitle, setRoomTitle] = useState('');

    // 사용자가 입력한 채팅방 이름이 빈 문자열 또는 공백이 아닌 경우 채팅방을 생성한다.
    const handleCreate = () => {
        if (roomTitle.trim()) {
            onCreate(roomTitle);
            setRoomTitle('');
            onClose();
        } else {
            alert('채팅방 이름을 입력해주세요.');
        }
    };

    // isOpen이 false인 경우 모달을 렌더링 하지 않는다.
    if (!isOpen) {
        return null;
    }

    return (
        <div className={styles.createChatRoomModalOverlay}>
            <div className={styles.createChatRoomModal}>
                <p className={styles.modalTitle}>채팅방 만들기</p>

                <div className={styles.modalSettings}>
                    <input
                        className={styles.chatRoomTitleInput}
                        type="text"
                        placeholder="방 제목을 입력하세요"
                        value={roomTitle}
                        onChange={(e) => setRoomTitle(e.target.value)}
                    />
                </div>

                <div className={styles.modalActions}>
                    <button className={styles.cancelButton} onClick={onClose}>취소하기</button>

                    <button className={styles.createButton} onClick={handleCreate}>생성하기</button>
                </div>
            </div>
        </div>
    );
};

export default CreateChatRoomModal;