package com.mace.handson.redisApp.config;

/**
 * description: redis key存储前缀
 * <br />
 * Created by mace on 16:01 2018/7/27.
 */
public class RedisKeyProperties {

    public static final String COMMON_PREFIX = "hands-on:redisApp:";

    public interface Count{

        String PRODUCT = COMMON_PREFIX + "count:product:";
    }

    public interface StoreSocialRelationships{

        String MACE = COMMON_PREFIX + "store_social_relationships:mace:user:";
        String FANS   = ":fans";         //粉丝
        String FOLLOW = ":follow";       //关注
        String FRIEND = ":friend";       //好友
    }

    public interface Cache{

        String REDIS_PREFIX = COMMON_PREFIX + "cache";
        // 缓存命名空间 cacheNames
    }


    public interface LatestList{

        String USER = COMMON_PREFIX + "latest_list:user:";
    }
}
