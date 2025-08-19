import React from 'react';
import styles from './RenameChatRoomModal.module.css';

const RenameChatRoomModal = ({ isOpen, onClose, onConfirm, value, onChange }) => {
    if (!isOpen) return null;

    return (
        <div className={styles.modalBackdrop}>
            <div className={styles.modal}>
                <h2>방 제목 변경</h2>
                <input
                    type="text"
                    value={value}
                    // 사용자가 입력을 하면 이벤트 발생
                    // e는 이벤트 발생시 브라우저가 자동 생성
                    // 여기 안에는 이벤트에 관련한 정보 있음
                    // e.target은 이벤트가 발생한 HTML요소
                    // e.target.value는 그 요소의 값
                    onChange={(e) => onChange(e.target.value)}
                    placeholder="새 방 제목 입력"
                />
                <div className={styles.modalButtons}>
                    <button onClick={() => onConfirm(value)}>변경</button>
                    <button onClick={onClose}>취소</button>
                </div>
            </div>
        </div>
    );
};

export default RenameChatRoomModal;