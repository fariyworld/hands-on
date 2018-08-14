package com.mace.handson.redisApp.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * description:
 * <br />
 * Created by mace on 14:51 2018/7/27.
 */
@Configuration
@EnableCaching//启用缓存,这个注解很重要
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {

    // cache:user:com.mace.handson.redisApp.service.impl.RedisServiceImpl:selectUserById:1
    // 前缀:缓存空间:类名:方法名及参数类型:参数值
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName()+":");
            sb.append(method.getName()+":");
            for (int i = 0; i < params.length; i++) {
                sb.append(params[i].toString());
                if(i == params.length - 1)
                    continue;
                else
                    sb.append(",");
            }
            return sb.toString();
        };
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, Method method, Object... params) {
//                StringBuilder sb = new StringBuilder();
//                sb.append(target.getClass().getName());
//                sb.append(method.getName());
//                for (Object obj : params) {
//                    sb.append(obj.toString());
//                }
//                return sb.toString();
//            }
//        };
    }


    /**
     * description: 缓存管理器. <br />
     * 自定义CacheManager的值序列化方式为 Jackson2JsonRedisSerializer<br />
     * 允许基于每个缓存定义配置<br />
     * 可以自定义事务行为和预定义的缓存。<br />
     * <br /><br />
     * create by mace on 2018/7/27 14:52.
     * @param connectionFactory
     * @return: org.springframework.data.redis.cache.RedisCacheManager
     */

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        // 初始化一个无锁 RedisCacheWriter 无锁缓存可提高吞吐量
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory);

        // 缺少条目锁定可能导致putIfAbsent和clean方法的重叠非原子命令，因为那些需要将多个命令发送到Redis.
        // 锁定对应方通过设置显式锁定密钥并检查是否存在此密钥来防止命令重叠，这会导致其他请求和潜在的命令等待时间.
        // 可以选择锁定行为
//        RedisCacheWriter redisCacheWriter = RedisCacheWriter.lockingRedisCacheWriter(connectionFactory);

        // 设置CacheManager的值序列化方式为 Jackson2JsonRedisSerializer
        // 使用 Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = jackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair);

        // 设置CacheManager的值序列化方式为 FastJsonRedisSerializer
        // 使用 FastJsonRedisSerializer 替换默认序列化
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        RedisSerializationContext.SerializationPair<Object> pair_fastjson = RedisSerializationContext.SerializationPair.fromSerializer(fastJsonRedisSerializer);
        RedisCacheConfiguration defaultCacheConfig_fastjson = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(pair_fastjson);

        //设置默认超过期时间是30秒
        defaultCacheConfig_fastjson = defaultCacheConfig_fastjson
                .entryTtl(Duration.ofSeconds(60))   //设置缓存的默认过期时间，使用Duration设置
                .disableCachingNullValues()        //不缓存空值
                .computePrefixWith(new CustomRedisPrefix(RedisKeyProperties.Cache.REDIS_PREFIX));//自定义redis key前缀

        defaultCacheConfig = defaultCacheConfig
                .entryTtl(Duration.ofSeconds(60))   //设置缓存的默认过期时间，使用Duration设置
                .disableCachingNullValues()         //不缓存空值
                .computePrefixWith(new CustomRedisPrefix(RedisKeyProperties.Cache.REDIS_PREFIX));

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames =  new HashSet<>();
        cacheNames.add("user");
        cacheNames.add("default");
        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put("user", defaultCacheConfig_fastjson);
        cacheConfigurations.put("dept", defaultCacheConfig);

        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManager.RedisCacheManagerBuilder.fromCacheWriter(redisCacheWriter);
//        RedisCacheManager.RedisCacheManagerBuilder redisCacheManagerBuilder = RedisCacheManager.builder(connectionFactory);

        RedisCacheManager cacheManager = redisCacheManagerBuilder
                .initialCacheNames(cacheNames)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware() // 事务行为 默认没有
                .build();

        //初始化RedisCacheManager
//        RedisCacheManager cacheManager = new RedisCacheManager(redisCacheWriter, defaultCacheConfig_fastjson, "user");
//        cacheManager.setTransactionAware(true);// 事务行为 默认没有

        log.info("自定义 RedisCacheManager 加载完成");

        return cacheManager;
    }


//    @Bean
//    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//
//        log.info("初始化 redis 缓存管理器");
//        //使用Spring提供的默认配置
//        return RedisCacheManager.create(connectionFactory);
//    }

//    @Bean
//    public CacheManager customCacheManager(RedisConnectionFactory factory) {
//        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//
//        config = config
//                        .entryTtl(Duration.ofMinutes(1))        // 设置缓存的默认过期时间，使用Duration设置
//                        .disableCachingNullValues();            // 不缓存空值
//
//        // 设置一个初始化的缓存空间set集合
//        Set<String> cacheNames =  new HashSet<>();
//        cacheNames.add("my-redis-cache1");
//        cacheNames.add("my-redis-cache2");
//
//        // 对每个缓存空间应用不同的配置
//        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
//        configMap.put("my-redis-cache1", config);
//        configMap.put("my-redis-cache2", config.entryTtl(Duration.ofSeconds(120)).disableCachingNullValues());
//
//        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)     // 使用自定义的缓存配置初始化一个cacheManager
//                .initialCacheNames(cacheNames)  // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
//                .withInitialCacheConfigurations(configMap)
//                .build();
//
//        return cacheManager;
//    }


    /**
     * description: redis模板操作类,类似于jdbcTemplate的一个类;
     * <br />
     * 解决redis自动生成key，key乱码，为自定义key
     * <br /><br />
     * create by mace on 2018/7/27 14:52.
     * @param factory   通过Spring进行注入，参数在application.yml进行配置
     * @return: org.springframework.data.redis.core.RedisTemplate<java.lang.Object,java.lang.Object>
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = jackson2JsonRedisSerializer();

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.setDefaultSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setEnableDefaultSerializer(true);
        redisTemplate.afterPropertiesSet();

        log.info("装配 RedisTemplate 成功");

        return redisTemplate;
    }


    private Jackson2JsonRedisSerializer jackson2JsonRedisSerializer(){

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        return jackson2JsonRedisSerializer;
    }
}
