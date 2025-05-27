import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import styles from "./Join.module.css";
import SignupSuccessModal from './SignupSuccessModal';

const Join = () => {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        nickname: ''
    });

    const [errors, setErrors] = useState({});
    const [successMessage, setSuccessMessage] = useState('');

    // 유효성 검사 (필드별)
    const validateField = (name, value) => {
        let error = '';

        if (name === 'email' && !/\S+@\S+\.\S+/.test(value)) {
            error = '이메일 형식이 올바르지 않습니다.';
        }

        return error;
    };

    // 입력 변화 처리 및 실시간 검증
    const handleChange = (e) => {
        const { name, value } = e.target;

        setFormData(prev => ({ ...prev, [name]: value }));

        // 필드별 유효성 검사
        const fieldError = validateField(name, value);

        setErrors(prev => ({
            ...prev,
            [name]: fieldError
        }));

        // 비밀번호 일치 검사
        if ((name === 'password' || name === 'confirmPassword')) {
            const pw = name === 'password' ? value : formData.password;
            const confirm = name === 'confirmPassword' ? value : formData.confirmPassword;

            if (confirm && pw !== confirm) {
                setErrors(prev => ({
                    ...prev,
                    confirmPassword: '비밀번호가 일치하지 않습니다.'
                }));
            } else {
                setErrors(prev => ({
                    ...prev,
                    confirmPassword: ''
                }));
            }
        }
    };

    const validateAll = () => {
        const newErrors = {};

        if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = '이메일 형식이 올바르지 않습니다.';
        }

        if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = '비밀번호가 일치하지 않습니다.';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setSuccessMessage('');
        setErrors({});

        if (!validateAll()) return;

        try {
            const response = await axios.post('http://localhost:8080/api/members', {
                email: formData.email,
                password: formData.password,
                nickname: formData.nickname
            });

            setSuccessMessage(`${response.data.nickname}님, 회원가입이 완료되었습니다!`);
            setFormData({
                email: '',
                password: '',
                confirmPassword: '',
                nickname: ''
            });
        } catch (err) {
            setErrors({ server: err.response?.data?.message || '서버 오류가 발생했습니다.' });
        }
    };

    const handleCancel = () => {
        navigate(-1);
    };

    // 버튼 활성화 조건
    const isEmailValid = /\S+@\S+\.\S+/.test(formData.email);
    const isPasswordMatch = formData.password && formData.confirmPassword && formData.password === formData.confirmPassword;
    const isFormValid = isEmailValid && isPasswordMatch;

    return (
        <div className={styles.joinContainer}>
            <div className={styles.titleContainer}>
                <span className={styles.title}>회원가입</span>
            </div>

            <form className={styles.form} onSubmit={handleSubmit}>
                <div className={styles.inputContainer}>
                    <div className={styles.inputSection}>
                        <span className={styles.inputLabel}>이메일 주소</span>
                        <input className={styles.input} name="email" type="email" placeholder="이메일 주소를 입력해주세요."
                               value={formData.email} onChange={handleChange} required/>
                        {errors.email && <p
                            className={styles.errorText}>{errors.email}</p>}
                    </div>

                    <div className={styles.inputSection}>
                        <span className={styles.inputLabel}>비밀번호</span>
                        <input className={styles.input} name="password"
                               type="password" placeholder="비밀번호를 입력해주세요."
                               value={formData.password} onChange={handleChange}
                               required></input>
                    </div>

                    <div className={styles.inputSection}>
                        <span className={styles.inputLabel}>비밀번호 확인</span>
                        <input className={styles.input} name="confirmPassword"
                               type="password" placeholder="비밀번호를 다시 입력해주세요."
                               value={formData.confirmPassword}
                               onChange={handleChange} required></input>
                        {errors.confirmPassword && <p
                            className={styles.errorText}>{errors.confirmPassword}</p>}
                    </div>

                    <div className={styles.inputSection}>
                        <span className={styles.inputLabel}>닉네임</span>
                        <input className={styles.input} name="nickname"
                               type="text" placeholder="닉네임을 입력해주세요."
                               value={formData.nickname} onChange={handleChange}
                               required></input>
                    </div>
                </div>

                <div className={styles.buttonContainer}>
                    <button className={styles.cancelButton} type="button"
                            onClick={handleCancel}>취소하기
                    </button>
                    <button className={styles.submitButton} type="submit"
                            disabled={!isFormValid}>가입하기
                    </button>
                </div>

                {errors.server && <p
                    className={styles.errorText}>{errors.server}</p>}
            </form>

            {successMessage && (
                <SignupSuccessModal message={successMessage} />
            )}
        </div>


    );
};

export default Join;
