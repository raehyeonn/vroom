import React, { useEffect, useState } from 'react';
import { useSearchParams } from "react-router-dom";
import styles from './FollowerList.module.css';
import {
    getFollowers,
    getFollowing,
    followMember as followMemberAPI,
    unfollowMember as unfollowMemberAPI,
    removeFollower as removeFollowerAPI
} from "./api/followApi";

const FollowerList = () => {
    const [searchParams,setSearchParams] = useSearchParams();
    const defaultTab = searchParams.get('tab') || 'followers';

    const [activeTab, setActiveTab] = useState(defaultTab);
    const [followers, setFollowers] = useState(null);
    const [following, setFollowing] = useState(null);
    const [loading, setLoading] = useState(true);

    const fetchList = async (type) => {
        try {
            if(type === "followers") {
                const data = await getFollowers();
                setFollowers(data);
            } else {
                const data = await getFollowing();
                setFollowing(data);
            }
        } catch (error) {
            console.error(`${type} 목록 조회 실패: `, error);
            alert("목록을 불러오는데 실패했습니다.");
        }
    };

    const followMember = async (targetNickname) => {
        try {
            await followMemberAPI(targetNickname);

            setFollowers((prev) => prev.map((member) => member.nickname === targetNickname
                    ? {...member, followedByMe: true}
                    : member
                )
            );

            if(following !== null && !following.find(member => member.nickname === targetNickname)) {
                const addMember = followers.find(member => member.nickname === targetNickname);
                if(addMember) {
                    setFollowing((prev) => [...prev, {...addMember, followedByMe: true}]);
                }
            }

        } catch (error) {
            console.error("팔로우 실패:", error);
            alert("팔로우에 실패했습니다. 다시 시도해 주세요.");
        }
    };

    const unfollowMember = async (targetNickname) => {
        try {
            await unfollowMemberAPI(targetNickname);

            setFollowing((prevFollowing) => prevFollowing.filter(member => member.nickname !== targetNickname));

            setFollowers((prev) => prev.map(member => member.nickname === targetNickname
                    ? {...member, followedByMe: false}
                    : member
                )
            );
        } catch (error) {
            console.error("언팔로우 실패:", error);
            alert("언팔로우에 실패했습니다. 다시 시도해 주세요.");
        }
    };

    const removeFollower = async (targetNickname) => {
        try {
            await removeFollowerAPI(targetNickname);

            setFollowers((prev) => prev.filter(member => member.nickname !== targetNickname));
            setFollowing(prev => prev ? prev.filter(member => member.nickname !== targetNickname) : prev);
        } catch (error) {
            console.error("팔로워 삭제 실패:", error);
            alert("팔로워 삭제에 실패했습니다. 다시 시도해 주세요.");
        }
    };

    useEffect(() => {
        setSearchParams({tab: activeTab});

        // 활성화된 탭이 followers 이며, 저장된 followers가 없다면 로딩중으로 바꾸고 함수 호출
        if (activeTab === 'followers' && followers === null) {
            setLoading(true);
            fetchList('followers').finally(() => setLoading(false));
        } else if (activeTab === 'following' && following === null) {
            setLoading(true);
            fetchList('following').finally(() => setLoading(false));
        } else {
            setLoading(false);
        }
    }, [activeTab]);

    if (loading) return <div>로딩 중...</div>;

    const data = activeTab === 'followers' ? followers : following;

    return (
        <div className={styles.container}>
            <div className={styles.tabButtons}>
                <button
                    onClick={() => setActiveTab('followers')}
                    className={`${styles.tabButton} ${activeTab === 'followers' ? styles.activeTab : ''}`}
                >
                    팔로워
                </button>
                <button
                    onClick={() => setActiveTab('following')}
                    className={`${styles.tabButton} ${activeTab === 'following' ? styles.activeTab : ''}`}
                >
                    팔로잉
                </button>
            </div>

            <h2>{activeTab === 'followers' ? '팔로워' : '팔로잉'}</h2>

            {loading ? (
                <p>로딩 중...</p>
            ) : !data || data.length === 0 ? (
                <p>표시할 내용이 없습니다.</p>
            ) : (
                <ul className={styles.list}>
                    {data.map((member) => (
                        <li className={styles.listItem} key={member.memberId} >
                            <span>{member.nickname}</span>
                            {activeTab === 'following' && (
                                <button className={styles.unfollowButton} onClick={() => unfollowMember(member.nickname)}>언팔로우</button>
                            )}
                            {activeTab === 'followers' && (
                                <div>
                                    { !member.followedByMe && (
                                        <button className={styles.followButton} onClick={() => followMember(member.nickname)}>맞팔로우</button>
                                    )}
                                    <img src="/x.png"
                                         alt="팔로워 삭제"
                                         className={styles.removeFollower}
                                         onClick={() => removeFollower(member.nickname)}/>
                                </div>
                            )}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default FollowerList;