export const getAuthHeader = () => {
    const accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
        alert('로그인이 필요합니다.');
        return;
    }

    return {
        Authorization: `Bearer ${accessToken}`
    };
};