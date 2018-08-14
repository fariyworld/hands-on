package com.mace.handson.redisApp.config;

import org.springframework.data.redis.cache.CacheKeyPrefix;

/**
 * description:
 * <br />
 * Created by mace on 9:02 2018/7/31.
 */
public class CustomRedisPrefix implements CacheKeyPrefix {

    private final String delimiter;

    public CustomRedisPrefix() {
        this(null);
    }

    public CustomRedisPrefix(String delimiter) {
        this.delimiter = delimiter;
    }

    // cache:user:com.mace.handson.redisApp.service.impl.RedisServiceImplselectUserById1
    @Override
    public String compute(String cacheName) {

        return this.delimiter != null ? this.delimiter.concat(":").concat(cacheName).concat(":") : cacheName.concat(":");
    }
}
