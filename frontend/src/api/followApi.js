import axios from 'axios';
import { API_ENDPOINTS } from "./endPoints";
import { getAuthHeader } from "../utils/auth";

export const getFollowers = async () => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.get(API_ENDPOINTS.getFollowers(), {
        headers,
        withCredentials: true,
        params: {
            page: 0,
            size: 20
        }
    });

    return response.data.content;
};

export const getFollowing = async () => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.get(API_ENDPOINTS.getFollowing(), {
        headers,
        withCredentials: true,
        params: {
            page: 0,
            size: 20
        }
    });

    return response.data.content;
};

export const followMember = async (targetNickname) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    return axios.post(API_ENDPOINTS.followMember(targetNickname), null, {
        headers,
        withCredentials: true
    });
};

export const unfollowMember = async (targetNickname) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    return axios.delete(API_ENDPOINTS.unfollowMember(targetNickname), {
        headers,
        withCredentials: true
    });
};

export const removeFollower = async (targetNickname) => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    return axios.delete(API_ENDPOINTS.removeFollower(targetNickname), {
        headers,
        withCredentials: true
    })
}