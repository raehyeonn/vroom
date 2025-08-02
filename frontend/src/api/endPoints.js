const BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const API_ENDPOINTS = {
    followMember: (targetNickname) => `${BASE_URL}/api/members/${targetNickname}/follow`,
    unfollowMember: (targetNickname) => `${BASE_URL}/api/members/${targetNickname}/follow`,
    getFollowers: () => `${BASE_URL}/api/members/me/followers`,
    getFollowing: () => `${BASE_URL}/api/members/me/following`,
    removeFollower: (targetNickname) => `${BASE_URL}/api/members/me/followers/${targetNickname}`
};