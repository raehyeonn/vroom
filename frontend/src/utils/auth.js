export const getAuthHeader = () => {
    const accessToken = localStorage.getItem('accessToken');

    if (!accessToken) {
        return;
    }

    return {
        Authorization: `Bearer ${accessToken}`
    };
};