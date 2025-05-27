import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from './Login.module.css';

function LoginForm() {
    const [email, setEmail] = useState('');
    const [rawPassword, setRawPassword] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setError(null);

        try {
            const response = await axios.post(
                'http://localhost:8080/api/auth/login',
                { email, rawPassword },
                { withCredentials: true } // 🍪 쿠키 저장 허용
            );

            const authHeader = response.headers['authorization'];
            if (authHeader && authHeader.startsWith('Bearer ')) {
                const token = authHeader.substring(7); // 'Bearer ' 제거
                localStorage.setItem('accessToken', token); // 저장 (또는 recoil 등으로 상태관리해도 됨)
            }

            // 로그인 성공
            console.log('로그인 성공!', response.data); // LoginResponse(email, nickname, id 등)

            localStorage.setItem('memberId', response.data.memberId);

            // 필요하면 상태 저장하거나 리디렉트
            alert(`${response.data.email} 님 환영합니다!`);
            navigate('/'); // ← 채팅방 목록으로 이동
        } catch (err) {
            console.error(err);
            setError(err.response?.data?.message || '로그인 실패');
        }
    };

    return (
        <div className={styles.loginContainer}>
            <div className={styles.logo}>
                <a className={styles.logoLink} href={"/"}>VROOM</a>
            </div>

            <form className={styles.form} onSubmit={handleLogin}>
                <div className={styles.inputContainer}>
                    <div className={styles.inputSection}>
                        <span className={styles.inputLabel}>이메일 주소</span>
                        <input className={styles.input} type="email"
                               placeholder="이메일 주소를 입력해주세요."
                               value={email}
                               onChange={(e) => setEmail(e.target.value)}
                               required/>
                    </div>

                    <div className={styles.inputSection}>
                        <span className={styles.inputLabel}>비밀번호</span>
                        <input className={styles.input} type="password"
                               placeholder="비밀번호를 입력해주세요."
                               value={rawPassword}
                               onChange={(e) => setRawPassword(e.target.value)}
                               required/>
                    </div>
                </div>

                {error && <p style={{color: 'red'}}>{error}</p>}
                <button className={styles.button} type="submit">
                    <span className={styles.buttonText}>로그인</span>
                </button>
            </form>

            <p className={styles.joinGuide}>
                아직 계정이 없으신가요? <a className={styles.joinGuideLink}
                                 href="/join">회원가입</a>
            </p>
        </div>
    );
}

export default LoginForm;
