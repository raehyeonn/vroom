import React from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './SignupSuccessModal.module.css';



const Modal = ({ message, onClose }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate('/login');
    };

    return (
        <div className={styles.overlay}>
            <div className={styles.modal}>
                <p>{message}</p>
                <button onClick={handleClick}>로그인하러 가기</button>
            </div>
        </div>
    );
};

export default Modal;