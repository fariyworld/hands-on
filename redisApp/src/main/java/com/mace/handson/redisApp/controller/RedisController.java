package com.mace.handson.redisApp.controller;

import com.mace.handson.redisApp.config.RedisKeyProperties;
import com.mace.handson.redisApp.entity.User;
import com.mace.handson.redisApp.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * description:
 * <br />
 * Created by mace on 15:24 2018/7/27.
 */
@RestController
public class RedisController {

    @Autowired
    private IRedisService iRedisService;

    // http://127.0.0.1:8088/redisApp/increment.do?id=1&hashKey=love&count=1
    @GetMapping("increment.do")
    public String testIncrement(String id, String hashKey, Long count){

        Long current_val = iRedisService.updateCount(RedisKeyProperties.Count.PRODUCT + id,hashKey, count);

        if(Objects.isNull(current_val)){
            return "error";
        }

        return Long.toString(current_val);
    }

    // http://127.0.0.1:8088/redisApp/test.do?key=test:stringkey&count=1
    @GetMapping("test.do")
    public String test(String key, Long count){

        Long current_val = iRedisService.updateCount(key, count);

        if(Objects.isNull(current_val)){
            return "error";
        }

        return Long.toString(current_val);
    }

    // http://127.0.0.1:8088/redisApp/testStroeSocialRelationships.do?by_follower=1&follower=2
    @GetMapping("testStroeSocialRelationships.do")
    public boolean testStroeSocialRelationships(String by_follower, String follower){

        String zkey = RedisKeyProperties.StoreSocialRelationships.MACE
                + by_follower
                + RedisKeyProperties.StoreSocialRelationships.FOLLOW;

        Double score = Double.parseDouble(String.valueOf(System.currentTimeMillis()));
        return iRedisService.stoerSocialRelationships(zkey, follower, score);
    }

    // http://127.0.0.1:8088/redisApp/testGetMutualFollowers.do?userId1=1&userId2=2&destkey=sum_friends
    @GetMapping("testGetMutualFollowers.do")
    public String testGetMutualFollowers(String userId1, String userId2, String destkey){

        String zkey1 = RedisKeyProperties.StoreSocialRelationships.MACE
                + userId1
                + RedisKeyProperties.StoreSocialRelationships.FOLLOW;

        String zkey2 = RedisKeyProperties.StoreSocialRelationships.MACE
                + userId2
                + RedisKeyProperties.StoreSocialRelationships.FOLLOW;

        if(iRedisService.getMutualFollowers(zkey1, zkey2, destkey))
            return "success";
        else
            return "error";
    }

    @GetMapping("queryByUserId.do/{userId}")
    public User queryByUserId(@PathVariable Integer userId){

        return iRedisService.selectUserById(userId);
    }

}
