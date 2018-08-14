package com.mace.handson.redisApp.service;

import com.mace.handson.redisApp.entity.User;

/**
 * description:
 * <br />
 * Created by mace on 14:46 2018/7/27.
 */
public interface IRedisService {

    /**
     * description: 各种计数（喜欢数，评论数，鉴定数，浏览数）等等
     * <br /><br />
     * create by mace on 2018/7/27 15:17.
     * @param key
     * @param hashkey       维度
     * @param count         incrby 数
     * @return: Long        执行 HINCRBY 命令之后，哈希表 key 中域 field 的值
     */
    Long updateCount(String key, String hashkey, Long count);

    Long updateCount(String key, Long count);

    /**
     * description: 存储社交关系
     * 将用户的好友/粉丝/关注，可以存在一个sorted set中，score可以是timestamp，
     * 这样求两个人的共同好友的操作，可能就只需要用求交集命令即可
     * <br /><br />
     * create by mace on 2018/7/27 16:38.
     * @param zkey
     * @param userId
     * @param score
     * @return: boolean
     */
    boolean stoerSocialRelationships(String zkey, String userId, Double score);


    /**
     * description: 交集命令，获得共同关注
     * <br /><br />
     * create by mace on 2018/7/27 17:24.
     * @param zkey1
     * @param zkey2
     * @param destkey
     * @return: boolean
     */
    boolean getMutualFollowers(String zkey1, String zkey2, String destkey);



    User selectUserById(Integer userId);
}
