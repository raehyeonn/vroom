import { useNavigate } from "react-router-dom";
import axios from 'axios'
import styles from './ChatRoomDrawer.module.css';
import ChangeRoomNameModal from './ChangeRoomNameModal';

const ChatRoomDrawer = ({chatRoomId, isDrawerOpen, setIsDrawerOpen, drawerView, setDrawerView, newRoomName, setNewRoomName, changeRoomName}) => {
    const navigate = useNavigate();

    const handleExitChatRoom = async () => {
        const accessToken = localStorage.getItem('accessToken');

        try {
            await axios.post(`http://localhost:8080/api/chat-rooms/${chatRoomId}/exit`, {},
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`
                    },
                    withCredentials: true
                });

            alert("채팅방을 나갔습니다.");
            navigate('/');
        } catch (error) {
            alert('오류 발생');
        }
    }

    if(!isDrawerOpen) {
        return null;
    }

    return (
        <div className={styles.drawer}>
            <button className={styles.closeButton} onClick={() => setIsDrawerOpen(false)}>닫기</button>

            {drawerView === 'menu' && (
                <div>
                    <button className={styles.drawerButton} onClick={() => setDrawerView('rename')}>방 이름 변경</button>
                    <button className={styles.drawerButton} onClick={() => setDrawerView('participants')}>참여자 보기</button>
                    <button className={styles.drawerButton} onClick={() => handleExitChatRoom()}>채팅방 나가기</button>
                </div>
            )}

            {drawerView === 'rename' && (
                <div>
                    <button className={styles.backButton} onClick={() => setDrawerView('menu')}>뒤로가기</button>
                    <ChangeRoomNameModal
                        isOpen={true}
                        onClose={() => setDrawerView('menu')}
                        onConfirm={changeRoomName}
                        value={newRoomName}
                        onChange={setNewRoomName}
                    />
                </div>
            )}

            {drawerView === 'participants' && (
                <div>
                    <button onClick={() => setDrawerView('menu')}>뒤로가기</button>
                    <h3 className={styles.heading}>참여자 목록</h3>
                </div>
            )}
        </div>
    );
};

export default ChatRoomDrawer;