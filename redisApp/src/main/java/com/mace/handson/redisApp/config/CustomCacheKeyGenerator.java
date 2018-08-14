package com.mace.handson.redisApp.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * description:
 * <br />
 * Created by mace on 14:14 2018/7/30.
 */
@Component("customCacheKeyGenerator")
public class CustomCacheKeyGenerator implements KeyGenerator {


    /**
     * description: cache: 前缀
     *              user:  缓存空间
     *              com.mace.handson.redisApp.service.impl.RedisServiceImpl: 类名
     *              selectUserById(java.lang.Integer): 方法名及参数类型
     *              [1] 参数值
     * 前缀:缓存空间:类名:方法名及参数类型:参数值
     * <br /><br />
     * create by mace on 2018/7/30 14:15.
     * @param target                调用方法类的实例
     * @param method                调用的方法
     * @param params                调用方法传入的参数
     * @return: java.lang.Object    自定义生成的 key 对象
     */
    @Override
    public Object generate(Object target, Method method, Object... params) {

        return new CustomCacheKey(target, method, params).toString();
    }
}
