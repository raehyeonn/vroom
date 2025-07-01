import React, { useState } from 'react';
import styles from './CreateChatRoomModal.module.css';

const CreateChatRoomModal = ({ isOpen, onClose, onCreate }) => {
    const [name, setName] = useState('');
    const [hidden, setHidden] = useState(false);
    const [passwordRequired, setPasswordRequired] = useState(false);
    const [password, setPassword] = useState('');

    const handleCreate = () => {
        if (!name.trim()) {
            alert('채팅방 이름을 입력해주세요.');
            return;
        }

        if (passwordRequired && !password.trim()) {
            alert('비밀번호를 입력해주세요.');
            return;
        }

        onCreate({
            name,
            hidden,
            passwordRequired,
            password: passwordRequired ? password : null,
        });

        // 초기화
        setName('');
        setHidden(false);
        setPasswordRequired(false);
        setPassword('');
        onClose();
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
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                    />

                    <div className={styles.radioGroup}>
                        <p>비밀방 여부</p>
                        <label>
                            <input
                                type="radio"
                                name="private"
                                value="false"
                                checked={!hidden}
                                onChange={() => setHidden(false)}
                            />
                            공개방
                        </label>
                        <label>
                            <input
                                type="radio"
                                name="private"
                                value="true"
                                checked={hidden}
                                onChange={() => setHidden(true)}
                            />
                            비공개방
                        </label>
                    </div>

                    <div className={styles.radioGroup}>
                        <p>비밀번호 설정</p>
                        <label>
                            <input
                                type="radio"
                                name="passwordRequired"
                                value="false"
                                checked={!passwordRequired}
                                onChange={() => setPasswordRequired(false)}
                            />
                            사용 안 함
                        </label>
                        <label>
                            <input
                                type="radio"
                                name="passwordRequired"
                                value="true"
                                checked={passwordRequired}
                                onChange={() => setPasswordRequired(true)}
                            />
                            사용함
                        </label>
                    </div>

                    <input
                        className={styles.chatRoomTitleInput}
                        type="password"
                        placeholder="비밀번호 입력"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        disabled={!passwordRequired}
                    />

                </div>

                <div className={styles.modalActions}>
                    <button className={styles.cancelButton}
                            onClick={onClose}>취소하기
                    </button>

                    <button className={styles.createButton}
                            onClick={handleCreate}>생성하기
                    </button>
                </div>
            </div>
        </div>
    );
};

export default CreateChatRoomModal;