import React from 'react';
import styles from './ChangeRoomNameModal.module.css';

const ChangeRoomNameModal = ({ isOpen, onClose, onConfirm, value, onChange }) => {
    if (!isOpen) return null;

    return (
        <div className={styles.modalBackdrop}>
            <div className={styles.modal}>
                <h2>방 제목 변경</h2>
                <input
                    type="text"
                    value={value}
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

export default ChangeRoomNameModal;