const BASE_URL = process.env.REACT_APP_API_BASE_URL;

export const API_ENDPOINTS = {

    getMyChatRooms: () => `${BASE_URL}/api/members/me/chat-rooms`,

    createChatRoom: () => `${BASE_URL}/api/chat-rooms`,
    getChatRooms: () => `${BASE_URL}/api/chat-rooms`,
    getChatRoomByCode: () => `${BASE_URL}/api/chat-rooms/search`,
    getChatRoomDetail: (chatRoomId) => `${BASE_URL}/api/chat-rooms/${chatRoomId}`,
    getChatRoomPasswordRequired: (chatRoomId) => `${BASE_URL}/api/chat-rooms/${chatRoomId}/passwordRequired`,
    updateChatRoomName: (chatRoomId) => `${BASE_URL}/api/chat-rooms/${chatRoomId}/name`,
    joinChatRoom: (chatRoomId) => `${BASE_URL}/api/chat-rooms/${chatRoomId}/participants`,
    leaveChatRoom: (chatRoomId) => `${BASE_URL}/api/chat-rooms/${chatRoomId}/participants`,
    getChatRoomParticipants: (chatRoomId) => `${BASE_URL}/api/chat-rooms/${chatRoomId}/participants`,

    followMember: (targetNickname) => `${BASE_URL}/api/members/${targetNickname}/follow`,
    unfollowMember: (targetNickname) => `${BASE_URL}/api/members/${targetNickname}/follow`,
    getFollowers: () => `${BASE_URL}/api/members/me/followers`,
    getFollowing: () => `${BASE_URL}/api/members/me/following`,
    removeFollower: (targetNickname) => `${BASE_URL}/api/members/me/followers/${targetNickname}`

};