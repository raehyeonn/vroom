import axios from 'axios';
import { API_ENDPOINTS } from "./endPoints";
import { getAuthHeader } from "../utils/auth";

export const createChatRoom = async (chatRoom) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.post(
        API_ENDPOINTS.createChatRoom(),
        {
            name: chatRoom.name,
            hidden: chatRoom.hidden,
            passwordRequired: chatRoom.passwordRequired,
            password: chatRoom.password
        },
        {
            headers,
            withCredentials: true
        }
    );

    return response.data;
};

export const getChatRooms = async (page = 0, size = 20, sort = 'createdAt,desc') => {
    const response = await axios.get(
        API_ENDPOINTS.getChatRooms(),
        {
            params: {page, size, sort}
        }
    );

    return response.data;
};

export const getChatRoomByCode = async (code) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.get(
        API_ENDPOINTS.getChatRoomByCode(),
        {
            headers,
            withCredentials: true,
            params: {code}
        }
    );

    return response.data;
};

export const getChatRoomPasswordRequired = async (chatRoomId) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.get(
        API_ENDPOINTS.getChatRoomPasswordRequired(chatRoomId),
        {
            headers,
            withCredentials:true
        }
    );

    return response.data;
};

export const getChatRoomDetail = async (chatRoomId) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.get(
        API_ENDPOINTS.getChatRoomDetail(chatRoomId),
        {
            headers,
            withCredentials: true
        }
    );

    return response.data;
};

export const updateChatRoomName = async (chatRoomId, newName) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.patch(
        API_ENDPOINTS.updateChatRoomName(chatRoomId),
        {
            name: newName
        },
        {
            headers,
            withCredentials: true
        }
    );

    return response.data.name;
};


export const joinChatRoom = async (chatRoomId, password = null) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const request = password ? {password: password} : {password: null};

    const response = await axios.post(
        API_ENDPOINTS.joinChatRoom(chatRoomId),
        request,
        {
            headers,
            withCredentials: true
        }
    );

    return response.data.success;
};