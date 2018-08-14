package com.mace.handson.redisApp.dao;

import com.mace.handson.redisApp.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * description:
 * <br />
 * Created by mace on 13:48 2018/7/30.
 */
@Mapper
public interface UserMapper{

    User selectByPrimaryKey(Integer id);
}
