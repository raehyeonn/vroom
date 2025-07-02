import React from "react";
import styles from './VerifyChatRoomPasswordModal.module.css';

const VerifyChatRoomPasswordModal = ({ isOpen, onClose, onConfirm, password, setPassword }) => {
    if (!isOpen) return null;

    return (
        <div className={styles.modalBackdrop}>
            <div className={styles.modalContent}>
                <h3>비밀번호 입력</h3>
                <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="비밀번호를 입력하세요"
                />
                <button onClick={onConfirm}>확인</button>
                <button onClick={onClose}>취소</button>
            </div>
        </div>
    );
};

export default VerifyChatRoomPasswordModal;