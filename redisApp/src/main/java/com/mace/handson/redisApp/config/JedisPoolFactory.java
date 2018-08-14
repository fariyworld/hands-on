package com.mace.handson.redisApp.config;

import com.mace.handson.redisApp.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * description: JedisPoolFactory类，用来配置JedisPool属性信息，以及创建RedisPool
 * <br />
 * Created by mace on 14:23 2018/8/1.
 */
@Configuration
public class JedisPoolFactory {

    //自动注入redis配置属性文件
    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisPool getJedisPool(){

        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxIdle(properties.getJedis().getPool().getMaxIdle());
        config.setMaxTotal(properties.getJedis().getPool().getMaxActive());
        config.setMaxWaitMillis(properties.getJedis().getPool().getMaxWait().toMillis());
        config.setTestOnBorrow(true);//在获取一个jedis实例时，是否提前进行验证操作；如果为true，则得到的jedis实例均是可用的
        config.setTestOnReturn(true);//在归还一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。

        JedisPool pool = new JedisPool(config, properties.getHost(), properties.getPort(), CommonUtils.long2int(properties.getTimeout().toMillis()), properties.getPassword(), 0);

        return pool;
    }

}
