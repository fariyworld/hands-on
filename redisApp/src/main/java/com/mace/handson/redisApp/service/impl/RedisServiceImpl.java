package com.mace.handson.redisApp.service.impl;

import com.mace.handson.redisApp.config.RedisKeyProperties;
import com.mace.handson.redisApp.dao.UserMapper;
import com.mace.handson.redisApp.entity.User;
import com.mace.handson.redisApp.service.IRedisService;
import com.mace.handson.redisApp.util.NumberArithmeticUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * description:
 * <br />
 * Created by mace on 14:46 2018/7/27.
 */
@Service("iRedisService")
@Slf4j
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // TODO Redis应用 -- 各种计数
    // 1. 数据存储使用 hash类型  hashkey为 维度

    @Override
    public Long updateCount(String key, String hashKey, Long count) {

        try {
            return redisTemplate.opsForHash().increment(key, hashKey, count);
        } catch (Exception e) {
            log.warn("updateCount(String key, String hashKey, Long count) > {}", e.getMessage());
            log.warn("key : [{}], hashKey : [{}]", key, hashKey);
            log.warn("您可能对储存字符串值的域 field 执行了 HINCRBY 命令");
            return null;
        }
    }

    @Override
    public Long updateCount(String key, Long count) {

        try {
            return redisTemplate.opsForValue().increment(key, count);
        } catch (Exception e) {
            log.warn("updateCount(String key, Long count) > {}", e.getMessage());
            log.warn("key : [{}]", key);
            log.warn("您可能对储存字符串值的域 field 执行了 HINCRBY 命令");
            return null;
        }
    }


    // TODO Redis应用 -- 存储社交关系
    /*
     * 将用户的好友/粉丝/关注，可以存在一个sorted set中，score可以是timestamp，
     * 这样求两个人的共同好友的操作，可能就只需要用求交集命令即可。
     */
    @Override
    public boolean stoerSocialRelationships(String zkey, String userId, Double score) {

        try {
            return redisTemplate.opsForZSet().add(zkey, userId, score.doubleValue());
        } catch (Exception e) {
            log.warn("stoerSocialRelationships(String zkey, String userId, Double score) > {}", e.getMessage());
            log.warn("key : [{}]", zkey);
            log.warn("key 存在但不是有序集类型");
            return false;
        }
    }

    @Override
    public boolean getMutualFollowers(String zkey1, String zkey2, String destkey) {

        try {
            Long sum = redisTemplate.opsForZSet().intersectAndStore(zkey1, zkey2, destkey);

            if (sum > 0) {
                // 有共同关注
                Set<Object> mutualFollowers = redisTemplate.opsForZSet().range(destkey, 0, -1);
                StringBuilder userIds = new StringBuilder();
                int index = 0;
                for (Object mutualFollower : mutualFollowers) {
                    userIds.append(mutualFollower);
                    index++;
                    if (index != sum) {
                        userIds.append(",");
                    }
                }
                log.info("{} 和 {} 的有 {} 个共同好友， 明细：[{}]", zkey1, zkey2, sum, userIds.toString());
                // 更改过期时间 or 删除 destkey 默认过期时间为 -1
                // 1秒后过期
                redisTemplate.boundValueOps(destkey).expire(1, TimeUnit.SECONDS);
                log.info("1秒后过期： [{}]", destkey);
            } else {
                log.info("{} 和 {} 无共同好友", zkey1, zkey2);
            }
            return true;
        } catch (Exception e) {
            log.warn("getMutualFollowers(String zkey1, String zkey2, String destkey) > {}", e.getMessage());
            log.warn("zkey1 : [{}] ， zkey2 : [{}]", zkey1, zkey2);
            return false;
        }
    }


    // TODO Redis应用 -- 用作缓存代替memcached
    /*
     * 商品列表，评论列表，提示列表 等等
     * Service层应用缓存（注解方式）
     */
    // 逻辑： 第一次访问的时候先从数据库读取数据，然后将数据写入到缓存，再次访问同一内容的时候就从缓存中读取，如果缓存中没有则从数据库中读取
    // 缓存同步原理：增加删除修改都会涉及到同步问题，当发生除查询以外的操作时，将redis中的key进行删除

    @Autowired
    private UserMapper userMapper;


    /**
     * description: @Cacheable 应用到读取数据的方法上，先从缓存中读取，如果没有再从DB获取数据，然后把数据添加到缓存中
     * unless 表示条件表达式成立的话不放入缓存 在函数被调用之后才做判断的
     * ：@CachePut 应用到写数据的方法上，如新增/修改方法，调用方法时会自动把相应的数据放入缓存
     * ：@CacheEvict 应用到删除数据的方法上，调用方法时会从缓存中删除对应key的数据
     * ：@CacheConfig：主要用于配置该类中会用到的一些共用的缓存配置
     * <br /><br />
     * create by mace on 2018/7/31 11:06.
     *
     * @param userId
     * @return: com.mace.handson.redisApp.entity.User
     */
    @Cacheable(cacheNames = "user", // value、cacheNames 两个等同的参数, 作为value的别名
            /*key = "", // 缓存对象存储在Map集合中的key值，非必需，缺省按照函数的所有参数组合作为key值*/
            keyGenerator = "customCacheKeyGenerator", // 用于指定key生成器, 该参数与key是互斥的 keyGenerator
            condition = "#userId ne null",// 缓存对象的条件
            unless = "#result eq null")
    @Override
    public User selectUserById(Integer userId) {

        return userMapper.selectByPrimaryKey(userId);
    }


    // TODO Redis应用 -- 反spam系统
    /*
     * 评论，发布商品，论坛发贴 等等 sorted set
     * 网站被各种spam攻击是少不免（垃圾评论、发布垃圾商品、广告、刷自家商品排名等），针对这些spam制定一系列anti-spam规则，
     * 其中有些规则可以利用redis做实时分析，譬如：1分钟评论不得超过2次、5分钟评论少于5次等（更多机制/规则需要结合drools ）。
     * 采用sorted set将最近一天用户操作记录起来（为什么不全部记录？节省memory，全部操作会记录到log，后续利用hadoop进行更全面分析统计）
     */

    public void antiSpamSystem(String key, String value) {

        if (!redisTemplate.hasKey(key)) {
            // 第一次
            redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
            // 失效时间
            redisTemplate.boundValueOps(key).expire(1, TimeUnit.DAYS);
        } else {
            // 按照规则获取记录
            Set<Object> result = redisTemplate.opsForZSet().rangeByScore(key, System.currentTimeMillis() - 60 * 1000, System.currentTimeMillis());
            if (result.size() == 2) {
                log.warn("1分钟内评论不能超过两次, key: {}", key);
            } else {
                // 第二次
                redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
                // 失效时间
                redisTemplate.boundValueOps(key).expire(1, TimeUnit.DAYS);
            }
        }
    }


    // TODO Redis应用 -- 最新列表
    /*
     * 用户刚刚喜欢的商品, 最新菜单
     * List sorted set score为时间戳
     * 储存用户浏览的商品:
     *       用户在打开详情页的时候,以用户ID作key,商品的ID做值,以List存入redi缓存中
     *          在加入添加缓存之前,为了保证浏览商品的 唯一性,每次添加前,使用lrem将缓存的list中该商品ID去掉,在加入,以保证其浏览的最新的商品在最前面;
     *          将value push 到该key下的list中
     *          在lpush到redis的List中之后,根据产品需求还需要将该list的前n个数据之外的缓存修剪掉
     *          最后添加缓存失效时间;
     *
     * 获取用户最近浏览的商品列表:
     *      根据用户的ID及当前的页数和每页的个数,来获取商品缓存
     */
    public void cacheUserRecentProduct(Long userId, Long productId) {

        String key = RedisKeyProperties.LatestList.USER + userId;

        // 1.为了保证浏览商品的 唯一性,每次添加前,使用lrem将缓存的list中该商品ID去掉,在加入,以保证其浏览的最新的商品在最前面
        // 移除key中值为value的i个,返回删除的个数；如果没有这个元素则返回0
        redisTemplate.opsForList().remove(key, 1, productId);

        // 2.将value push 到该key下的list中
        redisTemplate.opsForList().leftPush(key, productId);

        // 3.根据产品需求还需要将该list的前n个数据之外的缓存修剪掉 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
        int n = 0;
        redisTemplate.opsForList().trim(key, 0, n);

        // 4.设置缓存失效时间
        redisTemplate.boundValueOps(key).expire(30, TimeUnit.DAYS);
    }

    public Map<String, Object> queryUserRecentProductIds2Cache(Long userId, int page, int pageSize) {

        String key = RedisKeyProperties.LatestList.USER + userId;

        // 1.获取用户的浏览的商品的总页数;
        Long total = redisTemplate.opsForList().size(key);
        Long pageCount = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;

        // 2.根据用户的ID分頁获取该用户最近浏览的商品信息 从 0开始 检索
        List<Object> result = redisTemplate.opsForList().range(key, (page - 1) * pageSize, page * pageSize - 1);

        // 拼装返回
        Map<String, Object> map = new HashMap<>();
        map.put("result", result);
        map.put("pageCount", pageCount);
        return map;

    }


    // TODO Redis应用 -- 排行榜
    /*
     * sorted set
     */

    // 初始化排行榜
    public <V> void initLeaderboard(String key, Map<V, Double> infoMap) {

        redisTemplate.opsForZSet().add(key, assembleTypedTupleSet(infoMap));
    }

    public <V> void initLeaderboard(String key, List<Double> scoreList, List<V> valueList) {

        redisTemplate.opsForZSet().add(key, assembleTypedTupleSet(scoreList, valueList));
    }

    // 更新某个 value 的分数
    public <V> void updateScoreForValue(String key, V value, Double incrementScore) {

        redisTemplate.opsForZSet().incrementScore(key, value, incrementScore);
    }


    // 查询完整排行榜、Top10 降序
    public Set<ZSetOperations.TypedTuple<Object>> queryLeaderboardWithScores(String key) {

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
    }

    public Set<ZSetOperations.TypedTuple<Object>> queryLeaderboardTop10WithScores(String key) {

        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 9);
    }

    public Set<Object> queryLeaderboard(String key) {

        return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }

    public Set<Object> queryLeaderboardTop10(String key) {

        return redisTemplate.opsForZSet().reverseRange(key, 0, 9);
    }

    // 查询某个 value 的排名 从高到低 0：第一
    public <V> Long queryRankingByValue(String key, V value) {

        return redisTemplate.opsForZSet().reverseRank(key, value);
    }


    // 查询某个 value 的分数
    public <V> Double queryScoreByValue(String key, V value) {

        return redisTemplate.opsForZSet().score(key, value);
    }

    // 查询排行榜基数（统计有多少value）
    public Long queryCardOfLeaderboard(String key) {

        return redisTemplate.opsForZSet().size(key);
    }

    // 查询排行榜总分数
    // 查询完整排行榜 然后计算总分数
    // 或者 将总分数单独存储
    public Double queryTotalScoreOfLeaderboard(String key) {

        Set<ZSetOperations.TypedTuple<Object>> leaderboardWithScores = queryLeaderboardWithScores(key);

        Double totalScore = 0d;

        for (ZSetOperations.TypedTuple<Object> tuple : leaderboardWithScores) {

            totalScore = NumberArithmeticUtils.add(totalScore, tuple.getScore());
        }

        return totalScore;
    }

    // 距离上一名的分数差
    public <V> Double fetchGapFromPrevious(String key, V value) {

        // 1.获取自己的排名
        Long ranking = queryRankingByValue(key, value);

        // 2.获取自己与上一名的分数
        Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, ranking - 1, ranking);

        Double current = Double.MIN_VALUE;
        Double previous = Double.MIN_VALUE;

        // 3.计算分数
        for (ZSetOperations.TypedTuple<Object> tuple : tuples) {

            if (previous == Double.MIN_VALUE)
                previous = tuple.getScore();
            else
                current = tuple.getScore();
        }
        return NumberArithmeticUtils.sub(previous, current);
    }


    // scoreList 和 valueList 的index 一一对应
    public static <V> Set<ZSetOperations.TypedTuple<Object>> assembleTypedTupleSet(List<Double> scoreList, List<V> valueList) {

        if (CollectionUtils.isEmpty(scoreList) || CollectionUtils.isEmpty(valueList)) {
            log.warn("参数非法 scoreList: [{}], valueList: [{}] ", scoreList, valueList);
            return null;
        }
        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = new HashSet<>();
        for (int i = 0; i < scoreList.size(); i++) {
            typedTupleSet.add(new DefaultTypedTuple<>(valueList.get(i), scoreList.get(i)));
        }
        return typedTupleSet;
    }

    public static <V> Set<ZSetOperations.TypedTuple<Object>> assembleTypedTupleSet(Map<V, Double> infoMap) {

        if (CollectionUtils.isEmpty(infoMap)) {
            log.warn("参数非法 infoMap: [{}]", infoMap);
            return null;
        }

        Set<ZSetOperations.TypedTuple<Object>> typedTupleSet = new HashSet<>();

        Set<Map.Entry<V, Double>> entrySet = infoMap.entrySet();
        for (Map.Entry<V, Double> entry : entrySet) {
            typedTupleSet.add(new DefaultTypedTuple<>(entry.getKey(), entry.getValue()));
        }

        return typedTupleSet;
    }

    // TODO Redis应用 -- 消息通知
    /**
     * 采用Hash结构对消息通知业务场景计数(消息分类计数)
     * 消息内容 sorted set score为时间戳
     */


    // TODO Redis应用 -- 分布式锁
    /**
     * 为了确保分布式锁可用，我们至少要确保锁的实现同时满足以下四个条件：
     * 1.互斥性。在任意时刻，只有一个客户端能持有锁。
     * 2.不会发生死锁。即使有一个客户端在持有锁的期间崩溃而没有主动解锁，也能保证后续其他客户端能加锁。
     * 3.具有容错性。只要大部分的Redis节点正常运行，客户端就可以加锁和解锁。
     * 4.加锁和解锁必须是同一个客户端，客户端自己不能把别人加的锁给解了。
     */

    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Autowired
    private JedisPool jedisPool;

    /**
     * description: 尝试获取分布式锁
     * <br /><br />
     * create by mace on 2018/8/1 14:12.
     *
     * @param jedis      Redis客户端
     * @param lockKey    锁
     * @param requestId  请求标识(知道这把锁是哪个请求加的)
     * @param expireTime 超期时间
     * @return: boolean 是否获取成功
     */
    public boolean tryGetDistributedLock(Jedis jedis, String lockKey, String requestId, int expireTime) {
//        redisTemplate.opsForValue().setIfAbsent(lockKey, requestId);

        String result = jedis.set(lockKey, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);

        return LOCK_SUCCESS.equals(result);
    }

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * description: 释放分布式锁
     * <br /><br />
     * create by mace on 2018/8/1 14:42.
     *
     * @param jedis     Redis客户端
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return: boolean 是否释放成功
     */
    public boolean releaseDistributedLock(Jedis jedis, String lockKey, String requestId) {

        // 首先获取锁对应的value值，检查是否与requestId相等，如果相等则删除锁（解锁）。
        // 确保上述操作是原子性的
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Object result = jedis.eval(script, Collections.singletonList(lockKey), Collections.singletonList(requestId));

        return RELEASE_SUCCESS.equals(result);
    }
/**
 *
 */

    // TODO redis web 应用 -- 实现登录 cookie 功能
    /**
     * 实现原理： 采用 hash 结构存储 登录 cookie 令牌与已登录用户的映射
     */



}