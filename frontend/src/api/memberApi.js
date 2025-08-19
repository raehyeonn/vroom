import axios from 'axios';
import { API_ENDPOINTS } from "./endPoints";
import { getAuthHeader } from "../utils/auth";

export const getMyChatRooms = async (page = 0, size = 20, sort = 'joinedAt,desc') => {
    const headers = getAuthHeader();

    if(!headers) {
        return;
    }

    const response = await axios.get(
        API_ENDPOINTS.getMyChatRooms(),
        {
            headers,
            withCredentials: true,
            params: {page, size, sort}
        }
    );

    return response.data;
};